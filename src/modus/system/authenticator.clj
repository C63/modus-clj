(ns modus.system.authenticator
  (:require [buddy.auth :as auth]
            [buddy.auth.middleware :refer [wrap-authorization
                                           wrap-authentication]]
            [modus.misc.http :as http]
            [buddy.auth.backends.httpbasic :as http-basic-backend]
            [buddy.auth.backends.token :as jwt-backend]
            [ring.util.http-response :as resp]
            [clojure.tools.logging :as log])
  (:import (org.mindrot.jbcrypt BCrypt)))

(def sign-in-secret "modus-c63-sign-in-secret-need-to-change-to-better-secret")

(defn handle-unauthorized
  [request _]
  (if (auth/authenticated? request)
    {:status 403 :headers {} :body {:message "Permission denied"}}
    {:status 401 :headers {} :body {:message "Unauthorized"}}))

(defn identity-roles->keywords [request]
  (cond-> request
          (:identity request) (update-in [:identity :user-roles]
                                         (partial mapv (fn [s] (cond-> s (string? s) keyword))))))

(defn wrap-convert-identity-roles-to-keywords
  [handler]
  (fn [request] (-> request
                    identity-roles->keywords
                    handler)))

(defn create-identity
  [credentials]
  (when credentials
    {:account-id    (:account-id credentials)
     :account-roles "admin"}))

(defn get-user-credentials-fn
  [db-conn]
  (let [get-credentials (users/get-user-credentials-fn db-conn)]
    (fn [request {:keys [username password]}]
      (if (and username password)
        (when-let [credentials (get-credentials username)]
          (when (BCrypt/checkpw password (:password credentials))
            (create-identity credentials)))
        (log/warnf "Invalid http authentication, nil username or password: %s" request)))))

(defn configured-jws-backend
  []
  (jwt-backend/jws-backend {:secret sign-in-secret :unauthorized-handler handle-unauthorized}))

(defn wrap-authenticate-api-user-using-buddy-with-credentials-fn
  [handler credentials-fn]
  (let [basic-auth (http-basic-backend/http-basic-backend {:realm  "modus"
                                                          :authfn credentials-fn})
        jwt-auth (configured-jws-backend)]
    (-> handler
        (wrap-authorization basic-auth)
        (wrap-convert-identity-roles-to-keywords)
        (wrap-authentication basic-auth jwt-auth))))

(defn wrap-authenticate-api-user-using-buddy
  [handler db-conn]
  (wrap-authenticate-api-user-using-buddy-with-credentials-fn handler (get-user-credentials-fn db-conn)))


(defn authorize!
  [{{:keys [account-roles]} :identity} roles]
  (when-not (some roles account-roles)
    (auth/throw-unauthorized)))

(defn wrap-authorize
  "Throws unauthorized exception to be handled by buddy auth middlewares."
  [handler roles]
  (fn [request]
    (authorize! request roles)
    (handler request)))

(defn wrap-auth
  "Responds forbidden if user does not have required roles. Responds unauthorized if not signed in."
  [handler roles]
  (fn [{{:keys [account-roles]} :identity :as request}]
    (if (empty? account-roles)
      (resp/unauthorized)
      (authorize! request roles))))
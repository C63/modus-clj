(ns modus.system.authenticator
  (:require [buddy.auth :as auth]
            [buddy.auth.middleware :refer [wrap-authorization
                                           wrap-authentication]]
            [modus.back.account :as accounts]
            [buddy.auth.backends.httpbasic :as http-basic-backend]
            [buddy.auth.backends.token :as jwt-backend]
            [clojure.tools.logging :as log]
            [modus.misc.config :as config])
  (:import (org.mindrot.jbcrypt BCrypt)))

(defn create-identity
  [credentials]
  (when credentials
    {:account-id (:account-id credentials)}))

(defn get-account-credentials-fn
  [db-conn]
  (let [get-credentials (accounts/get-account-credential-fn db-conn)]
    (fn [request {:keys [username password]}]
      (if (and username password)
        (when-let [credentials (get-credentials username)]
          (when (BCrypt/checkpw password (:password credentials))
            (create-identity credentials)))
        (log/warnf "Invalid http authentication, nil username or password: %s" request)))))

(defn configured-jws-backend
  []
  (jwt-backend/jws-backend {:secret config/sign-in-secret}))

(defn wrap-authenticate-api-user-using-buddy-with-credentials-fn
  [handler credentials-fn]
  (let [basic-auth (http-basic-backend/http-basic-backend {:realm  "modus"
                                                           :authfn credentials-fn})
        jwt-auth (configured-jws-backend)]
    (-> handler
        (wrap-authentication basic-auth jwt-auth))))

(defn wrap-authenticate-api-user-using-buddy
  [handler db-conn]
  (wrap-authenticate-api-user-using-buddy-with-credentials-fn handler (get-account-credentials-fn db-conn)))

(defn wrap-authorize
  "Throws unauthorized exception to be handled by buddy auth middlewares."
  [handler]
  (fn [request]
    (if (auth/authenticated? request)
      (handler request)
      (auth/throw-unauthorized))))
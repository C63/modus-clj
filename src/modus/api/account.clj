(ns modus.api.account
  (:require [compojure.core :refer :all]
            [clojure.tools.logging :as log]
            [modus.back.account :as accounts]
            [modus.misc.api :as api-common]
            [modus.system.authenticator :as auth]
            [ring.util.http-response :as resp]))

(defn create-account-routes [db-conn]
  (routes
    (GET "/" req
      :middleware [#(auth/wrap-authorize %)]
      (resp/ok (api-common/authenticated-id req)))
    (POST "/register" [:as req]
      (let [{{:keys [name email password]} :body} req]
        (when-let [response (accounts/create-account! db-conn name email password)]
          (if (:success? response)
            (resp/ok {:access-token (:body response)})
            (resp/bad-request {:exception (:reason response)})))))
    (POST "/get-token" [:as req]
      (let [{{:keys [email password]} :body} req]
        (if-let [token (accounts/email-login db-conn email password)]
          (resp/ok {:access-token token})
          (resp/unauthorized "Unauthorized"))))))
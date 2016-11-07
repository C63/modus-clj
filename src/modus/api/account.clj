(ns modus.api.account
  (:require [compojure.core :refer :all]
            [clojure.tools.logging :as log]
            [modus.back.account :as accounts]
            [modus.misc.api :as api-common]
            [modus.misc.util :refer [str->int]]
            [modus.system.authenticator :as auth]
            [schema.core :as s]
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
            (resp/created {:access-token (:body response)})
            (resp/bad-request {:exception (:reason response)})))))
    (POST "/get-token" [:as req]
      (let [{{:keys [email password]} :body} req]
        (if-let [token (accounts/email-login db-conn email password)]
          (resp/ok {:access-token token})
          (resp/unauthorized "Unauthorized"))))
    (GET "/:id" [:as req]
      (let [{{id :id} :params} req
            account (accounts/get-account-by-id db-conn (str->int id))]
        (if account
          (resp/ok account)
          (resp/not-found "Account with that ID not found"))))
    (PUT "/:id/name" [:as req]
      (let [{{:keys [id]} :params} req
            {{:keys [name]} :body} req
            account-id (str->int id)
            auth-account-id (api-common/authenticated-id req)]
        (if (= auth-account-id account-id)
          (when (accounts/update-account-name db-conn account-id name)
            (resp/no-content))
          (if auth-account-id
            (resp/forbidden "Permission denied")
            (resp/unauthorized "Unauthorized"))
          )))
    (PUT "/:id/email" [:as req]
      (let [{{:keys [id]} :params} req
            {{:keys [email]} :body} req
            account-id (str->int id)
            auth-account-id (api-common/authenticated-id req)]
        (if (= auth-account-id account-id)
          (when (accounts/update-account-email db-conn account-id email)
            (resp/no-content))
          (if auth-account-id
            (resp/forbidden "Permission denied")
            (resp/unauthorized "Unauthorized"))
          )))
    (PUT "/:id/password" [:as req]
      (let [{{:keys [id]} :params} req
            {{:keys [new-password old-password]} :body} req
            account-id (str->int id)
            auth-account-id (api-common/authenticated-id req)]
        (if (= auth-account-id account-id)
          (if (accounts/valid-password? db-conn account-id old-password)
            (when (accounts/change-password db-conn account-id new-password)
              (resp/no-content))
            (resp/bad-request "Password is not correct!"))
          (if auth-account-id
            (resp/forbidden "Permission denied")
            (resp/unauthorized "Unauthorized"))
          )))
    ))
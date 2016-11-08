(ns modus.api.account
  (:require [compojure.api.sweet :refer :all]
            [modus.back.account :as accounts]
            [modus.misc.util :refer [str->int]]
            [ring.util.http-response :as resp]
            [schema.core :as s]
            [modus.system.authenticator :as auth]
            modus.system.restructure))

(defn create-account-routes [db-conn]
  :tags ["accounts"]
  (routes
    (POST "/register" []
      :body-params [name :- s/Str, email :- s/Str, password :- s/Str]
      (when-let [response (accounts/create-account! db-conn name email password)]
        (if (:success? response)
          (resp/ok {:access-token (:body response)})
          (resp/bad-request {:exception (:reason response)}))))

    (POST "/get-token" []
      :body-params [email :- s/Str, password :- s/Str]
      (if-let [token (accounts/email-login db-conn email password)]
        (resp/ok {:access-token token})
        (resp/unauthorized "Unauthorized")))

    (GET "/profile" []
      :middleware [#(auth/wrap-authorize %)]
      :auth-account auth-account
      (if-let [account (accounts/get-account-by-id db-conn (:account-id auth-account))]
        (resp/ok account)
        (resp/not-found "Account with that ID not found")))

    (PUT "/:account-id/name" []
      :path-params [account-id :- s/Int]
      :middleware [auth/wrap-authorize]
      :body-params [name :- s/Str]
      :auth-account auth-account
      (let [auth-id (:account-id auth-account)]
        (if (= auth-id account-id)
          (when (accounts/update-account-name db-conn account-id name)
            (resp/no-content))
          (resp/forbidden "Permission denied"))))

    (PUT "/:account-id/email" []
      :path-params [account-id :- s/Int]
      :middleware [auth/wrap-authorize]
      :body-params [email :- s/Str]
      :auth-account auth-account
      (let [auth-id (:account-id auth-account)]
        (if (= auth-id account-id)
          (when (accounts/update-account-email db-conn account-id email)
            (resp/no-content))
          (resp/forbidden "Permission denied"))))

    (PUT "/:account-id/password" []
      :path-params [account-id :- s/Int]
      :middleware [auth/wrap-authorize]
      :body-params [new-password :- s/Str, old-password :- s/Str]
      :auth-account auth-account
      (let [auth-id (:account-id auth-account)]
        (if (= auth-id account-id)
          (if (accounts/valid-password? db-conn account-id old-password)
            (when (accounts/change-password db-conn account-id new-password)
              (resp/no-content))
            (resp/bad-request "Password is not correct!"))
          (resp/forbidden "Permission denied"))))))
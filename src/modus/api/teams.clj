(ns modus.api.teams
  (:require [compojure.api.sweet :refer :all]
            [modus.back.team :as teams]
            [modus.misc.util :refer [str->int, str->uuid]]
            [modus.system.authenticator :as auth]
            [ring.util.http-response :as resp]
            [schema.core :as s]
            modus.system.restructure))


(defn create-team-routes [db-conn]
  (-> (routes
        (GET "/" []
          :auth-account auth-account
          (let [account-id (:account-id auth-account)]
            (resp/ok (teams/get-teams-by-account-id db-conn account-id))))

        (POST "/" []
          :body-params [name :- s/Str, description :- s/Str]
          :auth-account auth-account
          (let [account-id (:account-id auth-account)]
            (teams/create-team db-conn account-id name description)
            (resp/created)))

        (PUT "/:team-id" []
          :path-params [team-id :- s/Uuid]
          :body-params [name :- s/Str, description :- s/Str]
          :auth-account auth-account
          (let [account-id (:account-id auth-account)]
            (if (teams/check-relationship-account-team db-conn account-id team-id)
              (when (teams/update-team db-conn team-id name description)
                (resp/no-content))
              (resp/forbidden "Permission denied!"))))

        (POST "/:team-id/accounts" []
          :path-params [team-id :- s/Uuid]
          :body-params [account-id :- s/Int]
          :auth-account auth-account
          (let [auth-id (:account-id auth-account)]
            (if (teams/check-relationship-account-team db-conn auth-id team-id)
              (when (teams/add-account-to-team db-conn account-id team-id)
                (resp/no-content))
              (resp/forbidden "Permission denied!"))))

        (DELETE "/:team-id/accounts" []
          :path-params [team-id :- s/Uuid]
          :body-params [account-id :- s/Int]
          :auth-account auth-account
          (let [auth-id (:account-id auth-account)]
            (if (teams/check-relationship-account-team db-conn auth-id team-id)
              (when (teams/remove-account-from-team db-conn account-id team-id)
                (resp/no-content))
              (resp/forbidden "Permission denied!")))))

      (auth/wrap-authorize)))

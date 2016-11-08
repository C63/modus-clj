(ns modus.api.projects
  (:require [compojure.api.sweet :refer :all]
            [modus.back.project :as projects]
            [modus.misc.util :refer [str->int, str->uuid]]
            [modus.system.authenticator :as auth]
            [ring.util.http-response :as resp]
            [schema.core :as s]
            modus.system.restructure))

(defn create-projects-routes [db-conn]
  (-> (routes
        (GET "/" []
          :query-params [team-id :- s/Uuid]
          :auth-account auth-account
          (let [account-id (:account-id auth-account)]
            (-> (if team-id
                  (projects/get-projects-by-team-id db-conn team-id)
                  (projects/get-projects-by-account-id db-conn account-id))
                (resp/ok))))

        (POST "/" []
          :auth-account auth-account
          :body-params [name :- s/Str, description :- s/Str, team-id :- s/Uuid]
          (let [account-id (:account-id auth-account)]
            (projects/create-project db-conn account-id team-id name description)
            (resp/created)))

        (PUT "/:project-id" []
          :auth-account auth-account
          :path-params [project-id :- s/Uuid]
          :body-params [name :- s/Str, description :- s/Str]
          (let [auth-id (:account-id auth-account)]
            (if (projects/check-relationship-account-project db-conn auth-id project-id)
              (when (projects/update-project db-conn project-id name description)
                (resp/no-content))
              (resp/forbidden "Permission denied!"))))

        (POST "/:project-id/accounts" [:as req]
          :path-params [project-id :- s/Uuid]
          :body-params [account-id :- s/Uuid]
          :auth-account auth-account
          (let [auth-id (:account-id auth-account)]
            (if (projects/check-relationship-account-project db-conn auth-id project-id)
              (when (projects/add-account-to-project db-conn account-id project-id)
                (resp/no-content))
              (resp/forbidden "Permission denied!"))))

        (DELETE "/:project-id/accounts" []
          :path-params [project-id :- s/Uuid]
          :body-params [account-id :- s/Uuid]
          :auth-account auth-account
          (let [auth-id (:account-id auth-account)]
            (if (projects/check-relationship-account-project db-conn auth-id project-id)
              (when (projects/add-account-to-project db-conn account-id project-id)
                (resp/no-content))
              (resp/forbidden "Permission denied!")))))

      (auth/wrap-authorize)))

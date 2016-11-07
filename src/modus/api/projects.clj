(ns modus.api.projects
  (:require [compojure.core :refer :all]
            [modus.back.project :as projects]
            [modus.misc.api :as api-common]
            [modus.misc.util :refer [str->int, str->uuid]]
            [modus.system.authenticator :as auth]
            [ring.util.http-response :as resp]))


(defn create-projects-routes [db-conn]
  (-> (routes
        (GET "/" [:as req]
          (let [account-id (api-common/authenticated-id req)
                {{team-id :team-id} :params} req
                team-id (str->uuid team-id)]
            (-> (if team-id
                  (projects/get-projects-by-team-id db-conn team-id)
                  (projects/get-projects-by-account-id db-conn account-id))
                (resp/ok))))
        (POST "/" [:as req]
          (let [account-id (api-common/authenticated-id req)
                {{:keys [name description team-id]} :body} req]
            (when-let [response (projects/create-project db-conn account-id team-id name description)]
              (resp/created (str (:team-id response))))))
        (PUT "/:project-id" [:as req]
          (let [auth-id (api-common/authenticated-id req)
                {{project-id :project-id} :params} req
                {{:keys [name description]} :body} req
                project-id (str->uuid project-id)]
            (if (projects/check-relationship-account-project db-conn auth-id project-id)
              (when (projects/update-project db-conn project-id name description)
                (resp/no-content))
              (resp/forbidden "Permission denied!")
              )))
        (POST "/:project-id/accounts" [:as req]
          (let [auth-id (api-common/authenticated-id req)
                {{project-id :project-id} :params} req
                {{:keys [account-id]} :body} req
                project-id (str->uuid project-id)]
            (if (projects/check-relationship-account-project db-conn auth-id project-id)
              (when (projects/add-account-to-project db-conn account-id project-id)
                (resp/no-content))
              (resp/forbidden "Permission denied!")
              )))
        (DELETE "/:team-id/accounts" [:as req]
          (let [auth-id (api-common/authenticated-id req)
                {{project-id :project-id} :params} req
                {{:keys [account-id]} :body} req
                project-id (str->uuid project-id)]
            (if (projects/check-relationship-account-project db-conn auth-id project-id)
              (when (projects/add-account-to-project db-conn account-id project-id)
                (resp/no-content))
              (resp/forbidden "Permission denied!")
              )))
        )
      (auth/wrap-authorize)))

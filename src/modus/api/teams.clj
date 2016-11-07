(ns modus.api.teams
  (:require [compojure.core :refer :all]
            [modus.back.team :as teams]
            [modus.misc.api :as api-common]
            [modus.misc.util :refer [str->int, str->uuid]]
            [modus.system.authenticator :as auth]
            [ring.util.http-response :as resp]))


(defn create-team-routes [db-conn]
  (-> (routes
        (GET "/" [:as req]
          (let [account-id (api-common/authenticated-id req)]
            (resp/ok (teams/get-teams-by-account-id db-conn account-id))))
        (POST "/" [:as req]
          (let [account-id (api-common/authenticated-id req)
                {{:keys [name description]} :body} req]
            (when-let [response (teams/create-team db-conn account-id name description)]
              (resp/created (str (:team-id response))))))
        (PUT "/:team-id" [:as req]
          (let [auth-id (api-common/authenticated-id req)
                {{team-id :team-id} :params} req
                {{:keys [name description]} :body} req
                team-id (str->uuid team-id)]
            (if (teams/check-relationship-account-team db-conn auth-id team-id)
              (when (teams/update-team db-conn team-id name description)
                (resp/no-content))
              (resp/forbidden "Permission denied!")
              )))
        (POST "/:team-id/accounts" [:as req]
          (let [auth-id (api-common/authenticated-id req)
                {{team-id :team-id} :params} req
                {{:keys [account-id]} :body} req
                team-id (str->uuid team-id)]
            (if (teams/check-relationship-account-team db-conn auth-id team-id)
              (when (teams/add-account-to-team db-conn account-id team-id)
                (resp/no-content))
              (resp/forbidden "Permission denied!")
              )))
        (DELETE "/:team-id/accounts" [:as req]
          (let [auth-id (api-common/authenticated-id req)
                {{team-id :team-id} :params} req
                {{:keys [account-id]} :body} req
                team-id (str->uuid team-id)]
            (if (teams/check-relationship-account-team db-conn auth-id team-id)
              (when (teams/remove-account-from-team db-conn account-id team-id)
                (resp/no-content))
              (resp/forbidden "Permission denied!")
              )))
        )
      (auth/wrap-authorize)))

(ns modus.api.teams
  (:require [compojure.core :refer :all]
            [modus.back.team :as teams]
            [modus.misc.api :as api-common]
            [modus.misc.util :refer [str->int]]
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
        )
      (auth/wrap-authorize)))

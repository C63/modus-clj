(ns modus.api.webapp
  (:require [compojure.core :refer [routes context GET ANY]]
            [ring.util.http-response :refer :all]
            [modus.api.account :as account-api]
            [modus.api.teams :as team-api]))

(defn create-api-routes [{:keys [db-conn]}]
  (routes
    (context "/api/v1" []
      (context "/accounts" []
        (account-api/create-account-routes db-conn))
      (context "/teams" []
        (team-api/create-team-routes db-conn))
      (ANY "*" []
        (not-found "Not found")))))

(defrecord APIWebApp [db-conn])

(defn new-api-webapp []
  (map->APIWebApp {}))

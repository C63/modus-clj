(ns modus.api.webapp
  (:require [compojure.core :refer [routes context GET ANY]]
            [ring.util.http-response :refer :all]))

(defn create-api-routes [{:keys [db-conn]}]
  (routes
    (context "/api/v1" []
      (routes
        (GET "/test" [] (ok {:vck "FCK YOU BITCHES!!!"}))
        (ANY "*" []
          (not-found "Not found"))))))

(defrecord APIWebApp [db-conn])

(defn new-api-webapp []
  (map->APIWebApp {}))

(ns modus.api.webapp
  (:require [compojure.core :refer [routes context GET ANY]]
            [ring.util.http-response :refer :all]
            [modus.back.account :as accounts-back]
            [buddy.auth :refer [authenticated? throw-unauthorized]]))

(defn create-api-routes [{:keys [db-conn]}]
  (routes
    (context "/api/v1" []
      (routes
        (GET "/test" [req]
          (prn req)
          (if (authenticated? req)
            (ok {:vck "FCK YOU BITCHES!!!"})
            (throw-unauthorized)
            ))
        (ANY "*" []
          (not-found "Not found"))))))

(defrecord APIWebApp [db-conn])

(defn new-api-webapp []
  (map->APIWebApp {}))

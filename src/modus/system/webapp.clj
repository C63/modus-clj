(ns modus.system.webapp
  (:require [com.stuartsierra.component :as component]
            [compojure.core :refer [ANY GET routes context]]
            [ring.middleware.defaults :refer [api-defaults wrap-defaults]]
            [ring.middleware.format-response :refer [wrap-restful-response]]
            [buddy.auth.middleware :refer [wrap-authorization]]
            [ring.middleware.json :refer [wrap-json-body]]
            [ring.util.response :as ring]
            [modus.system.authenticator :refer [wrap-authenticate-api-user-using-buddy]]
            [modus.api.webapp :refer [create-api-routes]]))

(defn- create-routes [api-routes]
  (routes
    (ANY "/api/*" [] api-routes)
    (ANY "*" []
      (ring/not-found "Not found"))))

(defn create-app [db-conn api-web-app]
  (create-routes
    (-> (create-api-routes api-web-app)
        (wrap-authenticate-api-user-using-buddy db-conn)
        (wrap-json-body {:keywords? true :bigdecimals? true})
        (wrap-restful-response)
        (wrap-defaults api-defaults))))

(defrecord WebApp [routes db-conn api-web-app]
  component/Lifecycle

  (start [component]
    (assoc component :routes (create-app db-conn api-web-app)))
  (stop [component]
    (assoc component :routes nil)))

(defn new-webapp []
  (map->WebApp {}))


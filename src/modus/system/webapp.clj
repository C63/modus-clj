(ns modus.system.webapp
  (:require [clojure.tools.logging :as log]
            [com.stuartsierra.component :as component]
            [compojure.core :refer [ANY GET routes context]]
            [ring.middleware.defaults :refer [api-defaults
                                              secure-site-defaults
                                              site-defaults
                                              wrap-defaults]]
            [ring.middleware.format :refer [wrap-restful-format]]
            [ring.middleware.format-response :refer [wrap-restful-response]]
            [ring.middleware.json :refer [wrap-json-body]]
            [ring.util.response :as ring]
            [modus.misc.config :as config]))

(defn- create-api-routes []
  (routes
    (context "/api/v1" []
      (routes
        (GET "/" [:as req]
          (ring/response {:status "OK"}))))))

(defn- create-routes [api-routes]
  (routes
    (ANY "/api/*" [] api-routes)
    (ANY "*" []
      (ring/not-found "Not found"))))

(def ring-default-config
  (if config/secure-site?
    secure-site-defaults
    site-defaults))

(defn create-app [db-conn]
  (-> (create-routes
        (-> (create-api-routes)
            (wrap-json-body {:keywords? true :bigdecimals? true})
            (wrap-restful-response)))
      (wrap-defaults ring-default-config)))

(defrecord WebApp [routes db-conn]
  component/Lifecycle

  (start [component]
    (assoc component :routes (create-app db-conn)))
  (stop [component]
    (assoc component :routes nil)))

(defn new-webapp []
  (map->WebApp {}))


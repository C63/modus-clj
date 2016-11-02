(ns modus.system.webapp
  (:require [clojure.tools.logging :as log]
            [com.stuartsierra.component :as component]
            [compojure.core :refer [ANY GET routes]]
            [ring.middleware.defaults :refer [api-defaults
                                              secure-site-defaults
                                              site-defaults
                                              wrap-defaults]]
            [ring.middleware.format :refer [wrap-restful-format]]
            [ring.middleware.format-response :refer [wrap-restful-response]]
            [ring.middleware.json :refer [wrap-json-body]]
            [ring.util.response :as ring])
  (:import (java.sql SQLException)))

(defn- create-routes []
  (routes
    (ANY "*" []
      (ring/not-found "Not found"))))

(defn create-app []
  (create-routes))

(defrecord WebApp [routes]
  component/Lifecycle

  (start [component]
    (assoc component :routes (create-app)))
  (stop [component]
    (assoc component :routes nil)))

(defn new-webapp []
  (map->WebApp {}))


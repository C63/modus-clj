(ns modus.system.system
  (:require [com.stuartsierra.component :as component]
            [modus.system.webapp :refer [new-webapp]]
            [modus.system.http-kit :refer [create-http-kit-server]]))

(defn new-modus-system []
  (component/system-map
    :web-app (new-webapp)
    :http-kit (component/using
                (create-http-kit-server)
                [:web-app])))

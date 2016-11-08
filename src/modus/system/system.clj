(ns modus.system.system
  (:require [com.stuartsierra.component :as component]
            [modus.system.db-connection :refer [new-db-connections]]
            [modus.system.webapp :refer [new-webapp]]
            [modus.system.http-kit :refer [create-http-kit-server]]))

(defn new-modus-system []
  (component/system-map
    :db-conn (new-db-connections)
    :web-app (component/using
               (new-webapp)
               [:db-conn])
    :http-kit (component/using
                (create-http-kit-server)
                [:web-app])))

(ns modus.system.http-kit
  (:require [com.stuartsierra.component :as component]
            [org.httpkit.server :as httpkit]
            [clojure.tools.logging :as log]
            ))

(defn- run-server [web-app]
  (let [resp (httpkit/run-server (:routes web-app)
                                 {:port   8080
                                  :thread 500})]
    (log/info "http-kit start")
    resp))

(defrecord HttpKitServer [web-app http-kit]
  component/Lifecycle

  (start [component]
    (assoc component :http-kit (run-server web-app)))

  (stop [component]
    (when http-kit
      (try
        (http-kit :timeout 30000)
        (catch Exception e
          (log/error e "exception when trying to stop http-kit"))))
    (assoc component :http-kit nil)))

(defn create-http-kit-server []
  (map->HttpKitServer {}))



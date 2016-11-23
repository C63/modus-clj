(ns modus.main
  (:require [com.stuartsierra.component :as component]
            [clojure.tools.logging :as log]
            [modus.system.system :refer [new-modus-system]])
  (:gen-class))

(defn -main []
  (component/start (new-modus-system)))
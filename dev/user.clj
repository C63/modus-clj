(ns user
  "Tools for interactive development with the REPL. This file should
  not be included in a production build of the application."
  (:require
    [modus.system.db-connection :as db-connection]
    [modus.system.system :as system]
    [clojure.java.javadoc :refer [javadoc]]
    [clojure.pprint :refer [pprint]]
    [clojure.reflect :refer [reflect]]
    [clojure.repl :refer [apropos dir doc find-doc pst source]]
    [clojure.test :as test]
    [clojure.tools.namespace.repl :refer [refresh refresh-all]]
    [com.stuartsierra.component :as component]))

(def system
  "A Var containing an object representing the application under
  development."
  nil)

(defn assume-system [condition msg]
  (when-not (condition system)
    (throw (IllegalStateException. msg))))

(defn init
  "Creates and initializes the system under development in the Var
  #'system."
  []
  (assume-system nil? "already defined (looking for reset?)")
  (alter-var-root #'system (constantly
                             (system/new-modus-system))))

(defn start
  "Starts the system running, updates the Var #'system."
  []
  (assume-system some? "not inited")
  (alter-var-root #'system component/start))

(defn stop
  "Stops the system if it is currently running, updates the Var
  #'system."
  []
  (assume-system some? "not running")
  (alter-var-root #'system
                  (fn [s] (component/stop s))))

(defn go
  "Initializes and starts the system running."
  []
  (init)
  (start)
  :ready)

(defn reset
  "Stops the system, reloads modified source files, and restarts it."
  []
  (stop)
  (alter-var-root #'system (constantly nil))
  (refresh :after 'user/go))

(defn db
  "Returns system db-conn datasource"
  []
  (db-connection/datasource (:db-conn system)))
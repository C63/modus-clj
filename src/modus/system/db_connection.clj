(ns modus.system.db-connection
  (:require [com.stuartsierra.component :as component]
            [clojure.tools.logging :as log]
            [modus.misc.config :refer [int-system-property]]
            [modus.misc.util :refer [->kebab-case]])
  (:import (com.zaxxer.hikari HikariDataSource)
           (org.postgresql.util PSQLException)))

(defn db-pool [{:keys [url username password max-pool-size]}]
  (let [ds (doto (HikariDataSource.)
             (.setJdbcUrl url)
             (.setUsername username)
             (.setPassword password)
             (.setMaximumPoolSize max-pool-size))]
    {:datasource ds}))

(defn psql-db-properties
  []
  {:url           (str "jdbc:postgresql:" (System/getProperty "DB_CONNSTR" "//localhost:5432/modus"))
   :username      (System/getProperty "DB_USERNAME" (System/getProperty "user.name"))
   :password      (System/getProperty "DB_PASSWORD" "")
   :max-pool-size (int-system-property "DB_MAX_POOL_SIZE" 10)})

(defn datasource [db] (:singleton-datasource db))

(defn query-response [query-result]
  (when query-result
    (into {} (for [[k v] query-result]
               [(->kebab-case k) v]))))

(defrecord DBConnections [singleton-datasource]
  component/Lifecycle

  (start [component]
    (->> (psql-db-properties)
         db-pool
         (assoc component :singleton-datasource)))

  (stop [component]
    (when singleton-datasource
      (try
        (.close (:datasource singleton-datasource))
        (catch Exception e
          (log/error e "exception when closing DB connections"))))
    (assoc component :singleton-datasource nil)))

(defn new-db-connections []
  (map->DBConnections {}))

(defn is-unique-violation?
  [e]
  (when (instance? PSQLException e)
    (= "23505" (.getSQLState ^PSQLException e))))

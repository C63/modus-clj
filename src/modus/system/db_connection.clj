(ns modus.system.db-connection
  (:require [com.stuartsierra.component :as component]
            [clojure.tools.logging :as log]
            [modus.misc.config :refer [int-system-property]]
            [modus.misc.util :refer [->kebab-case]]
            [modus.misc.db :as db-util]
            [cheshire.core :as json]
            [clj-time.coerce :as time-coerce]
            [clojure.java.jdbc :as jdbc])
  (:import (com.zaxxer.hikari HikariDataSource)
           (org.postgresql.util PGobject)
           (java.sql Timestamp Date Array PreparedStatement)
           (clojure.lang IPersistentVector IPersistentMap)
           (org.joda.time DateTime)))

(defn db-pool [{:keys [url username password max-pool-size]}]
  (let [ds (doto (HikariDataSource.)
             (.setJdbcUrl url)
             (.setUsername username)
             (.setPassword password)
             (.setMaximumPoolSize max-pool-size))]
    {:datasource ds}))

(defn psql-db-properties
  []
  {:url           (str "jdbc:postgresql://" (System/getProperty "DB_CONNSTR" "localhost:5432/modus"))
   :username      (System/getProperty "DB_USERNAME" (System/getProperty "user.name"))
   :password      (System/getProperty "DB_PASSWORD" "")
   :max-pool-size (int-system-property "DB_MAX_POOL_SIZE" 10)})

(defn datasource [db] (:singleton-datasource db))

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

(extend-type IPersistentVector
  jdbc/ISQLParameter
  (set-parameter [v ^PreparedStatement stmt ^long idx]
    (let [conn (.getConnection stmt)
          meta (.getParameterMetaData stmt)
          type-name (.getParameterTypeName meta idx)]
      (if-let [elem-type (when (= (first type-name) \_) (apply str (rest type-name)))]
        (.setObject stmt idx (.createArrayOf conn elem-type (to-array v)))
        (.setObject stmt idx (db-util/to-pg-json v))))))

(extend-protocol jdbc/ISQLValue
  DateTime
  (sql-value [value] (time-coerce/to-sql-time value))
  IPersistentMap
  (sql-value [value] (db-util/to-pg-json value))
  IPersistentVector
  (sql-value [value] (db-util/to-pg-json value)))

(extend-protocol jdbc/IResultSetReadColumn
  Array
  (result-set-read-column [val _ _] (vec (.getArray val)))

  Date
  (result-set-read-column [v _ _] (db-util/sql-date->date-time v))

  Timestamp
  (result-set-read-column [v _ _] (time-coerce/from-sql-time v))

  PGobject
  (result-set-read-column [pgobj _metadata _idx]
    (let [type (.getType pgobj)
          value (.getValue pgobj)]
      (case type
        "json" (json/parse-string value true)
        "jsonb" (json/parse-string value true)
        :else value))))

(defn new-db-connections []
  (map->DBConnections {}))
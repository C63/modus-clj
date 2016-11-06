(ns modus.misc.db
  (:require [cheshire.core :as json]
            [clj-time.coerce :as time-coerce])
  (:import (java.sql Date)
           (java.time ZoneOffset)
           (org.postgresql.util PGobject PSQLException)))

(defn sql-date->date-time [^Date sql-date]
  (-> sql-date
      .toLocalDate
      .atStartOfDay
      (.toInstant ZoneOffset/UTC)
      java.util.Date/from
      time-coerce/from-date))

(defn to-pg-json [value]
  (doto (PGobject.)
    (.setType "jsonb")
    (.setValue (json/generate-string value))))

(defn pg-enum [enum-type value]
  (doto (PGobject.)
    (.setType enum-type)
    (.setValue value)))

(defn is-unique-violation?
  [e]
  (when (instance? PSQLException e)
    (= "23505" (.getSQLState ^PSQLException e))))

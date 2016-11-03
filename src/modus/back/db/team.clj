(ns modus.back.db.team
  (:require [hugsql.core :as hugsql]))

(hugsql/def-db-fns "sql/teams.sql")

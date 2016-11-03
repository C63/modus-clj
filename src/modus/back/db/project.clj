(ns modus.back.db.project
  (:require [hugsql.core :as hugsql]))

(hugsql/def-db-fns "sql/projects.sql")

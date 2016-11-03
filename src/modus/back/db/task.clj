(ns modus.back.db.task
  (:require [hugsql.core :as hugsql]))

(hugsql/def-db-fns "sql/tasks.sql")

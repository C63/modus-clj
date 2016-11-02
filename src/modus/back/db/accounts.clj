(ns modus.back.db.accounts
  (:require [hugsql.core :as hugsql]))

(hugsql/def-db-fns "sql/accounts.sql")

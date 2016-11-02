(ns modus.back.account
  (:require [modus.back.db.accounts :as sql]
            [modus.system.db-connection :refer [datasource is-unique-violation?]]
            [modus.misc.util :refer [map->kebab-case truncate]]))

(defn- account-name
  [name]
  (truncate name 100))

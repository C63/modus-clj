(ns modus.back.team
  (:require [modus.back.crud.teams :as crud]
            [modus.system.db-connection :refer [datasource]]))

(defn add-account-to-team [db-conn account-id team-id]
  (crud/add-account-to-team (datasource db-conn) account-id team-id))

(defn remove-account-from-team [db-conn account-id team-id]
  (crud/remove-account-from-team (datasource db-conn) account-id team-id))
(ns modus.back.team
  (:require [modus.back.crud.teams :as crud]
            [modus.system.db-connection :refer [datasource]]
            [clojure.java.jdbc :as jdbc]))

(defn add-account-to-team [db-conn account-id team-id]
  (crud/add-account-to-team (datasource db-conn) account-id team-id))

(defn remove-account-from-team [db-conn account-id team-id]
  (crud/remove-account-from-team (datasource db-conn) account-id team-id))

(defn create-team [db-conn account-id name description]
  (jdbc/with-db-transaction
    [tx (datasource db-conn)]
    (crud/create-team tx account-id name description)))

(defn get-teams-by-account-id [db-conn account-id]
  (crud/get-teams-by-account-id (datasource db-conn) account-id))

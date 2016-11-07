(ns modus.back.project
  (:require [modus.back.crud.projects :as crud]
            [clojure.java.jdbc :as jdbc]
            [modus.misc.util :refer [query-response]]
            [modus.system.db-connection :refer [datasource]]))

(defn create-project [db-conn account-id team-id name description]
  (jdbc/with-db-transaction
    [tx (datasource db-conn)]
    (when (crud/create-project tx account-id team-id name description)
      {:success? true})
    ))

(defn add-account-to-project [db-conn account-id project-id]
  (crud/add-account-to-project (datasource db-conn) account-id project-id))

(defn remove-account-from-project [db-conn account-id project-id]
  (crud/remove-account-from-project (datasource db-conn) account-id project-id))

(defn get-projects-by-account-id [db-conn account-id]
  (crud/get-projects-by-account-id (datasource db-conn) account-id))

(defn get-projects-by-team-id [db-conn team-id]
  (crud/get-projects-by-team-id (datasource db-conn) team-id))

(defn check-relationship-account-project [db-conn account-id project-id]
  (crud/check-relationship-account-project (datasource db-conn) account-id project-id))

(defn update-project [db-conn project-id name description]
  (crud/update-project (datasource db-conn) project-id name description))
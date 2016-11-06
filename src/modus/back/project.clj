(ns modus.back.project
  (:require [modus.back.crud.projects :as crud]
            [modus.system.db-connection :refer [datasource]]))

(defn create-project [db-conn team-id name description]
  (let [project-id (crud/create-project (datasource db-conn) team-id name description)]
    project-id))

(defn add-account-to-project [db-conn account-id project-id]
  (crud/add-account-to-project (datasource db-conn) account-id project-id))

(defn remove-account-from-project [db-conn account-id project-id]
  (crud/remove-account-from-project (datasource db-conn) account-id project-id))


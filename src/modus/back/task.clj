(ns modus.back.task
  (:require [modus.back.crud.tasks :as crud]
            [modus.system.db-connection :refer [datasource]]))

(defn create-task-list [db-conn project-id name description]
  (crud/create-task-list (datasource db-conn) project-id name description))

(defn create-task [db-conn task-list-id name description]
  (crud/create-task (datasource db-conn) task-list-id name description))

(defn add-account-to-task-list [db-conn account-id task-list-id]
  (crud/add-account-to-task-list (datasource db-conn) account-id task-list-id))

(defn remove-account-from-task-list [db-conn account-id task-list-id]
  (crud/remove-account-from-task-list (datasource db-conn) account-id task-list-id))

(defn add-account-to-task-list [db-conn account-id task-id]
  (crud/add-account-to-task (datasource db-conn) account-id task-id))

(defn remove-account-from-task-list [db-conn account-id task-id]
  (crud/remove-account-from-task (datasource db-conn) account-id task-id))

(defn get-task-list-by-project-id [db-conn project-id]
  (crud/get-task-list-by-project-id (datasource db-conn) project-id))

(defn get-task-list-by-id [db-conn task-list-id]
  (crud/get-task-list-by-id (datasource db-conn) task-list-id))

(defn update-task-list [db-conn task-list-id name description]
  (crud/update-task-list (datasource db-conn) task-list-id name description))

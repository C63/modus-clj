(ns modus.back.crud.tasks
  (:require [modus.back.db.task :as sql]
            [modus.misc.util :refer [truncate generate-uuid query-response]]))

(defn truncate-name [name] (truncate name 100))

(defn create-task-list [ds project-id name description]
  (let [task-list-id (generate-uuid)]
    (do (sql/create-task-list ds {:task-list-id task-list-id
                                  :project-id   project-id
                                  :name         (truncate-name name)
                                  :description  description})
        task-list-id)))

(defn create-task [ds task-list-id name description]
  (let [task-id (generate-uuid)]
    (do (sql/create-task ds {:task-id      task-id
                             :task-list-id task-list-id
                             :name         (truncate-name name)
                             :description  description})
        task-id)))

(defn add-account-to-task-list [ds account-id task-list-id]
  (sql/add-account-to-task-list ds {:account-id account-id :task-list-id task-list-id}))

(defn remove-account-from-task-list [ds account-id task-list-id]
  (sql/remove-account-from-task-list ds {:account-id account-id :task-list-id task-list-id}))

(defn add-account-to-task [ds account-id task-id]
  (sql/add-account-to-task ds {:account-id account-id :task-id task-id}))

(defn remove-account-from-task [ds account-id task-id]
  (sql/remove-account-from-task ds {:account-id account-id :task-id task-id}))

(defn get-task-list-by-project-id [ds project-id]
  (map query-response (sql/get-task-list-by-project-id ds {:project-id project-id})))

(defn get-task-list-by-id [ds task-list-id]
  (query-response (sql/get-task-list-by-id ds {:task-list-id task-list-id})))

(defn update-task-list [ds task-list-id name description]
  (sql/update-task-list ds {:task-list-id task-list-id
                            :name         name
                            :description  description}))

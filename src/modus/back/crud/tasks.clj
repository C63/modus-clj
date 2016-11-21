(ns modus.back.crud.tasks
  (:require [modus.back.db.task :as sql]
            [modus.misc.util :refer [truncate generate-uuid query-response
                                     to-iat]]
            [modus.misc.db :refer [pg-enum]]))

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

(defn get-task-by-task-list-id [ds task-list-id]
  (map query-response (sql/get-task-by-task-list-id ds {:task-list-id task-list-id})))

(defn get-task-by-id [ds task-id]
  (query-response (sql/get-task-by-id ds {:task-id task-id})))

(defn update-task [ds task-id task-list-id task-name description status]
  (sql/update-task ds {:task-id      task-id
                       :task-list-id task-list-id
                       :name         task-name
                       :description  description
                       :status (pg-enum (name status))}))

(defn get-comment-by-task-id [ds task-id]
  (map query-response (sql/get-comment-by-task-id ds {:task-id task-id})))

(defn create-comment-for-task [ds task-id account-id content]
  (sql/create-comment-for-task ds {:task-id task-id :account-id account-id :content content}))
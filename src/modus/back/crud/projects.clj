(ns modus.back.crud.projects
  (:require [modus.back.db.project :as sql]
            [modus.misc.util :refer [truncate generate-uuid]]))

(defn- project-name [name] (truncate name 100))

(defn create-project [ds team-id name description]
  (let [project-id (generate-uuid)]
    (do (sql/create-projects ds {:team-id     team-id
                                 :project-id  project-id
                                 :name        (project-name name)
                                 :description description})
        project-id)))

(defn add-account-to-project [ds account-id project-id]
  (sql/add-account-to-project ds {:account-id account-id :project-id project-id}))

(defn remove-account-from-project [ds account-id project-id]
  (sql/remove-account-from-project ds {:account-id account-id :project-id project-id}))


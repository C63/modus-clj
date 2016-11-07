(ns modus.back.crud.projects
  (:require [modus.back.db.project :as sql]
            [modus.misc.util :refer [truncate generate-uuid query-response]]))

(defn- project-name [name] (truncate name 100))

(defn add-account-to-project [ds account-id project-id]
  (sql/add-account-to-project ds {:account-id account-id :project-id project-id}))

(defn remove-account-from-project [ds account-id project-id]
  (sql/remove-account-from-project ds {:account-id account-id :project-id project-id}))

(defn create-project [ds account-id team-id name description]
  (let [project-id (generate-uuid)]
    (do (sql/create-projects ds {:team-id     team-id
                                 :project-id  project-id
                                 :name        (project-name name)
                                 :description description})
        (add-account-to-project ds account-id project-id))))

(defn get-projects-by-account-id [ds account-id]
  (prn "account")
  (map query-response (sql/get-projects-by-account-id ds {:account-id account-id})))

(defn get-projects-by-team-id [ds team-id]
  (prn "team")
  (map query-response (sql/get-projects-by-team-id ds {:team-id team-id})))

(defn check-relationship-account-project [ds account-id project-id]
  (->> {:account-id account-id :project-id project-id}
       (sql/check-relationship-account-project ds)
       :relationship_count
       (= 1)))

(defn update-project [ds project-id name description]
  (sql/update-project ds {:project-id  project-id
                          :name        name
                          :description description}))
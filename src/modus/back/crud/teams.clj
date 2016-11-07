(ns modus.back.crud.teams
  (:require [modus.back.db.team :as sql]
            [modus.misc.util :refer [truncate generate-uuid query-response]]))

(defn- team-name [name] (truncate name 100))

(defn create-team
  ([tx account-id name description]
   (let [team-id (generate-uuid)]
     (do (sql/create-team tx {:team-id     team-id
                              :name        (team-name name)
                              :description description})
         (sql/add-account-to-team tx {:account-id account-id :team-id team-id})
         {:team-id team-id}))))

(defn add-account-to-team [ds account-id team-id]
  (sql/add-account-to-team ds {:account-id account-id :team-id team-id}))

(defn remove-account-from-team [ds account-id team-id]
  (sql/remove-account-from-team ds {:account-id account-id :team-id team-id}))

(defn get-teams-by-account-id [ds account-id]
  (map query-response (sql/get-teams-by-account-id ds {:account-id account-id})))

(defn check-relationship-account-team [ds account-id team-id]
  (->> {:account-id account-id :team-id team-id}
       (sql/check-relationship-account-team ds)
       :relationship_count
       (= 1)))

(defn update-team [ds team-id name description]
  (sql/update-team ds {:team-id team-id :name name :description description}))
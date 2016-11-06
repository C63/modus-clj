(ns modus.back.crud.teams
  (:require [modus.back.db.team :as sql]
            [modus.misc.util :refer [truncate generate-uuid]]))

(defn- team-name [name] (truncate name 100))

(defn create-team [ds name description]
  (let [team-id (generate-uuid)]
    (do (sql/create-team ds {:team-id     team-id
                             :name        (team-name name)
                             :description description})
        team-id)))

(defn add-account-to-team [ds account-id team-id]
  (sql/add-account-to-team ds {:account-id account-id :team-id team-id}))

(defn remove-account-from-team [ds account-id team-id]
  (sql/remove-account-fro-team ds {:account-id account-id :team-id team-id}))

(ns modus.back.team
  (:require [clojure.java.jdbc :refer [with-db-transaction]]
            [modus.back.db.team :as sql]
            [modus.system.db-connection :refer [datasource is-unique-violation? query-response]]
            [modus.misc.util :refer [map->kebab-case truncate generate-uuid]]))

(defn- team-name [name] (truncate name 100))

(defn create-team [db-conn account-id name description]
  (with-db-transaction
    [tx (datasource db-conn)]
    (let [team-id (generate-uuid)]
      (do (sql/create-team tx {:team-id     team-id
                               :name        (team-name name)
                               :description description})
          (sql/add-account-to-team tx {:account-id account-id :team-id team-id})))))


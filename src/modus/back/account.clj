(ns modus.back.account
  (:require [modus.back.db.accounts :as sql]
            [modus.back.team :as team]
            [modus.system.db-connection :refer [datasource is-unique-violation? query-response]]
            [modus.misc.util :refer [map->kebab-case truncate]]))

(defn- account-name
  [name]
  (truncate name 100))

(defn create-account! [db-conn name email password]
  (when-let [account-id (->> {:email email :name (account-name name) :password password}
                             (sql/create-account (datasource db-conn))
                             query-response
                             :account-id)]
    (team/create-team db-conn account-id "personal" nil)))

(defn valid-password? [db-conn account-id password]
  (->> {:account-id account-id
        :password   password}
       (sql/check-password (datasource db-conn))
       :account_count
       (= 1)))

(defn change-password [db-conn account-id new-password]
  (sql/change-password-hash (datasource db-conn) {:new-password new-password
                                                  :account-id   account-id}))

(defn update-account-name [db-conn account-id name]
  (sql/update-account-name (datasource db-conn) {:account-id account-id :name name}))

(defn update-account-email [db-conn account-id email]
  (sql/update-account-email (datasource db-conn) {:account-id account-id :email email}))

(defn email-login [db-conn email password]
  (when-let [account-id (:account_id (sql/find-account-by-email (datasource db-conn) {:email email}))]
    (valid-password? db-conn account-id password)))
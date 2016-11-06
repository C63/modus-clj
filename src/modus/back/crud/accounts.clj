(ns modus.back.crud.accounts
  (:require [modus.back.db.accounts :as sql]
            [modus.system.db-connection :refer [query-response]]
            [modus.misc.util :refer [truncate]]))

(defn- account-name
  [name]
  (truncate name 100))

(defn create-account [ds name email password]
  (when-let [account-id (->> {:email email :name (account-name name) :password password}
                             (sql/create-account ds)
                             query-response
                             :account-id)]
    account-id))

(defn valid-password? [ds account-id password]
  (->> {:account-id account-id
        :password   password}
       (sql/check-password ds)
       :account_count
       (= 1)))

(defn change-password [ds account-id new-password]
  (sql/change-password-hash ds {:new-password new-password :account-id account-id}))

(defn update-account-name [ds account-id name]
  (sql/update-account-name ds {:account-id account-id :name name}))

(defn update-account-email [ds account-id email]
  (sql/update-account-email ds {:account-id account-id :email email}))

(defn email-login [ds email password]
  (when-let [account-id (:account_id (sql/find-account-by-email ds {:email email}))]
    (valid-password? db-conn account-id password)))

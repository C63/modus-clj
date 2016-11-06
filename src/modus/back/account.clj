(ns modus.back.account
  (:require [modus.back.crud.accounts :as accounts]
            [modus.back.crud.teams :as teams]
            [modus.system.db-connection :refer [datasource query-response]]
            [clojure.java.jdbc :refer [with-db-transaction]]))

(defn create-account! [db-conn name email password]
  (with-db-transaction
    (let [tx (datasource db-conn)]
      (when-let [account-id (accounts/create-account tx name email password)
                 team-id (teams/create-team tx "personal" nil)
                 _ (teams/add-account-to-team tx account-id team-id)]
        (modus.back.authenticator/generate-access-token)))))

(defn valid-password? [db-conn account-id password]
  (accounts/valid-password? (datasource db-conn) account-id password))

(defn change-password [db-conn account-id new-password]
  (accounts/change-password (datasource db-conn) account-id new-password))

(defn update-account-name [db-conn account-id name]
  (accounts/update-account-name (datasource db-conn) account-id name))

(defn update-account-email [db-conn account-id email]
  (accounts/update-account-email (datasource db-conn) account-id email))

(defn email-login [db-conn email password]
  (when (accounts/email-login (datasource db-conn) email password)
    (modus.back.authenticator/generate-access-token)))
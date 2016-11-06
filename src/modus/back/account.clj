(ns modus.back.account
  (:require [modus.back.crud.accounts :as accounts-crud]
            [modus.back.crud.teams :as teams-crud]
            [modus.back.authenticator :as authenticator]
            [modus.system.db-connection :refer [datasource]]
            [clojure.java.jdbc :refer [with-db-transaction]]))

(defn create-account! [db-conn name email password]
  (with-db-transaction [tx (datasource db-conn)]
                       (let [account-id (accounts-crud/create-account tx name email password)
                             team-id (teams-crud/create-team tx "personal" nil)]
                         (when (teams-crud/add-account-to-team tx account-id team-id)
                           (authenticator/generate-access-token)))))

(defn valid-password? [db-conn account-id password]
  (accounts-crud/valid-password? (datasource db-conn) account-id password))

(defn change-password [db-conn account-id new-password]
  (accounts-crud/change-password (datasource db-conn) account-id new-password))

(defn update-account-name [db-conn account-id name]
  (accounts-crud/update-account-name (datasource db-conn) account-id name))

(defn update-account-email [db-conn account-id email]
  (accounts-crud/update-account-email (datasource db-conn) account-id email))

(defn email-login [db-conn email password]
  (when (accounts-crud/email-login (datasource db-conn) email password)
    (authenticator/generate-access-token)))
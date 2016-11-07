(ns modus.back.account
  (:require [modus.back.crud.accounts :as accounts-crud]
            [modus.back.crud.teams :as teams-crud]
            [buddy.sign.jwt :as jwt]
            [modus.system.db-connection :refer [datasource]]
            [clojure.java.jdbc :refer [with-db-transaction]]
            [modus.misc.config :as config]
            [clojure.string :as str]))

(defn- generate-token [account-id]
  (jwt/sign {:account-id account-id} config/sign-in-secret))

(defn create-account! [db-conn name email password]
  (let [ds (datasource db-conn)]
    (if (:account-id (accounts-crud/find-account-by-email ds email))
      {:success? false :reason "Account with that email already existed"}
      (when-let [account-id (accounts-crud/create-account ds name email password)]
        {:success? true :body (generate-token account-id)}))))

(defn valid-password? [db-conn account-id password]
  (accounts-crud/valid-password? (datasource db-conn) account-id password))

(defn change-password [db-conn account-id new-password]
  (accounts-crud/change-password (datasource db-conn) account-id new-password))

(defn update-account-name [db-conn account-id name]
  (accounts-crud/update-account-name (datasource db-conn) account-id name))

(defn update-account-email [db-conn account-id email]
  (accounts-crud/update-account-email (datasource db-conn) account-id email))

(defn email-login [db-conn email password]
  (when-let [account-id (accounts-crud/email-login (datasource db-conn) email password)]
    (generate-token account-id)))

(defn get-account-credential-fn [db-conn]
  (fn [email]
    (let [trim-email (str/trim email)
          credentials (accounts-crud/find-account-by-email (datasource db-conn) trim-email)]
      (prn credentials)
      (if (:account-id credentials)
        credentials
        nil))))

(defn get-account-by-id [db-conn account-id]
  (accounts-crud/find-account-by-id (datasource db-conn) account-id))

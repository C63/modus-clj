(ns modus.misc.api)

(defn authenticated-id [req]
  (get-in req [:identity :account-id]))
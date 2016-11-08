(ns modus.api.tasks
  (:require [compojure.api.sweet :refer :all]
            [modus.back.task :as task]
            [modus.misc.util :refer [str->int, str->uuid]]
            [modus.system.authenticator :as auth]
            [ring.util.http-response :as resp]
            [schema.core :as s]
            modus.system.restructure))

(defn create-task-routes [db-conn]
  (routes
    (GET "/" []
      :middleware [#(auth/wrap-authorize %)]
      :query-params [task-list-id :- s/Uuid]
      (resp/ok (task/get-task-by-task-list-id db-conn task-list-id)))

    (POST "/" []
      :middleware [#(auth/wrap-authorize %)]
      :body-params [name :- s/Str, description :- s/Str, task-list-id :- s/Uuid]
      (task/create-task db-conn task-list-id name description)
      (resp/created))

    (GET "/:task-id" []
      :middleware [#(auth/wrap-authorize %)]
      :path-params [task-id :- s/Uuid]
      (if-let [response (task/get-task-by-id db-conn task-id)]
        (resp/ok response)
        (resp/not-found "No tasklist with that id!")))

    (PUT "/:task-id" []
      :middleware [#(auth/wrap-authorize %)]
      :path-params [task-id :- s/Uuid]
      :body-params [name :- s/Str, description :- s/Str, task-list-id :- s/Uuid]
      (task/update-task db-conn task-id task-list-id name description)
      (resp/no-content))

    (POST "/:task-id/accounts" []
      :middleware [#(auth/wrap-authorize %)]
      :path-params [task-id :- s/Uuid]
      :body-params [account-id :- s/Int]
      (task/add-account-to-task db-conn account-id task-id)
      (resp/no-content))

    (DELETE "/:task-id/accounts" []
      :middleware [#(auth/wrap-authorize %)]
      :path-params [task-id :- s/Uuid]
      :body-params [account-id :- s/Int]
      (task/remove-account-from-task db-conn account-id task-id)
      (resp/no-content))))



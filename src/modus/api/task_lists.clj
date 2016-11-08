(ns modus.api.task-lists
  (:require [compojure.api.sweet :refer :all]
            [modus.back.task :as tasklist]
            [modus.misc.util :refer [str->int, str->uuid]]
            [modus.system.authenticator :as auth]
            [ring.util.http-response :as resp]
            [schema.core :as s]
            modus.system.restructure))

(defn create-task-lists-routes [db-conn]
  (routes
    (GET "/" []
      :middleware [#(auth/wrap-authorize %)]
      :query-params [project-id :- s/Uuid]
      (resp/ok (tasklist/get-task-list-by-project-id db-conn project-id)))

    (POST "/" []
      :middleware [#(auth/wrap-authorize %)]
      :body-params [name :- s/Str description :- s/Str project-id :- s/Uuid]
      (tasklist/create-task-list db-conn project-id name description)
      (resp/created))

    (GET "/:task-list-id" []
      :middleware [#(auth/wrap-authorize %)]
      :path-params [task-list-id :- s/Uuid]
      (if-let [response (tasklist/get-task-list-by-id db-conn task-list-id)]
        (resp/ok response)
        (resp/not-found "No tasklist with that id!")))

    (PUT "/:task-list-id" []
      :middleware [#(auth/wrap-authorize %)]
      :path-params [task-list-id :- s/Uuid]
      :body-params [name :- s/Str, description :- s/Str]
      (tasklist/update-task-list db-conn task-list-id name description)
      (resp/no-content))))

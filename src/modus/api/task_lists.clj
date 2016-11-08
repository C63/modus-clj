(ns modus.api.task-lists
  (:require [compojure.core :refer :all]
            [modus.back.task :as tasklist]
            [modus.misc.api :as api-common]
            [modus.misc.util :refer [str->int, str->uuid]]
            [modus.system.authenticator :as auth]
            [ring.util.http-response :as resp]))


(defn create-task-lists-routes [db-conn]
  (-> (routes
        (GET "/" [:as req]
          (let [{{project-id :project-id} :params} req
                project-id (str->uuid project-id)]
            (resp/ok (tasklist/get-task-list-by-project-id db-conn project-id))))
        (POST "/" [:as req]
          (let [{{:keys [name description project-id]} :body} req
                project-id (str->uuid project-id)]
            (when-let [response (tasklist/create-task-list db-conn project-id name description)]
              (resp/created (str response)))))
        (GET "/:task-list-id" [:as req]
          (let [{{task-list-id :task-list-id} :params} req
                task-list-id (str->uuid task-list-id)
                response (tasklist/get-task-list-by-id db-conn task-list-id)]
            (if response
              (resp/ok response)
              (resp/not-found "No tasklist with that id!"))))
        (PUT "/:task-list-id" [:as req]
          (let [{{task-list-id :task-list-id} :params} req
                {{:keys [name description]} :body} req
                task-list-id (str->uuid task-list-id)]
            (when (tasklist/update-task-list db-conn task-list-id name description)
              (resp/no-content))))
        )
      (auth/wrap-authorize)))

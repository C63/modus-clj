(ns modus.api.tasks
  (:require [compojure.core :refer :all]
            [modus.back.task :as task]
            [modus.misc.api :as api-common]
            [modus.misc.util :refer [str->int, str->uuid]]
            [modus.system.authenticator :as auth]
            [ring.util.http-response :as resp]))


(defn create-task-routes [db-conn]
  (-> (routes
        (GET "/" [:as req]
          (let [{{task-list-id :task-list-id} :params} req
                task-list-id (str->uuid task-list-id)]
            (resp/ok (task/get-task-by-task-list-id db-conn task-list-id))))
        (POST "/" [:as req]
          (let [{{:keys [name description task-list-id]} :body} req
                task-list-id (str->uuid task-list-id)]
            (when-let [response (task/create-task db-conn task-list-id name description)]
              (resp/created (str response)))))
        (GET "/:task-id" [:as req]
          (let [{{task-id :task-id} :params} req
                task-id (str->uuid task-id)
                response (task/get-task-by-id db-conn task-id)]
            (if response
              (resp/ok response)
              (resp/not-found "No tasklist with that id!"))))
        (PUT "/:task-id" [:as req]
          (let [{{task-id :task-id} :params} req
                task-id (str->uuid task-id)
                {{:keys [name description task-list-id]} :body} req
                task-list-id (str->uuid task-list-id)]
            (when (task/update-task db-conn task-id task-list-id name description)
              (resp/no-content))))
        (POST "/:task-id/accounts" [:as req]
          (let [{{task-id :task-id} :params} req
                {{:keys [account-id]} :body} req
                task-id (str->uuid task-id)]
            (when (task/add-account-to-task db-conn account-id task-id)
              (resp/no-content))))
        (DELETE "/:task-id/accounts" [:as req]
          (let [{{task-id :task-id} :params} req
                {{:keys [account-id]} :body} req
                task-id (str->uuid task-id)]
            (when (task/remove-account-from-task db-conn account-id task-id)
              (resp/no-content))
            )))
      (auth/wrap-authorize)))



(ns modus.system.webapp
  (:require [com.stuartsierra.component :as component]
            [compojure.api.sweet :refer :all]
            [ring.middleware.defaults :refer [api-defaults wrap-defaults]]
            [ring.middleware.format-response :refer [wrap-restful-response]]
            [buddy.auth.middleware :refer [wrap-authorization]]
            [ring.middleware.json :refer [wrap-json-body]]
            [ring.util.response :as ring]
            [ring.middleware.cors :refer [wrap-cors]]
            [modus.system.authenticator :refer [wrap-authenticate-api-user-using-buddy]]
            [modus.api.account :as account-api]
            [modus.api.teams :as team-api]
            [modus.api.projects :as project-api]
            [modus.api.task-lists :as task-list-api]
            [modus.api.tasks :as task-api]))

(defn create-app [db-conn]
  (-> (api
        {:swagger
         {:ui   "/api-docs"
          :spec "/swagger.json"
          :data {:info                {:title       "Modus API"
                                       :description "vcc swagger"
                                       :version     "0.0.1"}
                 :tags                [{:name "api", :description "some apis"}
                                       {:name "Account", :description "Account api"}
                                       {:name "Team", :description "Team api"}
                                       {:name "Project", :description "Project api"}
                                       {:name "Task List", :description "Task List api"}
                                       {:name "Task", :description "Task api"}]
                 :securityDefinitions {:login   {:type "basic"}
                                       :api_key {:type "apiKey"
                                                 :name "Authorization"
                                                 :in   "header"}}
                 }}}

        (context "/api/v1" []
          :tags ["api"]
          (context "/accounts" []
            :tags ["Account"]
            (account-api/create-account-routes db-conn))
          (context "/teams" []
            :tags ["Team"]
            (team-api/create-team-routes db-conn))
          (context "/projects" []
            :tags ["Project"]
            (project-api/create-projects-routes db-conn))
          (context "/task-lists" []
            :tags ["Task List"]
            (task-list-api/create-task-lists-routes db-conn))
          (context "/tasks" []
            :tags ["Task"]
            (task-api/create-task-routes db-conn))
          (ANY "*" []
            (ring/not-found "Not found"))))
      (wrap-authenticate-api-user-using-buddy db-conn)
      (wrap-cors :access-control-allow-origin [#".+"]
                 :access-control-allow-methods [:get :put :post :delete])))

(defrecord WebApp [routes db-conn api-web-app]
  component/Lifecycle

  (start [component]
    (assoc component :routes (create-app db-conn)))
  (stop [component]
    (assoc component :routes nil)))

(defn new-webapp []
  (map->WebApp {}))


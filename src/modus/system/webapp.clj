(ns modus.system.webapp
  (:require [com.stuartsierra.component :as component]
            [compojure.api.sweet :refer :all]
            [ring.middleware.defaults :refer [api-defaults wrap-defaults]]
            [ring.middleware.format-response :refer [wrap-restful-response]]
            [buddy.auth.middleware :refer [wrap-authorization]]
            [ring.middleware.json :refer [wrap-json-body]]
            [ring.util.response :as ring]
            [modus.system.authenticator :refer [wrap-authenticate-api-user-using-buddy]]
            [modus.api.webapp :refer [create-api-routes]]))

(defn- create-routes [api-routes]
  (routes
    (ANY "/api/*" [] api-routes)
    (ANY "*" []
      (ring/not-found "Not found"))))

(defn create-app [db-conn api-web-app]
  (api
    {:swagger
     {:ui   "api/v1/api-docs"
      :spec "/swagger.json"
      :data {:info {:title       "Sample Api"
                    :description "Compojure Api sample application"}
             :tags [{:name "accounts" :description "Account Api"}
                    {:name "teams" :description "Team Api"}
                    {:name "projects" :description "Project Api"}
                    {:name "task_lists" :description "Task List Api"}
                    {:name "tasks" :description "Task Api"}]}}}
    (swagger-routes)
    (create-routes
      (-> (create-api-routes api-web-app)
          (wrap-authenticate-api-user-using-buddy db-conn)
          (wrap-json-body {:keywords? true :bigdecimals? true})
          (wrap-restful-response)
          (wrap-defaults api-defaults))))
  )

(defrecord WebApp [routes db-conn api-web-app]
  component/Lifecycle

  (start [component]
    (assoc component :routes (create-app db-conn api-web-app)))
  (stop [component]
    (assoc component :routes nil)))

(defn new-webapp []
  (map->WebApp {}))


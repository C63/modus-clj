(ns modus.system.restructure
  (:require [compojure.api.meta :refer [restructure-param]]))

(defmethod restructure-param :auth-account
  [_ binding acc]
  (update-in acc [:letks] into [binding `(:identity ~'+compojure-api-request+)]))

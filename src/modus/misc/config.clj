(ns modus.misc.config
  (:require [clojure.string :as str]))

(defn int-system-property [property-name default-value]
  (-> (System/getProperty property-name (str default-value))
      (Integer/parseInt 10)))

(defn boolean-system-property [property-name default-value]
  (-> (System/getProperty property-name (str default-value))
      (Boolean/parseBoolean)))

(def listen-port (int-system-property "LISTEN_PORT" 8080))

(def dev-mode? (boolean-system-property "DEV_MODE" true))

(def secure-site? (boolean-system-property "SECURE_SITE" false))

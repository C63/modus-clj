(ns modus.misc.config
  (:require [clojure.string :as str]))

(defn int-system-property [property-name default-value]
  (-> (System/getProperty property-name (str default-value))
      (Integer/parseInt 10)))

(defn boolean-system-property [property-name default-value]
  (-> (System/getProperty property-name (str default-value))
      (Boolean/parseBoolean)))

(def listen-port (int-system-property "LISTEN_PORT" 8080))

(def sign-in-secret "modus-c63-sign-in-secret-need-to-change-to-better-secret")


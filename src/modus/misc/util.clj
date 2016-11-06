(ns modus.misc.util
  (:require [camel-snake-kebab.core :refer [->snake_case]]
            [clojure.string :as str]
            [clojure.walk :refer [postwalk]])
  (:import (java.util UUID)
           (org.joda.time DateTime ReadableInstant)))

(defn ->kebab-case [kw]
  (if (or (string? kw)
          (keyword? kw))
    (keyword (clojure.string/replace (name kw) \_ \-))
    kw))

(defn map-all-keys
  [f m]
  (letfn [(transform-key [[k v]] [(f k) v])
          (transform-maps [form]
            (if (map? form)
              (into {} (map transform-key form))
              form))]
    (postwalk transform-maps m)))

(defn map->kebab-case
  "Recursively transforms all map keys to keywords in kebab-case"
  [m]
  (map-all-keys ->kebab-case m))

(def underscorify-map (partial map-all-keys ->snake_case))

(defn relation->kebab-case
  "convert results read from db (relation) to kebab-case"
  [r]
  (map map->kebab-case r))

(defn generate-uuid
  []
  (UUID/randomUUID))

(defn str->uuid
  [s]
  (try
    (UUID/fromString s)
    (catch Exception _
      nil)))

(defn str->long
  [s]
  (try
    (Long/parseLong s)
    (catch Exception _
      nil)))

(defn str->double
  [s]
  (try
    (Double/parseDouble s)
    (catch Exception _
      nil)))

(defn str->int
  [s]
  (try
    (Integer/parseInt s)
    (catch Exception _
      nil)))

(defn truncate [s max-length]
  (when s
    (subs s 0 (min max-length (count s)))))

(defn query-response [query-result]
  (when query-result
    (into {} (for [[k v] query-result]
               [(->kebab-case k) v]))))

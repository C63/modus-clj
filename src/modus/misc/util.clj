(ns modus.misc.util
  (:require [camel-snake-kebab.core :refer [->snake_case]]
            [clojure.string :as str])
  (:import (java.util UUID)
           (org.joda.time DateTime ReadableInstant)))

(defn ->kebab-case [kw]
  (if (or (string? kw)
          (keyword? kw))
    (keyword (clojure.string/replace (name kw) \_ \-))
    kw))

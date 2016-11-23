(defproject modus "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [http-kit "2.2.0"]
                 [ring/ring-defaults "0.2.1"]
                 [ring/ring-json "0.4.0"]
                 [com.stuartsierra/component "0.3.1"]
                 [org.postgresql/postgresql "9.4.1211"]
                 [com.zaxxer/HikariCP "2.5.1"]
                 [org.clojure/java.jdbc "0.6.1"]
                 [buddy "1.1.0"]
                 [org.mindrot/jbcrypt "0.3m"]
                 [org.clojure/tools.logging "0.3.1"]
                 [camel-snake-kebab "0.4.0"]
                 [clj-time "0.12.1"]
                 [clj-http "3.3.0"
                  :exclusions [org.apache.httpcomponents/httpcore]]
                 [ring-middleware-format "0.7.0"]
                 [ring-cors "0.1.8"]
                 [cheshire "5.6.3"]
                 [metosin/compojure-api "1.1.9"]
                 [com.layerware/hugsql "0.4.7"]
                 [prismatic/schema "1.1.3"]
                 [environ "1.1.0"]]
  :source-paths ["src"]
  :plugins [[lein-ring "0.9.7"]
            [lein-figwheel "0.5.8"
             :exclusions [org.clojure/clojure org.clojure/tools.reader ring/ring-core]]
            [com.github.metaphor/lein-flyway "1.0"]
            [lein-cljsbuild "1.1.4"
             :exclusions [org.clojure/clojure]]
            [lein-environ "1.1.0"]
            [lein-licenses "0.2.0"]]
  :flyway {:driver    "org.postgresql.Driver"
           :locations ["filesystem:./db/migration"]}
  :profiles {:repl    {:plugins [[cider/cider-nrepl "0.13.0"]]}
             :dev     {:dependencies [[org.clojure/tools.namespace "0.2.11"]
                                      [ring/ring-mock "0.3.0"]
                                      [org.clojure/tools.nrepl "0.2.12"]
                                      [org.clojure/test.check "0.9.0"]]
                       :source-paths ["dev"]
                       :jvm-opts     ["-Dapple.awt.UIElement=true"
                                      "-DLISTEN_PORT=8080"]
                       :flyway       {:url      "jdbc:postgresql://localhost:5432/modus"
                                      :user     ~(System/getProperty "user.name")
                                      :password ""}
                       }
             :dev_sb  {:jvm-opts ["-DLISTEN_PORT=8080"]
                       :flyway   {:url      "jdbc:postgresql://modus-monolith-c63-dev.c8epalgxmd5k.eu-central-1.rds.amazonaws.com:5432/modus_dev"
                                  :user     "modus_c63_dev"
                                  :password "modus-c63-dev"}}
             :uberjar {:aot :all}
             }
  )

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
                 [org.mindrot/jbcrypt "0.3m"]
                 [compojure "1.5.1"]
                 [org.clojure/tools.logging "0.3.1"]
                 [camel-snake-kebab "0.4.0"]
                 [clj-time "0.12.1"]
                 [clj-http "3.3.0"
                  :exclusions [org.apache.httpcomponents/httpcore]]
                 [ring-middleware-format "0.7.0"]
                 [cheshire "5.6.3"]
                 [metosin/ring-http-response "0.8.0"]
                 ]
  :source-paths ["src"]
  :main modus.main
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
                                      "-DLISTEN_PORT=3000"]
                       :flyway       {:url      "jdbc:postgresql://localhost:5432/modus"
                                      :user     "modus"
                                      :password "modus"}
                       }
             :uberjar {:aot :all}
             }
  )

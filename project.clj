(defproject app "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.758"]
                 [org.clojure/data.json "1.0.0"]
                 [org.clojure/core.match "1.0.0"]
                 [compojure "1.6.2"]
                 [http-kit "2.4.0"]
                 [hiccups "0.3.0"]
                 [jayq "2.5.5"]
                 [cljs-ajax "0.7.5"]
                 [ring/ring-defaults "0.3.2"]
                 [ring/ring-json "0.5.0"]
                 [ring-cors "0.1.13"]
                 [org.postgresql/postgresql "42.2.14"]
                 [migratus "1.2.8"]
                 [com.stuartsierra/component "1.0.0"]
                 [reloaded.repl "0.2.4"]]
  :plugins [[migratus-lein "0.7.3"]
            [lein-cljsbuild "1.1.8"]
            [lein-figwheel "0.5.18"]]
  :cljsbuild {:builds [{:id "crud"
                        :source-paths ["src"]
                        :figwheel true
                        :compiler {:output-to "resources/public/js/main.js"
                                   :optimizations :whitespace
                                   :pretty-print true}}]}
  :migratus {:store :database
             :migration-dir "resources/migrations"
             :db ~(get (System/getenv) "DATABASE_URL")}
  :main ^:skip-aot app.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :dev {:plugins []
                   :dependencies []
                   :source-paths ["dev"]}})

(defproject app "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [compojure "1.6.2"]
                 [http-kit "2.4.0"]
                 [ring/ring-defaults "0.3.2"]
                 [org.clojure/data.json "1.0.0"]
                 [clj-postgresql "0.7.0"]
                 [migratus "1.2.8"]
                 [com.stuartsierra/component "1.0.0"]
                 [reloaded.repl "0.2.4"]]
  :main ^:skip-aot app.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :dev {:plugins []
                   :dependencies []
                   :source-paths ["dev"]}})

(ns app.core
  (:gen-class)
  (:require [app.db :as db]
            [app.state :as state]
            [app.patients-service :as service]
            [org.httpkit.server :as server]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer :all]
            [com.stuartsierra.component :as component]
            [clojure.pprint :as pp]
            [clojure.string :as str]
            [clojure.data.json :as json]
            [clojure.java.jdbc :as jdbc]))

(defn list-page [req]
  {:status  200
   :headers {"Content-Type" "text/json"}
   :body (str (json/write-str (service/get-all-patients (:page (:params req)) 10)))})

(defroutes app-routes
  (GET "/" [] list-page)
  (route/not-found "Error, page not found!"))

(defn- start-http-server [port]
  (server/run-server (wrap-defaults #'app-routes site-defaults) {:port port}))

(defn- stop-server [server]
  (when server
    (server)))

(defrecord Database [uri connection]
  component/Lifecycle

  (start [component]
    (println (str "Starting database at " uri))
    (let [connection (db/connect-to-database uri)]
      (state/set-db-connection connection)
      (assoc component :connection connection)))

  (stop [component]
    (println "Stopping database")
    (db/close-db-connection (:connection component))
    (state/remove-db-connection)
    (dissoc component :connection)))

(defrecord HttpServer [port server]
  component/Lifecycle

  (start [component]
    (println (str "Starting server at localhost" ":" port))
    (let [server (start-http-server port)]
      (assoc component :server server)))

  (stop [component]
    (println "Stopping server")
    (stop-server (:server component))
    (dissoc component :server)))

(defn new-database [uri]
  (map->Database {:uri uri}))

(defn new-http-server [port]
  (map->HttpServer {:port port}))

(defn crud-system [config-options]
  (let [{:keys [db_uri http_port]} config-options]
    (component/system-map
     :db (new-database db_uri)
     :server (new-http-server http_port))))

(defn create-system []
  (crud-system {:db_uri (System/getenv "DATABASE_URL")
                :http_port 8080}))

(defn -main
  [& args]
  (.start (create-system)))

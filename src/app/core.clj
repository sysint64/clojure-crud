(ns app.core
  (:gen-class)
  (:require [org.httpkit.server :as server]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer :all]
            [com.stuartsierra.component :as component]
            [clojure.pprint :as pp]
            [clojure.string :as str]
            [clojure.data.json :as json]
            [clj-postgresql.core :as pg]
            [clojure.java.jdbc :as jdbc]))

(defrecord AppState [db-connection])

(def app-state (atom (AppState. nil)))

(defn db-connection []
  (:db-connection @app-state))

(defn list-page [req]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body (let [connection (db-connection)]
           (jdbc/query connection ["SELECT * FROM patients"]))})

(defroutes app-routes
  (GET "/" [] list-page)
  (route/not-found "Error, page not found!"))

(defn- start-http-server [port]
  (server/run-server (wrap-defaults #'app-routes site-defaults) {:port port}))

(defn- stop-server [server]
  (when server
    (server)))

(defn- connect-to-database [host port user name password]
  (pg/pool :host host
           :port port
           :user user
           :dbname name
           :password password))

(defn- close-db-connection [connection]
  (pg/close! connection))

(defrecord Database [host port user name password, connection]
  component/Lifecycle

  (start [component]
    (println (str "Starting database at " host ":" port))
    (let [connection (connect-to-database host port user name password)]
      (swap! app-state (fn [it] (assoc it :db-connection connection)))
      (assoc component :connection connection)))

  (stop [component]
    (println "Stopping database")
    (close-db-connection (:connection component))
    (swap! app-state (fn [it] (dissoc it :db-connection)))
    (dissoc component :connection)))

(defrecord HttpServer [port]
  component/Lifecycle

  (start [component]
    (println (str "Starting server at localhost" ":" port))
    (let [server (start-http-server port)]
      (assoc component :server server)))

  (stop [component]
    (println "Stopping server")
    (stop-server (:server component))
    (dissoc component :server)))

(defn new-database [host port user name password]
  (map->Database {:host host :port port :user user :name name :password password}))

(defn new-http-server [port]
  (map->HttpServer {:port port}))

(defn crud-system [config-options]
  (let [{:keys [db_host db_port db_user db_name db_password http_port]} config-options]
    (component/system-map
     :db (new-database db_host db_port db_user db_name db_password)
     :server (new-http-server http_port))))

(defn create-system []
  (crud-system {:db_host "localhost"
                :db_port 5432
                :db_user "clojure_crud"
                :db_name "clojure_crud"
                :db_password "123321"
                :http_port 8080}))

(defn -main
  [& args]
  (.start (create-system)))

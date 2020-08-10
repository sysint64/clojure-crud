(ns app.core
  (:gen-class)
  (:require [app.db :as db]
            [app.state :as state]
            [app.api :as api]
            [app.errors-middleware :as errors-middleware]
            [org.httpkit.server :as server]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer :all]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [ring.middleware.cors :refer [wrap-cors]]
            [com.stuartsierra.component :as component]
            [clojure.string :as str]))

(defroutes app-routes
  (context "/api" [] (api/api-routes))
  (route/resources "/")
  (route/not-found "Error, page not found!"))

(defn- start-http-server [port]
  (server/run-server (wrap-cors
                      (wrap-json-response
                       (wrap-json-body
                        (errors-middleware/wrap-error-handler
                         (wrap-defaults #'app-routes api-defaults))))
                      :access-control-allow-origin [#".*"]
                      :access-control-allow-methods [:get :put :post :delete])
                     {:port port}))

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

(defn crud-system [config-options]
  (let [{:keys [db_uri http_port]} config-options]
    (component/system-map
     :db     (map->Database   {:uri  db_uri})
     :server (map->HttpServer {:port http_port}))))

(defn create-system []
  (crud-system {:db_uri    (System/getenv "DATABASE_URL")
                :http_port 8080}))

(defn -main
  [& args]
  (.start (create-system)))

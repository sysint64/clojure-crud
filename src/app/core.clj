(ns app.core
  (:gen-class)
  (:require [org.httpkit.server :as server]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer :all]
            [clojure.pprint :as pp]
            [clojure.string :as str]
            [clojure.data.json :as json]
            [com.stuartsierra.component :as component]))

(defn- start-server [handler port]
  (let [server (server/run-server handler {:port port})]
    (println (str "Started serve on localhos: " port))
    server))

(defn- stop-server [server]
  (when server
    (server)))

(defn list-page [req]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    "Hello world!"})

(defroutes app-routes
  (GET "/" [] list-page)
  (route/not-found "Error, page not found!"))

(defrecord App []
  component/Lifecycle
  (start [this]
    (assoc this :server (start-server (wrap-defaults #'app-routes site-defaults) 8080)))
  (stop [this]
    (stop-server (:server this))
    (dissoc this :server)))

(defn create-system []
  (App.))

(defn -main
  [& args]
  (.start (create-system)))

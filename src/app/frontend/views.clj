(ns app.frontend.views
  (:require [compojure.core :refer :all]
            [compojure.route :as route]))

(defn index [request]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    "Hello world!"})

(defn frontend-routes []
  (routes
   (GET "/" [] index)))

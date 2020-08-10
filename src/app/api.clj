(ns app.api
  (:require [app.patients-service :as service]
            [compojure.core :refer :all]
            [compojure.route :as route]))

(defn get-patients [request]
  {:status  200
   :headers {"Content-Type" "application/json"}
   :body    (let [page (:page (:params request))]
              (service/get-all-patients page 10))})

(defn get-patient-by-id [request id]
  {:status  200
   :headers {"Content-Type" "application/json"}
   :body    (service/get-patient-by-id id)})

(defn update-patient-by-id [request id]
  {:status  200
   :headers {"Content-Type" "application/json"}
   :body    (let [body (:body request)]
              (service/update-patient-by-id id body))})

(defn delete-patient-by-id [request id]
  {:status  200
   :headers {"Content-Type" "application/json"}
   :body    (service/delete-patient-by-id id)})

(defn post-patients [request]
  {:status  200
   :headers {"Content-Type" "application/json"}
   :body    (let [body (:body request)]
              (service/create-patient body))})

(defn put-patients [request]
  {:status  200
   :headers {"Content-Type" "application/json"}
   :body    (let [body (:body request)]
              (service/create-patient body))})

(defn search-patients [request]
  {:status  200
   :headers {"Content-Type" "application/json"}
   :body    (let [page (:page (:params request))
                  key  (:key  (:params request))]
              (service/search-patients key page 10))})

(defn api-routes []
  (routes
   (GET "/patients/" [] get-patients)
   (POST "/patients" [] post-patients)
   (GET "/patients/:id{[0-9]+}" [id :as request] (get-patient-by-id request id))
   (PUT "/patients/:id{[0-9]+}" [id :as request] (update-patient-by-id request id))
   (DELETE "/patients/:id{[0-9]+}" [id :as request] (delete-patient-by-id request id))
   (GET "/search-patients" [] search-patients)))

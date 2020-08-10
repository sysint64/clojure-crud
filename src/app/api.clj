(ns app.api
  (:require [app.patients-service :as service]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.util.response :refer [response]]
            [clojure.stacktrace]
            [clojure.core.match :refer [match]]
            [clojure.data.json :as json]))

(defn- create-validation-error-json [e]
  (let [error-messages (:error-messages (:data (Throwable->map e)))]
    {:error_code "validation_error" :errors error-messages}))

(defn- validation-error-body [e]
  {:status  400
   :headers {"Content-Type" "application/json"}
   :body    (create-validation-error-json e)})

(defn- not-found-error-body [e]
  {:status  404
   :headers {"Content-Type" "application/json"}
   :body    {:error_code "not_found"}})

(defn- internal-server-error-body [e]
  (clojure.stacktrace/print-stack-trace e)
  {:status  500
   :headers {"Content-Type" "application/json"}
   :body    {:error_code "internal_server_error"}})

(defn wrap-error-handler [handler]
  (fn [request]
    (try (handler request)
         (catch Exception e
           (let [type (:type (:data (Throwable->map e)))]
             (match type
                    :validation-exception (validation-error-body e)
                    :not-found-exception  (not-found-error-body e)
                    :else                 (internal-server-error-body e)))))))

(defn get-patients [req]
  {:status  200
   :headers {"Content-Type" "application/json"}
   :body    (let [page (:page (:params req))]
              (service/get-all-patients page 10))})

(defn post-patients [req]
  {:status  200
   :headers {"Content-Type" "application/json"}
   :body    (let [body (:body req)]
              (println body)
              (service/create-patient body))})

(defn search-patients [req]
  {:status  200
   :headers {"Content-Type" "application/json"}
   :body    (let [page (:page (:params req))
                  key  (:key  (:params req))]
              (service/search-patients key page 10))})

(defn api-routes []
  (routes
   (GET "/patients/" [] get-patients)
   (POST "/patients" [] post-patients)
   (GET "/search-patients" [] search-patients)))

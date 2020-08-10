(ns app.api
  (:require [app.patients-service :as service]
            [clojure.core.match :refer [match]]
            [clojure.data.json :as json]))

(defn- create-validation-error-json [e]
  (let [error-messages (:error-messages (:data (Throwable->map e)))]
    (str (json/write-str {:error_code "validation_error" :errors error-messages}))))

(defn wrap-error-handler [handler]
  (fn [request]
    (try (handler request)
         (catch Exception e
           (let [type (:type (:data (Throwable->map e)))]
             (match type
                    :validation-exception {:status  400
                                           :headers {"Content-Type" "text/json"}
                                           :body    (create-validation-error-json e)}
                    :else                 {:status  500
                                           :headers {"Content-Type" "text/json"}
                                           :body    "{\"error_code\": \"internal_server_error\"}"}))))))

(defn get-patients [req]
  {:status  200
   :headers {"Content-Type" "text/json"}
   :body    (let [page (:page (:params req))]
              (str (json/write-str (service/get-all-patients page 10))))})

(defn search-patients [req]
  {:status  200
   :headers {"Content-Type" "text/json"}
   :body    (let [page (:page (:params req))
                  key  (:key  (:params req))]
              (str (json/write-str (service/search-patients key page 10))))})

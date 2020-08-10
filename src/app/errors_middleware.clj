(ns app.errors-middleware
  (:require [clojure.stacktrace]
            [clojure.core.match :refer [match]]))

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

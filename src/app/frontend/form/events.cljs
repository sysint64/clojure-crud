(ns app.frontend.form.events
  (:require [ajax.core :refer [POST]]
            [app.frontend.ui :as ui]
            [app.frontend.form.states :as states]
            [clojure.core.match :refer [match]]))

(declare on-inserted)
(declare on-insert-error)

(defn go-back []
  (.reload (. js/document -location)))

(defn on-insert-submit [event]
  (.preventDefault event)
  (let [data (ui/get-form-json "form")]
    (ui/set-component (states/insert-submitting data))
    (POST (str "/api/patients")
          {:params        data
           :format        :json
           :handler       on-inserted
           :error-handler (fn [error] (on-insert-error data error))})))

(defn base-handlers []
  )

(defn insert-loaded-handlers []
  (ui/add-event-listener "#form" "submit" on-insert-submit))

(defn on-inserted [response]
  (js/alert "Successfully inserted")
  (go-back))

(defn on-insert-validation-error [input errors]
  (ui/set-component (states/insert-loaded input errors))
  (base-handlers)
  (insert-loaded-handlers))

(defn on-insert-unknown-error [input]
  (ui/set-component (states/insert-error input))
  (base-handlers))

(defn on-insert-error [input error]
  (let [response (get error :response)]
    (match (get response "error_code")
           "validation_error" (on-insert-validation-error input (get response "errors"))
           :else              (on-insert-unknown-error input))))

(defn on-insert-loaded []
  (ui/set-component (states/insert-loaded nil nil))
  (base-handlers)
  (insert-loaded-handlers))

(defn on-insert-load []
  (base-handlers)
  (on-insert-loaded))

(defn on-update-load [id]
  (ui/set-component (states/loading))
  (base-handlers)
  ;; (GET (str "/api/patients/" id)
  ;;      {:handler       on-update-loaded
  ;;       :error-handler on-error})
  )

(ns app.frontend.form.events
  (:require [ajax.core :refer [GET POST PUT]]
            [app.frontend.ui :as ui]
            [app.frontend.form.states :as states]
            [clojure.core.match :refer [match]]))

(declare on-inserted)
(declare on-update-load)
(declare on-udpated)
(declare on-insert-error)
(declare on-update-error)

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

(defn on-update-submit [patient event]
  (.preventDefault event)
  (let [data (ui/get-form-json "form")
        id (get patient "id")]
    (ui/set-component (states/update-submitting data))
    (PUT (str "/api/patients/" id)
         {:params        data
          :format        :json
          :handler       on-udpated
          :error-handler (fn [error] (on-update-error data error))})))

(defn on-try-again-click [id event]
  (.preventDefault event)
  (on-update-load id))

(defn base-handlers []
  )

(defn update-loading-error-handlers [id]
  (ui/add-event-listener "#try-again" "click" (fn [event] (on-try-again-click id event))))

(defn insert-loaded-handlers []
  (ui/add-event-listener "#form" "submit" on-insert-submit))

(defn update-loaded-handlers [patient]
  (ui/add-event-listener "#form" "submit" (fn [event] (on-update-submit patient event))))

(defn on-inserted [response]
  (js/alert "Successfully inserted")
  (go-back))

(defn on-udpated [response]
  (js/alert "Successfully updated")
  (go-back))

(defn on-insert-validation-error [input errors]
  (ui/set-component (states/insert-loaded input errors))
  (base-handlers)
  (insert-loaded-handlers))

(defn on-insert-unknown-error [input]
  (ui/set-component (states/insert-error input))
  (base-handlers))

(defn on-update-validation-error [input errors]
  (ui/set-component (states/update-loaded input errors))
  (base-handlers)
  (update-loaded-handlers input))

(defn on-update-unknown-error [input]
  (ui/set-component (states/update-error input))
  (base-handlers))

(defn on-insert-error [input error]
  (let [response (get error :response)]
    (match (get response "error_code")
           "validation_error" (on-insert-validation-error input (get response "errors"))
           :else              (on-insert-unknown-error input))))

(defn on-update-error [input error]
  (let [response (get error :response)]
    (match (get response "error_code")
           "validation_error" (on-update-validation-error input (get response "errors"))
           :else              (on-update-unknown-error input))))

(defn on-insert-loaded []
  (ui/set-component (states/insert-loaded nil nil))
  (base-handlers)
  (insert-loaded-handlers))

(defn on-update-loaded [patient]
  (ui/set-component (states/update-loaded patient nil))
  (base-handlers)
  (update-loaded-handlers patient))

(defn on-insert-load []
  (base-handlers)
  (on-insert-loaded))

(defn on-update-loading-error [id error]
  (ui/set-component (states/error))
  (base-handlers)
  (update-loading-error-handlers id))

(defn on-update-load [id]
  (ui/set-component (states/loading))
  (base-handlers)
  (GET (str "/api/patients/" id)
       {:handler       on-update-loaded
        :error-handler (fn [error] (on-update-loading-error id error))}))

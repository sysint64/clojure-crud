(ns app.frontend.list.events
  (:require [ajax.core :refer [GET]]
            [app.frontend.ui :as ui]
            [app.frontend.list.states :as states]))

(declare on-load)

(defn on-insert-click [event]
  (js/alert "Hello world!")
  (.preventDefault event))

(defn on-try-again-click [event]
  (on-load)
  (.preventDefault event))

(defn loading-error-handlers []
  (ui/add-event-listener "try-again" "click" on-try-again-click))

(defn base-handlers []
  (ui/add-event-listener "insert" "click" on-insert-click))

(defn on-loaded [response]
  (ui/set-component (states/loaded response)))

(defn on-error [error]
  (ui/set-component (states/error))
  (base-handlers)
  (loading-error-handlers))

(defn on-load []
  (ui/set-component (states/loading))
  (base-handlers)
  (GET "/api/patients/" {:handler       on-loaded
                         :error-handler on-error}))

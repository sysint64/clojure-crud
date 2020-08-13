(ns app.frontend.list.events
  (:require [ajax.core :refer [GET]]
            [app.frontend.ui :as ui]
            [app.frontend.list.states :as states]
            [app.frontend.details.events :as details]
            [app.frontend.form.events :as form]))

(declare on-load)
(declare on-search)
(declare on-load-by-query)

(defn on-insert-click [event]
  (form/on-insert-load)
  (.preventDefault event))

(defn on-try-again-click [event]
  (on-load)
  (.preventDefault event))

(defn on-search-click [event]
  (let [key (. (.getElementById js/document "search-input") -value)]
    (on-search key))
  (.preventDefault event))

(defn on-next-page-click [event]
  (let [query (. (. (.getElementById js/document "next-page") -dataset) -query)]
    (on-load-by-query query))
  (.preventDefault event))

(defn on-prev-page-click [event]
  (let [query (. (. (.getElementById js/document "prev-page") -dataset) -query)]
    (on-load-by-query query))
  (.preventDefault event))

(defn on-patient-link-click [event]
  (.preventDefault event)
  (let [id (. (. (. event -target) -dataset) -id)]
    (details/on-load id)))

(defn loading-error-handlers []
  (ui/add-event-listener "#try-again" "click" on-try-again-click))

(defn loaded-handlers []
  (ui/add-event-listener "#search-button" "click" on-search-click)
  (ui/add-event-listener "#next-page" "click" on-next-page-click)
  (ui/add-event-listener "#prev-page" "click" on-prev-page-click)
  (ui/add-event-listener ".patient-link" "click" on-patient-link-click))

(defn base-handlers []
  (ui/add-event-listener "#insert" "click" on-insert-click))

(defn on-loaded [query response]
  (ui/set-component (states/loaded query response))
  (base-handlers)
  (loaded-handlers))

(defn on-error [error]
  (ui/set-component (states/error))
  (base-handlers)
  (loading-error-handlers))

(defn on-load-by-query [query]
  (ui/set-component (states/loading))
  (base-handlers)
  (GET query
       {:handler       (fn [response] (on-loaded query response))
        :error-handler on-error}))

(defn on-load []
  (let [query "/api/patients/"]
    (on-load-by-query query)))

(defn on-search [key]
  (let [query (str "/api/search-patients?key=" key)]
    (on-load-by-query query)))

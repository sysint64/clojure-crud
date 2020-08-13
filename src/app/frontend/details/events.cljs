(ns app.frontend.details.events
  (:require [ajax.core :refer [GET DELETE]]
            [app.frontend.ui :as ui]
            [app.frontend.details.states :as states]
            [app.frontend.form.events :as form]))

(declare on-load)
(declare on-delete)

(defn on-try-again-click [event]
  (on-load)
  (.preventDefault event))

(defn on-try-delete-again-click [id event]
  (on-delete id)
  (.preventDefault event))

(defn on-delete-click [patient event]
  (.preventDefault event)
  (if (js/confirm "Are you sure you want to delete the patient?")
    (on-delete (get patient "id"))))

(defn on-edit-click [patient event]
  (.preventDefault event)
  (form/on-update-load (get patient "id")))

(defn go-back []
  ;; TODO: Couldn't manage to resolve circular dependency :(
  (.reload (. js/document -location)))

(defn on-go-back-click [event]
  (.preventDefault event)
  (go-back))

(defn loaded-handlers [patient]
  (ui/add-event-listener "#edit" "click"
                         (fn [event] (on-edit-click patient event)))
  (ui/add-event-listener "#delete" "click"
                         (fn [event] (on-delete-click patient event))))

(defn base-handlers []
  (ui/add-event-listener "#go-back-link" "click" on-go-back-click))

(defn loading-error-handlers []
  (ui/add-event-listener "#try-again" "click" on-try-again-click))

(defn deleting-error-handlers [id]
  (ui/add-event-listener "#try-delete-again" "click"
                         (fn [event] (on-try-delete-again-click id event))))

(defn on-loaded [response]
  (ui/set-component (states/loaded response))
  (base-handlers)
  (loaded-handlers response))

(defn on-error [error]
  (ui/set-component (states/error))
  (base-handlers)
  (loading-error-handlers))

(defn on-deleted [response]
  (js/alert "Successfully deleted")
  (go-back))

(defn on-delete-error [id error]
  (ui/set-component (states/deleting-error))
  (base-handlers)
  (deleting-error-handlers id))

(defn on-delete [id]
  (ui/set-component (states/deleting))
  (base-handlers)
  (DELETE (str "/api/patients/" id)
          {:handler       on-deleted
           :error-handler (fn [error] (on-delete-error id error))}))

(defn on-load [id]
  (ui/set-component (states/loading))
  (base-handlers)
  (GET (str "/api/patients/" id)
       {:handler       on-loaded
        :error-handler on-error}))

(ns app.frontend.form.events
  (:require [ajax.core :refer [POST]]
            [app.frontend.ui :as ui]
            [app.frontend.form.states :as states]))

(defn base-handlers []
  )

(defn insert-loaded-handlers []
  )

(defn on-insert-loaded []
  (ui/set-component (states/insert-loaded))
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

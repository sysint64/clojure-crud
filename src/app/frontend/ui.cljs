(ns app.frontend.ui)

(defn set-component [component]
  (-> js/document
      (.getElementById "root-container")
      (.-innerHTML)
      (set! component)))

(defn add-event-listener [id type handler]
  (.addEventListener
   (.getElementById js/document id) type handler false))

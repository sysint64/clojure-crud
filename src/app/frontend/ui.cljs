(ns app.frontend.ui)

(defn set-component [component]
  (-> js/document
      (.getElementById "root-container")
      (.-innerHTML)
      (set! component)))

(defn add-event-listener [selector type handler]
  (reduce
   (fn [_, item] (.addEventListener item type handler false))
   nil
   (.querySelectorAll js/document selector)))

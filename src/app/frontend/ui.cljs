(ns app.frontend.ui
  (:require [clojure.string :as str]))

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

(defn get-form-json [id]
  (let [elements (. (.getElementById js/document id) -elements)]
    (reduce (fn [data, element]
              (assoc data (. element -name) (. element -value)))
            {}
            (filter (fn [element] (not (str/blank? (. element -name)))) elements))))

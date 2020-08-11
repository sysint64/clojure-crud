(ns app.transforms)

;; https://stackoverflow.com/questions/9406156/clojure-convert-hash-maps-key-strings-to-keywords
(defn wrap-keyword-map [map]
  (into {}
        (for [[k v] map]
          [(keyword k) v])))

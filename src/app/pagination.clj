(ns app.pagination)

(defn parse-page [page]
  (try (let [page (Integer/parseInt page)]
         (if (< page 1) 1 page))
       (catch Exception e 1)))

(defn next-page [result page page-size]
  (if (= (count result) (inc page-size))
    (inc page)
    nil))

(defn get-result [result page-size]
  (if (= (count result) (inc page-size))
    (drop-last result)
    result))

(defn offset [page page-size]
  (* (dec page) page-size))

(defn limit [page-size]
  (inc page-size))

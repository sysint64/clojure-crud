(ns app.patients-service
  (:require [app.patients-repository :as repository]
            [app.state :as state]
            [app.validations :refer :all]
            [app.pagination :as pagination]))

;; https://stackoverflow.com/questions/9406156/clojure-convert-hash-maps-key-strings-to-keywords
(defn- wrap-keyword-map [map]
  (into {}
        (for [[k v] map]
          [(keyword k) v])))

(def patient-input-spec
  {:first_name        [str-not-blank-validator (fn [value] (min-length-validator value 3))]
   :last_name         [str-not-blank-validator (fn [value] (min-length-validator value 3))]
   :middle_name       [str-not-blank-validator (fn [value] (min-length-validator value 3))]
   :sex               [str-not-blank-validator
                       (fn [value] (enum-validator value ["MALE", "FEMALE", "PREFER_NOT_SAY"]))]
   :date_of_birth     [str-not-blank-validator]
   :address           [str-not-blank-validator (fn [value] (min-length-validator value 3))]
   :oms_policy_number [str-not-blank-validator digits-only-validator
                       (fn [value] (exact-length-validator value 3))]})

(defn create-patient [input]
  (let [connection (state/db-connection)
        input (wrap-keyword-map input)]
    (validate-by-spec input patient-input-spec)
    (repository/insert-patient connection input)))

(defn get-all-patients [page page-size]
  (let [connection (state/db-connection)
        page (pagination/parse-page page)]
    (let [result (repository/get-all-patients connection
                                              (pagination/offset page page-size)
                                              (pagination/limit page-size))]
      {:next-page (pagination/next-page result page page-size)
       :result    (pagination/get-result result page-size)})))

(defn search-patients [key page page-size]
  (let [connection (state/db-connection)
        page (pagination/parse-page page)]
    (let [result (repository/search-patients connection key
                                             (pagination/offset page page-size)
                                             (pagination/limit page-size))]
      {:next-page (pagination/next-page result page page-size)
       :result    (pagination/get-result result page-size)})))

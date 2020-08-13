(ns app.patients-service
  (:require [app.patients-repository :as repository]
            [app.state :as state]
            [app.validations :refer :all]
            [app.pagination :as pagination]
            [app.transforms :as transforms]))

(defn get-id-or-not-found [id]
  (try (Integer/parseInt id)
       (catch Exception e
         (throw (ex-info "Not found exception"
                         {:type :not-found-exception})))))

(defn get-or-not-found [element]
  (when (nil? element)
    (throw (ex-info "Not found exception"
                    {:type :not-found-exception})))
  element)

(def patient-input-spec
  {:first_name        [str-not-blank-validator (fn [value] (min-length-validator value 3))]
   :last_name         [str-not-blank-validator (fn [value] (min-length-validator value 3))]
   :middle_name       [str-not-blank-validator (fn [value] (min-length-validator value 3))]
   :sex               [str-not-blank-validator
                       (fn [value] (enum-validator value ["MALE", "FEMALE", "PREFER_NOT_SAY"]))]
   :date_of_birth     [str-not-blank-validator date-validator]
   :address           [str-not-blank-validator (fn [value] (min-length-validator value 3))]
   :oms_policy_number [str-not-blank-validator digits-only-validator
                       (fn [value] (exact-length-validator value 3))]})

(defn create-patient [input]
  (let [connection (state/db-connection)
        input (transforms/wrap-keyword-map input)]
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

(defn get-patient-by-id [id]
  (let [connection (state/db-connection)
        id (get-id-or-not-found id)
        patient (repository/get-patient-by-id connection id)]
    (get-or-not-found patient)))

(defn update-patient-by-id [id input]
  (let [connection (state/db-connection)
        id (get-id-or-not-found id)
        input (transforms/wrap-keyword-map input)
        input (validate-by-spec input patient-input-spec)
        patient (repository/update-patient-by-id connection id input)]
    (get-or-not-found patient)))

(defn delete-patient-by-id [id]
  (let [connection (state/db-connection)
        id (get-id-or-not-found id)]
    (repository/delete-patient-by-id connection id)
    {:success true}))

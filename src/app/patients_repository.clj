(ns app.patients-repository
  (:require [clojure.java.jdbc :as jdbc]
            [clojure.string :as str]))

(defn- replace-date-of-birth [it]
  (when (not (nil? it))
    (dissoc
     (assoc it :date_of_birth
            (:date_of_birth_str it))
     :date_of_birth_str)))

(defn get-all-patients [connection offset limit]
  (reduce (fn [result item] (conj result (replace-date-of-birth item))) []
          (jdbc/reducible-query connection [(str
                                             "SELECT"
                                             "  *, to_char(date_of_birth, 'DD.MM.YYYY') AS date_of_birth_str "
                                             "FROM patients OFFSET ? LIMIT ?") offset limit])))

(defn get-patient-by-id [connection id]
  (jdbc/query connection [(str
                           "SELECT"
                           "  *, to_char(date_of_birth, 'DD.MM.YYYY') AS date_of_birth_str "
                           "FROM patients WHERE id = ?") id]
              {:result-set-fn (fn [it] (replace-date-of-birth (first it)))}))

(defn search-patients [connection key offset limit]
  (reduce (fn [result item] (conj result (replace-date-of-birth item))) []
          (jdbc/reducible-query connection [(str
                                             "SELECT"
                                             "  *, to_char(date_of_birth, 'DD.MM.YYYY') AS date_of_birth_str "
                                             "FROM patients "
                                             "WHERE "
                                             "  lower(first_name || last_name || middle_name || address || "
                                             "      oms_policy_number) LIKE '%' || lower(?::TEXT) || '%' "
                                             "OFFSET ? LIMIT ?") key offset limit])))

(defn delete-patient-by-id [connection id]
  (jdbc/execute! connection ["DELETE FROM patients WHERE id = ?" id]))

(defn insert-patient [connection values]
  (jdbc/query connection [(str
                           "INSERT INTO patients (first_name, last_name, middle_name, sex, "
                           "                      date_of_birth, address, oms_policy_number) "
                           "VALUES (?, ?, ?, ?::sex_t, to_date(?, 'DD.MM.YYYY'), ?, ?)"
                           "RETURNING *, to_char(date_of_birth, 'DD.MM.YYYY') as date_of_birth_str")
                          (:first_name values)
                          (:last_name values)
                          (:middle_name values)
                          (:sex values)
                          (:date_of_birth values)
                          (:address values)
                          (:oms_policy_number values)]
              {:result-set-fn (fn [it] (replace-date-of-birth (first it)))}))

(defn update-patient-by-id [connection id, values]
  (jdbc/query connection [(str
                           "UPDATE patients SET"
                           "  first_name = ?,"
                           "  last_name = ?,"
                           "  middle_name = ?,"
                           "  sex = ?::sex_t,"
                           "  date_of_birth = to_date(?, 'DD.MM.YYYY'),"
                           "  address = ?,"
                           "  oms_policy_number = ?"
                           "WHERE id = ?"
                           "RETURNING id")
                          (:first_name values)
                          (:last_name values)
                          (:middle_name values)
                          (:sex values)
                          (:date_of_birth values)
                          (:address values)
                          (:oms_policy_number values)
                          id]
              {:result-set-fn first}))

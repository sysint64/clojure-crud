(ns app.patients-repository-test
  (:require [app.patients-repository :refer :all]
            [app.db :as db]
            [clojure.java.jdbc :as jdbc]
            [clojure.test :refer :all]))

(def connection (db/connect-to-database (System/getenv "DATABASE_URL")))

(defn- clean-up []
  (jdbc/execute! connection ["DELETE FROM patients"]))

(def fixtures [{:first_name "Ivan"
                :last_name "Ivanov"
                :middle_name "Ivanovich"
                :sex "MALE"
                :date_of_birth "08.04.1991"
                :address "st. Bolshevistskaya"
                :oms_policy_number "123321"}
               {:first_name "Konstantin"
                :last_name "KK"
                :middle_name "Andreevish"
                :sex "MALE"
                :date_of_birth "08.08.1998"
                :address "st. Lenina"
                :oms_policy_number "123123"}
               {:first_name "Anastasiya"
                :last_name "Zaharove"
                :middle_name "Viktorovna"
                :sex "FEMALE"
                :date_of_birth "02.12.1992"
                :address "st. Krasniy Prospect"
                :oms_policy_number "888888888"}])

(defn- test-fixture [result idx]
  (let [fixture (get fixtures idx)]
    (is (not (nil? (:id result))))
    (is (= (:first_name fixture) (:first_name result)))
    (is (= (:last_name fixture) (:last_name result)))
    (is (= (:middle_name fixture) (:middle_name result)))
    (is (= (:sex fixture) (:sex result)))
    (is (= (:date_of_birth fixture) (:date_of_birth result)))
    (is (= (:address fixture) (:address result)))
    (is (= (:oms_policy_number fixture) (:oms_policy_number result)))))

(deftest insert-patient-test
  (testing "insert"
    (clean-up)
    (let [patient (insert-patient connection (get fixtures 0))]
      (test-fixture patient 0))))

(deftest update-patient-test
  (testing "update patient"
    (clean-up)
    (let [insert-data (insert-patient connection (get fixtures 1))
          update-data (update-patient-by-id connection (:id insert-data) (get fixtures 2))
          patient (get-patient-by-id connection (:id insert-data))]
      (is (= (:id insert-data) (:id patient)))
      (is (= (:id update-data) (:id patient)))
      (test-fixture patient 2)))

  (testing "update patient nil"
    (clean-up)
    (let [update-data (update-patient-by-id connection 1 (get fixtures 2))]
      (is (nil? update-data)))))

(deftest get-patient-by-id-test
  (testing "get-patient-by-id"
    (clean-up)
    (let [insert-data (insert-patient connection (get fixtures 1))
          patient (get-patient-by-id connection (:id insert-data))]
      (is (= (:id insert-data) (:id patient)))
      (test-fixture patient 1)))

  (testing "not found"
    (clean-up)
    (let [patient (get-patient-by-id connection (:id 1))]
      (is (= nil patient)))))

(deftest delete-patient-by-id-test
  (testing "delete-patient-by-id-test"
    (clean-up)
    (let [insert-data (insert-patient connection (get fixtures 1))
          patient (delete-patient-by-id connection (:id insert-data))])))

(deftest get-all-patients-test
  (testing "get all patients"
    (clean-up)
    (let [patient1 (insert-patient connection (get fixtures 0))
          patient2 (insert-patient connection (get fixtures 1))
          patient3 (insert-patient connection (get fixtures 2))
          patients (get-all-patients connection 0 10)]
      (is (= (:id patient1) (:id (get patients 0))))
      (is (= (:id patient2) (:id (get patients 1))))
      (is (= (:id patient3) (:id (get patients 2))))
      (test-fixture (get patients 0) 0)
      (test-fixture (get patients 1) 1)
      (test-fixture (get patients 2) 2)))

  (testing "pagination"
    (clean-up)
    (let [patient1 (insert-patient connection (get fixtures 0))
          patient2 (insert-patient connection (get fixtures 1))
          patient3 (insert-patient connection (get fixtures 2))
          patients (get-all-patients connection 1 10)]
      (is (= (:id patient2) (:id (get patients 0))))
      (is (= (:id patient3) (:id (get patients 1))))
      (test-fixture (get patients 0) 1)
      (test-fixture (get patients 1) 2)))

  (testing "empty"
    (clean-up)
    (let [patient1 (insert-patient connection (get fixtures 0))
          patient2 (insert-patient connection (get fixtures 1))
          patient3 (insert-patient connection (get fixtures 2))
          patients (get-all-patients connection 10 10)]
      (is (empty? patients)))))

(deftest search-patients-test
  (testing "search"
    (clean-up)
    (let [patient1 (insert-patient connection (get fixtures 0))
          patient2 (insert-patient connection (get fixtures 1))
          patient3 (insert-patient connection (get fixtures 2))
          patients (search-patients connection "Iva" 0 10)]
      (is (= (:id patient1) (:id (get patients 0))))
      (test-fixture (get patients 0) 0)))

  (testing "search 2"
    (clean-up)
    (let [patient1 (insert-patient connection (get fixtures 0))
          patient2 (insert-patient connection (get fixtures 1))
          patient3 (insert-patient connection (get fixtures 2))
          patients (search-patients connection "123" 0 10)]
      (is (= (:id patient1) (:id (get patients 0))))
      (is (= (:id patient2) (:id (get patients 1))))
      (test-fixture (get patients 0) 0)
      (test-fixture (get patients 1) 1)))

  (testing "empty"
    (clean-up)
    (let [patient1 (insert-patient connection (get fixtures 0))
          patient2 (insert-patient connection (get fixtures 1))
          patient3 (insert-patient connection (get fixtures 2))
          patients (search-patients connection "aaasldfjaslkdj" 0 10)]
      (is (empty? patients)))))

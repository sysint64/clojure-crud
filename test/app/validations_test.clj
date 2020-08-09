(ns app.validations-test
  (:require [clojure.test :refer :all]
            [app.validations :refer :all]))

(deftest str-not-blank-validator-test
  (testing "blank str should be invalid"
    (let [[is_valid message] (str-not-blank-validator "")]
      (is (= false is_valid))
      (is (= "Can't be blank" message))))

  (testing "blank str should be invalid nil"
    (let [[is_valid message] (str-not-blank-validator nil)]
      (is (= false is_valid))
      (is (= "Can't be blank" message))))

  (testing "blank str should be valid"
    (let [[is_valid message] (str-not-blank-validator "Test")]
      (is (= true is_valid))
      (is (= nil message))))

  (testing "blank str should have custom message"
    (let [[is_valid message] (str-not-blank-validator "" :error-message "Field is require")]
      (is (= false is_valid))
      (is (= "Field is require" message)))))

(deftest min-length-validator-test
  (testing "min-length-validator invalid"
    (let [[is_valid message] (min-length-validator "Hello World!" 20)]
      (is (= false is_valid))
      (is (= "Min length is 20" message))))

  (testing "min-length-validator invalid nil"
    (let [[is_valid message] (min-length-validator nil 20)]
      (is (= false is_valid))
      (is (= "Min length is 20" message))))

  (testing "min-length-validator valid"
    (let [[is_valid message] (min-length-validator "Hello World!" 10)]
      (is (= true is_valid))
      (is (= nil message))))

  (testing "min-length-validator custom message"
    (let [[is_valid message] (min-length-validator "Hello World!" 20 :error-message "Too short message")]
      (is (= false is_valid))
      (is (= "Too short message" message)))))

(deftest exact-length-validator-test
  (testing "invalid"
    (let [[is_valid message] (exact-length-validator "Hello World!" 20)]
      (is (= false is_valid))
      (is (= "Length should be equals 20" message))))

  (testing "invalid nil"
    (let [[is_valid message] (exact-length-validator nil 20)]
      (is (= false is_valid))
      (is (= "Length should be equals 20" message))))

  (testing "valid"
    (let [[is_valid message] (exact-length-validator "Hello World!" 12)]
      (is (= true is_valid))
      (is (= nil message))))

  (testing "custom message"
    (let [[is_valid message] (exact-length-validator "Hello World!" 20 :error-message "Too short message")]
      (is (= false is_valid))
      (is (= "Too short message" message)))))

(deftest enum-validator-test
  (testing "enum-validator invalid"
    (let [[is_valid message] (enum-validator "hello" ["male", "female"])]
      (is (= false is_valid))
      (is (= "Value should be one of: 'male', 'female'" message))))

  (testing "enum-validator invalid nil"
    (let [[is_valid message] (enum-validator nil ["male", "female"])]
      (is (= false is_valid))
      (is (= "Value should be one of: 'male', 'female'" message))))

  (testing "enum-validator valid"
    (let [[is_valid message] (enum-validator "male" ["male", "female"])]
      (is (= true is_valid))
      (is (= nil message))))

  (testing "enum-validator custom message"
    (let [[is_valid message] (enum-validator nil ["male", "female"] :error-message "Select a correct option")]
      (is (= false is_valid))
      (is (= "Select a correct option" message)))))

(deftest digits-only-validator-test
  (testing "digits-only-validator invalid"
    (let [[is_valid message] (digits-only-validator "1amdj441")]
      (is (= false is_valid))
      (is (= "Only digits are allowed" message))))

  (testing "digits-only-validator invalid nil"
    (let [[is_valid message] (digits-only-validator nil)]
      (is (= false is_valid))
      (is (= "Only digits are allowed" message))))

  (testing "digits-only-validator valid"
    (let [[is_valid message] (digits-only-validator "291888")]
      (is (= true is_valid))
      (is (= nil message))))

  (testing "digits-only-validator custom message"
    (let [[is_valid message] (digits-only-validator "291888f" :error-message "Use numbers")]
      (is (= false is_valid))
      (is (= "Use numbers" message)))))

(deftest compoose-validator-test
  (testing "valid"
    (let [[is_valid message]
          (compoose-validator
           "male"
           [str-not-blank-validator
            (fn [value] (enum-validator value ["male", "female"]))])]
      (is (= true is_valid))
      (is (= nil message))))

  (testing "invalid"
    (let [[is_valid message]
          (compoose-validator
           "hello"
           [str-not-blank-validator
            (fn [value] (enum-validator value ["male", "female"]))])]
      (is (= false is_valid))
      (is (= "Value should be one of: 'male', 'female'" message))))

  (testing "invalid 2"
    (let [[is_valid message]
          (compoose-validator
           ""
           [str-not-blank-validator
            (fn [value] (enum-validator value ["male", "female"]))])]
      (is (= false is_valid))
      (is (= "Can't be blank" message))))

  (testing "invalid nil"
    (let [[is_valid message]
          (compoose-validator
           nil
           [str-not-blank-validator
            (fn [value] (enum-validator value ["male", "female"]))])]
      (is (= false is_valid))
      (is (= "Can't be blank" message)))))

(def test-spec
  {:first_name [str-not-blank-validator (fn [value] (min-length-validator value 3))]
   :sex [str-not-blank-validator (fn [value] (enum-validator value ["MALE", "FEMALE"]))]})

(deftest spec-validator-test
  (testing "invalid"
    (let [[is_valid messages] (spec-validator {:sex "xxx"} test-spec)]
      (is (= false is_valid))
      (is (= {:first_name "Can't be blank" :sex "Value should be one of: 'MALE', 'FEMALE'"} messages))))

  (testing "invalid 2"
    (let [[is_valid messages] (spec-validator {:sex "MALE"} test-spec)]
      (is (= false is_valid))
      (is (= {:first_name "Can't be blank"} messages))))

  (testing "invalid 3"
    (let [[is_valid messages] (spec-validator {} test-spec)]
      (is (= false is_valid))
      (is (= {:first_name "Can't be blank" :sex "Can't be blank"} messages))))

  (testing "invalid 4"
    (let [[is_valid messages] (spec-validator {:first_name "Andrey"} test-spec)]
      (is (= false is_valid))
      (is (= {:sex "Can't be blank"} messages))))

  (testing "valid"
    (let [[is_valid messages] (spec-validator {:first_name "Andrey" :sex "MALE"} test-spec)]
      (is (= true is_valid))
      (is (= {} messages)))))

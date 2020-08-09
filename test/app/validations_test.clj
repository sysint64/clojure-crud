(ns app.validations-test
  (:require [clojure.test :refer :all]
            [app.validations :refer :all]))

(deftest str-not-blank-validation-test-invalid
  (testing "blank str should be invalid"
    (let [[is_valid message] (str-not-blank-validation "")]
      (is (= false is_valid))
      (is (= "Can't be blank" message)))))

(deftest str-not-blank-validation-test-invalid-nil
  (testing "blank str should be invalid nil"
    (let [[is_valid message] (str-not-blank-validation nil)]
      (is (= false is_valid))
      (is (= "Can't be blank" message)))))

(deftest str-not-blank-validation-test-valid
  (testing "blank str should be valid"
    (let [[is_valid message] (str-not-blank-validation "Test")]
      (is (= true is_valid))
      (is (= nil message)))))

(deftest str-not-blank-validation-test-custom-message
  (testing "blank str should be custom message"
    (let [[is_valid message] (str-not-blank-validation "" :error-message "Field is require")]
      (is (= false is_valid))
      (is (= "Field is require" message)))))

(deftest min-length-validation-test-invalid
  (testing "min-length-validation invalid"
    (let [[is_valid message] (min-length-validation "Hello World!" 20)]
      (is (= false is_valid))
      (is (= "Min length is 20" message)))))

(deftest min-length-validation-test-invalid-nil
  (testing "min-length-validation invalid nil"
    (let [[is_valid message] (min-length-validation nil 20)]
      (is (= false is_valid))
      (is (= "Min length is 20" message)))))

(deftest min-length-validation-test-valid
  (testing "min-length-validation valid"
    (let [[is_valid message] (min-length-validation "Hello World!" 10)]
      (is (= true is_valid))
      (is (= nil message)))))

(deftest min-length-validation-test-cusom-message
  (testing "min-length-validation custom message"
    (let [[is_valid message] (min-length-validation "Hello World!" 20 :error-message "Too short message")]
      (is (= false is_valid))
      (is (= "Too short message" message)))))

(deftest enum-validation-test-invalid
  (testing "enum-validation invalid"
    (let [[is_valid message] (enum-validation "hello" ["male", "female"])]
      (is (= false is_valid))
      (is (= "Value should be one of: 'male', 'female'" message)))))

(deftest enum-validation-test-invalid-nil
  (testing "enum-validation invalid nil"
    (let [[is_valid message] (enum-validation nil ["male", "female"])]
      (is (= false is_valid))
      (is (= "Value should be one of: 'male', 'female'" message)))))

(deftest enum-validation-test-valid
  (testing "enum-validation valid"
    (let [[is_valid message] (enum-validation "male" ["male", "female"])]
      (is (= true is_valid))
      (is (= nil message)))))

(deftest enum-validation-test-custom-message
  (testing "enum-validation custom message"
    (let [[is_valid message] (enum-validation nil ["male", "female"] :error-message "Select a correct option")]
      (is (= false is_valid))
      (is (= "Select a correct option" message)))))

(deftest digits-only-validation-test-invalid
  (testing "digits-only-validation invalid"
    (let [[is_valid message] (digits-only-validation "1amdj441")]
      (is (= false is_valid))
      (is (= "Only digits are allowed" message)))))

(deftest digits-only-validation-test-invalid-nil
  (testing "digits-only-validation invalid nil"
    (let [[is_valid message] (digits-only-validation nil)]
      (is (= false is_valid))
      (is (= "Only digits are allowed" message)))))

(deftest digits-only-validation-test-valid
  (testing "digits-only-validation valid"
    (let [[is_valid message] (digits-only-validation "291888")]
      (is (= true is_valid))
      (is (= nil message)))))

(deftest digits-only-validation-test-custom-message
  (testing "digits-only-validation custom message"
    (let [[is_valid message] (digits-only-validation "291888f" :error-message "Use numbers")]
      (is (= false is_valid))
      (is (= "Use numbers" message)))))

(deftest compoose-validation-test
  (testing "valid"
    (let [[is_valid message]
          (compoose-validation
           "male"
           [str-not-blank-validation
            (fn [value] (enum-validation value ["male", "female"]))])]
      (is (= true is_valid))
      (is (= nil message))))

  (testing "invalid"
    (let [[is_valid message]
          (compoose-validation
           "hello"
           [str-not-blank-validation
            (fn [value] (enum-validation value ["male", "female"]))])]
      (is (= false is_valid))
      (is (= "Value should be one of: 'male', 'female'" message))))

  (testing "invalid 2"
    (let [[is_valid message]
          (compoose-validation
           ""
           [str-not-blank-validation
            (fn [value] (enum-validation value ["male", "female"]))])]
      (is (= false is_valid))
      (is (= "Can't be blank" message))))

  (testing "invalid nil"
    (let [[is_valid message]
          (compoose-validation
           nil
           [str-not-blank-validation
            (fn [value] (enum-validation value ["male", "female"]))])]
      (is (= false is_valid))
      (is (= "Can't be blank" message)))))

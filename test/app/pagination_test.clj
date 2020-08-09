(ns app.pagination-test
  (:require [app.pagination :refer :all]
            [clojure.test :refer :all]))

(deftest parse-page-test
  (testing "nil"
    (is (= 1 (parse-page nil))))

  (testing "negative"
    (is (= 1 (parse-page "-1"))))

  (testing "zero"
    (is (= 1 (parse-page "0"))))

  (testing "letters"
    (is (= 1 (parse-page "abc"))))

  (testing "valid"
    (is (= 12 (parse-page "12")))))

(deftest next-page-test
  (testing "next page"
    (is (= 2 (next-page [1 2 3 4 5 6] 1 5))))

  (testing "last page"
    (is (= nil (next-page [1 2 3 4 5] 1 5))))

  (testing "last page 2"
    (is (= nil (next-page [1 2] 1 5)))))

(deftest get-result-test
  (testing "next page"
    (is (= [1 2 3 4 5] (get-result [1 2 3 4 5 6] 5))))

  (testing "last page"
    (is (= [1 2 3 4 5] (get-result [1 2 3 4 5] 5))))

  (testing "last page 2"
    (is (= [1 2] (get-result [1 2] 5)))))

(deftest offset-test
  (testing "first page"
    (is (= 0 (offset 1 5))))

  (testing "second page"
    (is (= 5 (offset 2 5))))

  (testing "third page"
    (is (= 10 (offset 3 5)))))

(deftest limit-test
  (testing "limit"
    (is (= 6 (limit 5)))
    (is (= 11 (limit 10)))
    (is (= 88 (limit 87)))))

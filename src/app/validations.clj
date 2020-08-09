(ns app.validations
  (:require [clojure.string :as str]))

;; https://stackoverflow.com/questions/3249334/test-whether-a-list-contains-a-specific-value-in-clojure
(defn- in?
  "true if coll contains elm"
  [coll elm]
  (some #(= elm %) coll))

;; https://stackoverflow.com/questions/4086889/clojure-coalesce-function#:~:text=SQL%20offers%20a%20function%20called,the%20first%20non%2Dnull%20argument.
(defmacro coalesce
  ([] nil)
  ([x] x)
  ([x & next]
   `(let [v# ~x]
      (if (not (nil? v#)) v# (coalesce ~@next)))))

(defn str-not-blank-validation [value & {:keys [error-message]
                                         :or {error-message "Can't be blank"}}]
  (if (str/blank? value)
    [false error-message]
    [true nil]))

(defn min-length-validation [value length & {:keys [error-message]
                                             :or {error-message (str "Min length is " length)}}]
  (if (< (count value) length)
    [false error-message]
    [true nil]))

(defn- enum-validation-error-message [enum]
  (str "Value should be one of: "
       (str/join ", " (map (fn [it] (str "'" it "'")) enum))))

(defn enum-validation [value enum & {:keys [error-message]
                                     :or {error-message (enum-validation-error-message enum)}}]
  (if-not (in? enum value)
    [false error-message]
    [true nil]))

(defn digits-only-validation [value & {:keys [error-message]
                                       :or {error-message "Only digits are allowed"}}]
  (if (or (nil? value) (not (every? #(Character/isDigit %) value)))
    [false error-message]
    [true nil]))

(defn compoose-validation [value validators]
  (reduce
   (fn [a, b] (let [[is_valid message] (b value)
                    [previous_valid, previous_message] a]
                [(and is_valid previous_valid)
                 (coalesce message previous_message)]))
   [true nil]
   (reverse validators)))

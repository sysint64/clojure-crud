(ns app.frontend.form.states
  (:require-macros [hiccups.core :as hiccups :refer [html]])
  (:require [hiccups.runtime :as hiccupsrt]
            [clojure.string :as str]))

(defn full-name [patient]
  (str/join " " [(get patient "last_name")
                 (get patient "first_name")
                 (get patient "middle_name")]))

(hiccups/defhtml update-title []
  [:h1 "Update patient"]
  [:a {:href "" :id "go-back-link"} "< All patients"])

(hiccups/defhtml insert-title []
  [:h1 "Insert patient"]
  [:a {:href "" :id "go-back-link"} "< All patients"])

(hiccups/defhtml loading-title []
  [:h1 "Update patient"]
  [:a {:href "" :id "go-back-link"} "< All patients"])

(hiccups/defhtml loading []
  (loading-title)
  [:div {:class "loading"} "Loading..."])

(hiccups/defhtml submitting []
  [:div {:class "submitting"} "Submitting..."])

(hiccups/defhtml line [label error-text child]
  [:div {:class "form-line"}
   [:div [:label label]]
   [:div child]
   error-text])

(defn error-class [errors key]
  (when (not (nil? (get errors key)))
    "error"))

(hiccups/defhtml error-message []
  [:div {:class "error-message"} "Something went wrong"])

(hiccups/defhtml error-text [errors key]
  [:div (when (get errors key)
          [:div {:class "error-text"} (get errors key)])])

(hiccups/defhtml text-input [patient errors label key]
  (line label (error-text errors key)
        [:input {:name key :class (error-class errors key)
                 :type "text" :value (get patient key)}]))

(hiccups/defhtml date-input [patient errors label key]
  (line label (error-text errors key)
        [:input {:name key :class (error-class errors key)
                 :type "date" :value (get patient key)}]))

(hiccups/defhtml chooser-input [patient errors label key default options]
  (line label (error-text errors key)
        [:select {:name key :class (error-class errors key)}
         (map (fn [option]
                [:option {:value (nth option 0)
                          :selected (if (= (nth option 0) (get patient key)) true nil)}
                 (nth option 1)])
              options)]))

(hiccups/defhtml submit-button [title]
  [:input {:id "submit" :type "submit" :value title}])

(hiccups/defhtml form [patient errors]
  [:input {:name "id" :type "hidden" :value (get patient "id")}]
  (text-input patient errors "First name" "first_name")
  (text-input patient errors "Last name" "last_name")
  (text-input patient errors "Middle name" "middle_name")
  (chooser-input patient errors "Sex" "sex" "PREFER_NOT_SAY"
                 [["PREFER_NOT_SAY" "Prefer not say"]
                  ["MALE" "Male"]
                  ["FEMALE" "Female"]])
  (date-input patient errors "Birthday" "date_of_birth")
  (text-input patient errors "Address" "address")
  (text-input patient errors "OMS policy number" "oms_policy_number"))

(hiccups/defhtml insert-loaded [input errors]
  (insert-title)
  [:form {:id "form"}
   (form input errors)
   (submit-button "Insert")])

(hiccups/defhtml update-loaded [input errors]
  (update-title)
  [:form {:id "form"}
   (form input errors)
   (submit-button "Update")])

(hiccups/defhtml insert-submitting [input]
  (insert-title)
  [:form {:id "form"}
   (form input nil)
   (submitting)])

(hiccups/defhtml update-submitting [input]
  (update-title)
  [:form {:id "form"}
   (form input nil)
   (submitting)])

(hiccups/defhtml insert-error [input]
  (insert-title)
  [:form {:id "form"}
   (form input nil)
   (error-message)
   (submit-button "Insert")])

(hiccups/defhtml update-error [input]
  (update-title)
  [:form {:id "form"}
   (form input nil)
   (error-message)
   (submit-button "Update")])

(hiccups/defhtml error []
  (loading-title)
  [:div {:class "error"} "Loading error, try again later"]
  [:div [:a {:href "" :id "try-again"} "Try again"]])

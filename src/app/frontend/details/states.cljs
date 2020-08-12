(ns app.frontend.details.states
  (:require-macros [hiccups.core :as hiccups :refer [html]])
  (:require [hiccups.runtime :as hiccupsrt]
            [clojure.string :as str]))

(defn full-name [patient]
  (str/join " " [(get patient "last_name")
                 (get patient "first_name")
                 (get patient "middle_name")]))

(hiccups/defhtml title [patient]
  [:h1 (full-name patient)]
  [:a {:href "" :id "go-back-link"} "< All patients"])

(hiccups/defhtml loading-title []
  [:h1 "Patient details"]
  [:a {:href "" :id "go-back-link"} "< All patients"])

(hiccups/defhtml loading []
  (loading-title)
  [:div {:class "loading"} "Loading..."])

(hiccups/defhtml deleting []
  (loading-title)
  [:div {:class "deleting"} "Please wait..."])

(hiccups/defhtml menu [patient]
  (let [id (get patient "id")]
    [:div {:class "menu"}
     "["
     [:a {:href "" :id "edit" :data-id id} "Edit"]
     "] ["
     [:a {:href "" :id "delete" :data-id id} "Delete"]
     "]"]))

(hiccups/defhtml line [patient title key]
  [:div {:class "line"}
   [:div {:class "title"} (str title ":")]
   [:div {:class "data"} (get patient key)]])

(hiccups/defhtml loaded [patient]
  (title patient)
  [:div {:class "patient"}
   (menu patient)
   (line patient "First name" "first_name")
   (line patient "Last name" "last_name")
   (line patient "Middle name" "middle_name")
   (line patient "Sex" "sex")
   (line patient "Date of birth" "date_of_birth")
   (line patient "Address" "address")
   (line patient "OMS policy number" "oms_policy_number")])

(hiccups/defhtml error []
  (loading-title)
  [:div {:class "error"} "Loading error, try again later"]
  [:div [:a {:href "" :id "try-again"} "Try again"]])

(hiccups/defhtml deleting-error []
  (loading-title)
  [:div {:class "error"} "Something went wrong, try again later"]
  [:div [:a {:href "" :id "try-delete-again"} "Try again"]])

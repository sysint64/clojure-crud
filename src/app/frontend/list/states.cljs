(ns app.frontend.list.states
  (:require-macros [hiccups.core :as hiccups :refer [html]])
  (:require [hiccups.runtime :as hiccupsrt]
            [clojure.string :as str]))

(defrecord State [page])

(hiccups/defhtml title []
  [:h1 "Patients "
   [:span {:class "menu"}
    "[" [:a {:href "" :id "insert"} "+"] "]"]])

(hiccups/defhtml loading []
  (title)
  [:div {:class "loading"} "Loading..."])

(defn full-name [patient]
  (println patient)
  (str/join " " [(get patient "last_name")
                 (get patient "first_name")
                 (get patient "middle_name")]))

(hiccups/defhtml patient [patient]
  [:div {:class "patient-list-item"}
   [:a {:href "details.html"} (full-name patient)]])

(hiccups/defhtml search []
  [:div {:class "search"}
   [:input {:type "text" :id "search-input" :placeholder "Search..."}]
   [:input {:type "submit" :id "search-button" :value "Search"}]])

(hiccups/defhtml patients-list [data]
  [:div {:class "patients"}
   (map patient (get data "result"))])

(hiccups/defhtml pagination [query data]
  [:div {:class "pagination"}
   (when (not (nil? (get data "next-page")))
     [:div {:class "next-page-link"}
      [:a {:href "" :id "next-page"
           :data-query (str query "?page=" (get data "next-page"))} "Next page >"]])
   (when (not (nil? (get data "prev-page")))
     [:div {:class "prev-page-link"}
      [:a {:href "" :id "prev-page"
           :data-query (str query "?page=" (get data "prev-page"))} "< Prev page"]])])

(hiccups/defhtml loaded [query data]
  (title)
  (search)
  (patients-list data)
  (pagination query data))

(hiccups/defhtml error []
  (title)
  [:div {:class "error"} "Loading error, try again later"]
  [:div [:a {:href "" :id "try-again"} "Try again"]])

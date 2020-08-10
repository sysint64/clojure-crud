(ns app.frontend.list.states
  (:require-macros [hiccups.core :as hiccups :refer [html]])
  (:require [hiccups.runtime :as hiccupsrt]
            [clojure.string :as str]))

(hiccups/defhtml title []
  [:h1 "Patients "
   [:span {:class "menu"}
    "[" [:a {:href "" :id "insert"} "+"] "]"]])

(hiccups/defhtml loading []
  (title)
  [:div {:class "loading"} "Loading..."])

(hiccups/defhtml patient [patient]
  (title)
  [:div {:class "loading"} "Loading..."])

(hiccups/defhtml loaded [data]
  (title)
  [:div {:class "patients"} (str data)])

(hiccups/defhtml error []
  (title)
  [:div {:class "error"} "Loading error, try again later"]
  [:div [:a {:href "" :id "try-again"} "Try again"]])

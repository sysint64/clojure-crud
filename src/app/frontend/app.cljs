(ns app.frontend.app
  (:require-macros [hiccups.core :as hiccups :refer [html]])
  (:require [hiccups.runtime :as hiccupsrt]
            [clojure.string :as str]
            [app.frontend.list.events :as list]))

(enable-console-print!)

(list/on-load)

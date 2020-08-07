(ns user
  (:require [reloaded.repl :refer [system reset stop]]
            [app.core]))

(reloaded.repl/set-init! #'app.core/create-system)

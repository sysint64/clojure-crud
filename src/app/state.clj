(ns app.state)

(defrecord AppState [db-connection])

(def app-state (atom (AppState. nil)))

(defn db-connection []
  (:db-connection @app-state))

(defn set-db-connection [connection]
  (swap! app-state (fn [it] (assoc it :db-connection connection))))

(defn remove-db-connection []
  (swap! app-state (fn [it] (dissoc it :db-connection))))

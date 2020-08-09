(ns app.db)

(defn connect-to-database [uri]
  {:connection-uri uri})

(defn close-db-connection [connection]
  ;; nothing to do
  )

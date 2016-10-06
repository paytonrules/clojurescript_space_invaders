(ns util.time)

(defn epoch []
  (.now js/Date))

(ns space-invaders.bullet
  (:refer-clojure :exclude [update]))

(def velocity 0.200)
(def character {:character :bullet :state :default})

(defn create [position]
  {:position position})

(defn update [bullet delta]
  (->> (get-in bullet [:position :y])
       (+ (* delta velocity))
       (assoc-in bullet [:position :y])))

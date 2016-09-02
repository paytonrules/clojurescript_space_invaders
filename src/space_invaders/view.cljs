(ns space-invaders.view)

(def padding 20)
(def top 30)
(def invader-width 20)
(def invader-height 30)

(defn- row-to-position [row-num row]
  (map-indexed
    (fn [idx invader]
      {:x (+ (* invader-width idx) padding)
       :y (+ top (* invader-height row-num))})
    row))

(defn invaders-to-position [invaders ticks]
  (flatten (map-indexed row-to-position invaders)))

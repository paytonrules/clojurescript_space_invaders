(ns space-invaders.invaders)

(def start-position {:x 25 :y 40})
(def velocity 5)
(def column-width 16)
(def row-height 16)

(defn pose [ticks]
  (if (even? ticks)
    :open
    :closed))

(defn position [ticks]
  {:x (+ (:x start-position) (* ticks velocity))
   :y (:y start-position)})

(defn x-position [{:keys [column ticks]}]
  (let [position (position ticks)]
    (+ (:x position) (* column-width column))))

(defn y-position [row]
  (+ (:y start-position) (* row-height row)))

(defn right-edge [{:keys [invaders ticks]}]
  (let [longest-row-length (apply max (map count invaders))]
    (+ (:x (position ticks))
       (* longest-row-length column-width))))

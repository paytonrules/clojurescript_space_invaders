(ns space-invaders.invasion)

(def start-position {:x 15 :y 40})
(def velocity 4)
(def column-width 16)
(def row-height 16)

(defn pose [ticks]
  (if (even? ticks)
    :open
    :closed))

(def direction-multiplier {:left -1 :right 1})

(defn position [{:keys [ticks direction]}]
  (let [dm (get direction-multiplier direction)]
    {:x (+ (:x start-position) (* ticks velocity dm))
     :y (:y start-position)}))

(defn x-position [{:keys [column ticks] :as state}]
  (let [position (position state)]
    (+ (:x position) (* column-width column))))

(defn y-position [row]
  (+ (:y start-position) (* row-height row)))

(defn right-edge [{:keys [invaders ticks] :as state}]
  (let [longest-row-length (apply max (map count invaders))]
    (+ (:x (position state))
       (* longest-row-length column-width))))

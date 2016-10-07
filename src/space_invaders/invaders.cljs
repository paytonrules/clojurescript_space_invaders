(ns space-invaders.invaders)

(def invader-width 8)
(def top 20)

(def start-position {:x 0 :y 0})
(def velocity 5)
(def padding 16)
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
    (+ (:x position) (* padding column))))

(defn y-position [row]
  (+ (:y start-position) (* row-height row)))


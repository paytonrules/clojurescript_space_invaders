(ns space-invaders.invasion
  (:refer-clojure :exclude [update]))

(def start-position {:x 1 :y 20})
(def velocity 4)
(def column-width 16)
(def row-height 16)
(def row-length 11)
(def invader-states [:open :closed])
(def invader-types [:small :medium :large])
(def direction-multiplier {:left -1 :right 1})

(defonce initial
  {:pose :open
   :since-last-move 0
   :time-to-move 1000
   :position start-position
   :invaders [(take row-length (repeat :small))
              (take row-length (repeat :medium))
              (take row-length (repeat :medium))
              (take row-length (repeat :large))
              (take row-length (repeat :large))]})

(defn pose [{:keys [pose]}] pose)

(defn invader-position [{:keys [position]} {:keys [row col]}]
  (let [{:keys [x y]} position]
    {:x (+ x (* col column-width))
     :y (+ y (* row row-height))}))

(defn right-edge [{:keys [invaders position]}]
  (let [longest-row-length (apply max (map count invaders))]
    (+ (:x position) (* longest-row-length column-width))))

(defn- toggle-pose [{:keys [pose] :as invasion}]
  (if (= :closed pose)
    (assoc invasion :pose :open)
    (assoc invasion :pose :closed)))

(defn- next-position [{:keys [position direction]}]
  {:x (* (direction-multiplier direction)  (+ (:x position) velocity))
   :y (:y position)})

(defn- move [invasion]
  (-> (toggle-pose invasion)
      (assoc :position (next-position invasion))
      (assoc :since-last-move 0)))

(defn update [invasion delta]
  (let [since-last-move (-> (get invasion :since-last-move 0)
                            (+ delta))]
    (if (>= since-last-move (:time-to-move invasion))
      (move invasion)
      (assoc invasion :since-last-move since-last-move))))

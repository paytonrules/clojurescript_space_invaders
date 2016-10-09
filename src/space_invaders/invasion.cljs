(ns space-invaders.invasion
  (:refer-clojure :exclude [update]))

(def start-position {:x 15 :y 40})
(def velocity 4)
(def column-width 16)
(def row-height 16)
(def row-length 11)
(def invader-states [:open :closed])
(def invader-types [:small :medium :large])
(def direction-multiplier {:left -1 :right 1})

(defonce initial
  {:ticks 0 ; delete me
   :pose :open
   :since-last-move 0
   :time-to-move 1000
   :invaders [(take row-length (repeat :small))
              (take row-length (repeat :medium))
              (take row-length (repeat :medium))
              (take row-length (repeat :large))
              (take row-length (repeat :large))]})

(defn pose [{:keys [pose]}] pose)

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

(defn- toggle-pose [{:keys [pose] :as invasion}]
  (if (= :closed pose)
    (assoc invasion :pose :open)
    (assoc invasion :pose :closed)))

(defn- move [invasion]
  (-> (toggle-pose invasion)
      (assoc :since-last-move 0)))

(defn update [invasion delta]
  (let [since-last-move (-> (get invasion :since-last-move 0)
                            (+ delta))]
    (if (>= since-last-move (:time-to-move invasion))
      (move invasion)
      (assoc invasion :since-last-move since-last-move))))

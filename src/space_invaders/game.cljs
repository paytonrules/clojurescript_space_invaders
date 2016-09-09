(ns space-invaders.game)

(def row-length 11)
(def velocity 15)

(def initial-app-state
  {:ticks 0
   :invaders [(take row-length (repeat :small))
              (take row-length (repeat :medium))
              (take row-length (repeat :medium))
              (take row-length (repeat :large))
              (take row-length (repeat :large))]})

(defn update-game [state]
  {:state (assoc state :ticks (inc (:ticks state)))})

(defn invader-position [{:keys [ticks]}]
  (if (even? (quot ticks velocity))
    :open
    :closed))

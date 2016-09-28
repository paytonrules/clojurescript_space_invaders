(ns space-invaders.game
  (:require [space-invaders.view :as view]
            [util.image-loader :as image-loader]))

(def row-length 11)
(def velocity 15)

(def invader-types [:small :medium :large])
(def invader-states [:open :closed])

(def initial-app-state
  {:state :starting
   :ticks 0
   :invaders [(take row-length (repeat :small))
              (take row-length (repeat :medium))
              (take row-length (repeat :medium))
              (take row-length (repeat :large))
              (take row-length (repeat :large))]})

(defn enemy-images []
  (for [invader invader-types
        state invader-states]
    (view/invader->image-path invader state)))

(defmulti update-game :state)

(defmethod update-game :starting [state]
  (image-loader/load-images (enemy-images))
  (assoc state :state :loading-images))

(defmethod update-game :playing [state]
  (assoc state :ticks (inc (:ticks state))))

(defn invader-position [{:keys [ticks]}]
  (if (even? (quot ticks velocity))
    :open
    :closed))

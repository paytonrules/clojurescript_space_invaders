(ns space-invaders.presentation
  (:require [space-invaders.invaders :as invaders]
            [space-invaders.game :as game]))

(defn- image-lookup [{:keys [state] :as game-state} invader]
  (game/image-lookup game-state invader (invaders/pose (:ticks state))))

(defn images-with-position [{:keys [state] :as game-state}]
  (-> (map-indexed
        (fn [row-idx row]
          (map-indexed
            (fn [idx invader]
              {:image (image-lookup game-state invader)
               :x (game/invader-x-position idx)
               :y (game/invader-y-position row-idx)})
            row))
        (:invaders state))
      (flatten)))

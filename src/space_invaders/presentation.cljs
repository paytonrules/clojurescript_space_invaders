(ns space-invaders.presentation
  (:require [space-invaders.game :as game]))

(defn- image-lookup [game-state invader]
  (game/image-lookup game-state invader (game/invader-position game-state)))

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

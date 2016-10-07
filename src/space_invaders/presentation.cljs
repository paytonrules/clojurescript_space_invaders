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
               :x (invaders/x-position {:column idx
                                        :ticks (:ticks state)})
               :y (invaders/y-position row-idx)})
            row))
        (:invaders state))
      (flatten)))

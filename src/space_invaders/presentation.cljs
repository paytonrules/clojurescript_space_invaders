(ns space-invaders.presentation
  (:require [space-invaders.invasion :as invasion]
            [space-invaders.game :as game]))

(defn- image-lookup [{:keys [state] :as game-state} invader]
  (game/image-lookup
    game-state
    invader
    (invasion/pose (:invasion state))))

(defn images-with-position [{:keys [state] :as game-state}]
  (-> (map-indexed
        (fn [row-idx row]
          (map-indexed
            (fn [idx invader]
              {:image (image-lookup game-state invader)
               :x (invasion/x-position {:column idx
                                        :ticks (:ticks (:invasion state))})
               :y (invasion/y-position row-idx)})
            row))
        (:invaders (:invasion state)))
      (flatten)))

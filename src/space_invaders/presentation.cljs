(ns space-invaders.presentation
  (:require [space-invaders.invasion :as invasion]
            [space-invaders.image-lookup :as image-lookup]
            [space-invaders.game :as game]))

(defn- image-lookup [{:keys [state]} invader]
  (image-lookup/->image
    state
    invader
    (invasion/pose (:invasion state))))

(defn- image-for-row-col [invasion game-state row col invader]
  (-> (invasion/invader-position invasion {:row row :col col})
      (assoc :image (image-lookup game-state invader))))

(defn- images-for-row [invasion game-state row invaders]
  (map-indexed (partial image-for-row-col invasion game-state row) invaders))

(defn images-with-position [{:keys [state] :as game-state}]
  (let [invasion (:invasion state)]
    (->> (:invaders invasion)
         (map-indexed (partial images-for-row invasion game-state))
         (flatten))))

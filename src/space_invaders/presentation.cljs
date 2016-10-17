(ns space-invaders.presentation
  (:require [space-invaders.invasion :as invasion]
            [space-invaders.image-lookup :as image-lookup]
            [space-invaders.game :as game]))

(defn- invader-lookup [state invader]
  (image-lookup/->image
    state
    (:character invader)
    (invasion/pose (:invasion state))))

(defn- invaders->images [state invaders-with-positions]
  (map
    (fn [invader]
      (assoc invader :image (invader-lookup state invader)))
  invaders-with-positions))

(defn- invasion->images [{:keys [invasion] :as state}]
  (->> (invasion/invader-positions invasion)
       (invaders->images state)))

(defn laser->images [{:keys [laser] :as state}]
  (assoc laser :image (image-lookup/->image state :laser :default)))

(defn images-with-position [{:keys [state] :as game-state}]
  (concat (invasion->images state)
          [(laser->images state)]))


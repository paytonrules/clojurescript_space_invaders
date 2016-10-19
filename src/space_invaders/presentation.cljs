(ns space-invaders.presentation
  (:require [space-invaders.invasion :as invasion]
            [space-invaders.image-lookup :as image-lookup]
            [space-invaders.laser :as laser]))

(defn- invader-lookup [game invader]
  (image-lookup/->image
    game
    (:character invader)
    (invasion/pose (:invasion game))))

(defn- invaders->images [game invaders-with-positions]
  (map
    (fn [invader]
      (assoc invader :image (invader-lookup game invader)))
  invaders-with-positions))

(defn- invasion->images [{:keys [invasion] :as game}]
  (->> (invasion/invader-positions invasion)
       (invaders->images game)))

(defn laser->images [{:keys [laser] :as game}]
  (assoc laser :image (image-lookup/->image
                        game
                        (:character laser/character)
                        (:state laser/character))))

(defn images-with-position [{:keys [game]}]
  (concat (invasion->images game)
          [(laser->images game)]))


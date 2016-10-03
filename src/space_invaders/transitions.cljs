(ns space-invaders.transitions
  (:require [cljs.core.async :as ca]
            [util.game-loop :as gl]
            [util.image-loader :as image-loader]))

(defn load-images!
  ([images] (load-images! images image-loader/load-images))
  ([images image-loader]
   (ca/take!
     (image-loader images)
     #(gl/fire-event! {:name :images-loaded
                       :images %}))))

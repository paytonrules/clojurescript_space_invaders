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

(defn key-down! [evt]
  (case (.-key evt)
    "ArrowRight" (gl/fire-event! {:name :move-right})
    "ArrowLeft"  (gl/fire-event! {:name :move-left})
    " " (gl/fire-event! {:name :fire})
    nil))

(defn key-up! [evt]
  (case (.-key evt)
    "ArrowRight" (gl/fire-event! {:name :move-left})
    "ArrowLeft"  (gl/fire-event! {:name :move-right})
    nil))



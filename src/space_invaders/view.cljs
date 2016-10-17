(ns space-invaders.view
  (:require [space-invaders.presentation :as presentation]))

(defonce canvas (.getElementById js/document "game"))
(defonce ctx (.getContext canvas "2d"))
(defonce w 648)
(defonce h 672)
(set! (.-width canvas) w)
(set! (.-height canvas) h)
(set! (.-imageSmoothingEnabled ctx) false)
(.scale ctx 3 3)

(defn clear-screen []
  (.clearRect ctx 0 0 w h)
  (set! (.-fillStyle ctx) "#FFFFFF"))

(defmulti draw-canvas-contents (fn [{:keys [state]}] (:name state)))

(defmethod draw-canvas-contents nil [state]
  (clear-screen))

(defmethod draw-canvas-contents :loading-images [state]
  (clear-screen))

(defmethod draw-canvas-contents :playing [state]
  (clear-screen)
  (doseq [{:keys [image position]} (presentation/images-with-position state)]
    (.drawImage ctx image (:x position) (:y position))))


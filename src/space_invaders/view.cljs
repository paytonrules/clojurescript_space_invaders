(ns space-invaders.view
  (:require [space-invaders.game :as game]
            [space-invaders.presentation :as presentation]
            [space-invaders.transitions :as transitions]))


(defonce canvas (.getElementById js/document "game"))
(defonce ctx (.getContext canvas "2d"))
(defonce scale 2)
(defonce w (* scale (:w game/resolution)))
(defonce h (* scale (:h game/resolution)))
(set! (.-width canvas) w)
(set! (.-height canvas) h)
(set! (.-imageSmoothingEnabled ctx) false)
(.scale ctx scale scale)

(.addEventListener canvas "keydown" #(transitions/key-down! %1) false)
(.addEventListener canvas "keyup" #(transitions/key-up! %1) false)

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


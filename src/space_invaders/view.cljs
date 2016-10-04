(ns space-invaders.view
  (:require [space-invaders.presentation :as presentation]))

(defonce canvas (.getElementById js/document "game"))
(defonce ctx (.getContext canvas "2d"))
(defonce w (.-clientWidth canvas))
(defonce h (.-clientHeight canvas))

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
  (doseq [{:keys [image x y]} (presentation/images-with-position state)]
    (.drawImage ctx image x y)))


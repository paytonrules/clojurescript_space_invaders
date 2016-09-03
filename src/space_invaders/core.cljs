(ns space-invaders.core
  (:require [space-invaders.view :as view]))

(enable-console-print!)

(defonce initial-app-state
  {
   :ticks 0
   :invaders [
              [:triangle :triangle :triangle :triangle :triangle :triangle :triangle :triangle :triangle :triangle]
              [:waver :waver]
              ]
   })

(defonce canvas (.getElementById js/document "game"))

(defonce image (js/Image.))
(set! (.-src image) "images/small-invader.png")

(defn draw-canvas-contents [state ticks]
  (let [ ctx (.getContext canvas "2d")
        w (.-clientWidth canvas)
        h (.-clientHeight canvas)]
    (.clearRect ctx 0 0 w h)
    (set! (.-fillStyle ctx) "#FFFFFF")
    (doseq [invader-position (view/invaders-to-position (:invaders state) (:ticks state))]
      (.drawImage ctx
                  image
                  (:x invader-position)
                  (:y invader-position)))))

(defn draw-game-state [app-state ticks]
  (draw-canvas-contents app-state ticks)
  (js/requestAnimationFrame (partial draw-game-state app-state (inc ticks))))

(set! (.-backgroundColor (.-style canvas)) "black")
(set! (.-width canvas) 224)
(set! (.-height canvas) 256)
(draw-game-state initial-app-state 0)

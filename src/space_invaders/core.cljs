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

(defn draw-canvas-contents [state ticks]
  (let [ ctx (.getContext canvas "2d")
        w (.-clientWidth canvas)
        h (.-clientHeight canvas)]
    (.clearRect ctx 0 0 w h)
    (doseq [invader-position (view/invaders-to-position (:invaders state) (:ticks state))]
      (.fillRect ctx
                 (:x invader-position)
                 (:y invader-position)
                 view/invader-width
                 view/invader-height))))

(defn draw-game-state [app-state ticks]
  (draw-canvas-contents app-state ticks)
  (js/requestAnimationFrame (partial draw-game-state app-state (inc ticks))))

(draw-game-state initial-app-state 0)

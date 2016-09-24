(ns space-invaders.core
  (:require [space-invaders.game :as game]
            [space-invaders.view :as view]
            [util.game-loop :as game-loop]))

(enable-console-print!)

(defonce canvas (.getElementById js/document "game"))

(defonce image (js/Image.))
(set! (.-src image) "images/small-invader.png")

(defn draw-canvas-contents [state ticks]
  (let [ ctx (.getContext canvas "2d")
        w (.-clientWidth canvas)
        h (.-clientHeight canvas)]
    (.clearRect ctx 0 0 w h)
    (set! (.-fillStyle ctx) "#FFFFFF")
    (view/draw-enemies (partial .drawImage ctx) state)
    (doseq [invader-position (view/invaders-to-position (:invaders state) (:ticks state))]
      (.drawImage ctx
                  image
                  (:x invader-position)
                  (:y invader-position)))))

(set! (.-backgroundColor (.-style canvas)) "black")
(set! (.-width canvas) 224)
(set! (.-height canvas) 256)

(game-loop/start {:draw draw-canvas-contents
                  :update game/update-game
                  :state game/initial-app-state})


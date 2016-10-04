(ns space-invaders.core
  (:require [space-invaders.game :as game]
            [space-invaders.view :as view]
            [util.game-loop :as game-loop]))

(enable-console-print!)

(game-loop/start! {:draw view/draw-canvas-contents
                   :update game/update-game})


(ns space-invaders.presentation
  (:require [space-invaders.game :as game]))

(def padding 3)
(def top 20)
(def invader-width 8)
(def invader-height 8)

(defn images-with-position [{:keys [state]}]
  (-> (map-indexed
        (fn [row-idx row]
          (map-indexed
            (fn [idx invader]
              {:image (get-in state [:images invader
                                     (game/invader-position state)])
               :x (+ (* invader-width idx) (* (inc idx) padding))
               :y  (+ (* (inc row-idx) top) (* invader-height row-idx))})
            row))
        (:invaders state))
      (flatten)))

(ns space-invaders.view
  (:require [clojure.string :as string]))

(def padding 3)
(def top 20)
(def invader-width 8)
(def invader-height 8)

(defn invaders-to-position [invaders ticks]
  (-> (map-indexed
        (fn [row-num row]
          (map-indexed
            (fn [idx invader]
              {:x (+ (* invader-width idx) (* (inc idx) padding))
               :y (+ (* (inc row-num) top) (* invader-height row-num))})
            row))
        invaders)
      (flatten)))

(defn image-path->invader-state [image-path]
  (map keyword (-> (string/split image-path "/")
                   (last)
                   (string/replace ".png" "")
                   (string/split "_"))))

(defn invader->image-path [invader state]
  (str "images/" (name invader) "_" (name state) ".png"))

(defn draw-enemies [draw-fn state]
  )



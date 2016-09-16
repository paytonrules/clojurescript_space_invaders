(ns space-invaders.view)

(def padding 3)
(def top 30)
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

(defn image-path [alien state]
  (str "images/" (name alien) "_" (name state) ".png"))

(ns space-invaders.laser
  (:refer-clojure :exclude [update]))

(def initial
  {:position {:x 98 :y 216}
   :velocity 0})

(def character {:character :laser :state :default})
(def width 13)
(def speed 0.200) ; Speed is in pixels per millisecond. This is 200 pixels per second.

(defn update [{:keys [position velocity] :as laser} {:keys [bounds delta]}]
  (let [new-x (-> (+ (:x position) (* velocity delta))
                  (max (:left bounds))
                  (min (- (:right bounds) width)))]
    (assoc-in laser [:position :x] new-x)))

(defn- move [{:keys [velocity] :as laser} update]
  (let [new-velocity (-> (+ velocity update)
                         (max (- speed))
                         (min speed))]
    (assoc laser :velocity new-velocity)))

(defn move-left [laser]
  (move laser (- speed)))

(defn move-right [{:keys [velocity] :as laser}]
  (move laser speed))

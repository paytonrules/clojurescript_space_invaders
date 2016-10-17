(ns space-invaders.invasion
  (:refer-clojure :exclude [update])
  (:require [clojure.spec :as spec]))

(def start-position {:x 3 :y 20})
(def velocity 4)
(def column-width 16)
(def row-height 16)
(def row-length 11)
(def invader-states [:open :closed])
(def invader-types [:small :medium :large])
(def left-right-multiplier {:left -1 :right 1 :down 0})
(def up-down-multiplier {:left 0 :right 0 :down 1})

(defn character-matrix->vector [matrix]
  (-> (map-indexed
        (fn [row-idx row]
          (map-indexed
            (fn [col-idx col]
              {:character col :row row-idx :col col-idx})
            row))
        matrix)
      (flatten)))

(defn row-cols->offsets [row-col-hash]
  (map
    (fn [h]
      {:character (:character h)
       :offset {:x (* column-width (:col h))
                :y (* row-height (:row h))}})
    row-col-hash))

(defonce invader-matrix
  [(take row-length (repeat :small))
   (take row-length (repeat :medium))
   (take row-length (repeat :medium))
   (take row-length (repeat :large))
   (take row-length (repeat :large))])

(defonce initial
  {:pose :open
   :since-last-move 0
   :time-to-move 1000
   :direction :right
   :position start-position
   :invaders (-> invader-matrix
                 (character-matrix->vector)
                 (row-cols->offsets))})

(defn pose [{:keys [pose]}] pose)

(defn invaders-and-states []
  (for [invader invader-types
        state invader-states]
    {:character invader :state state}))

(defn invader-positions [{:keys [invaders position]}]
  (map
    #(-> (assoc % :position {:x (+ (:x position) (get-in % [:offset :x]))
                             :y (+ (:y position) (get-in % [:offset :y]))})
         (dissoc :offset))
    invaders))

; DELETE ME
(defn invader-position [{:keys [position]} {:keys [row col]}]
  (let [{:keys [x y]} position]
    {:x (+ x (* col column-width))
     :y (+ y (* row row-height))}))

(defn right-edge [{:keys [invaders position]}]
  (let [right-positions (map #(get-in % [:offset :x]) invaders)
        farthest-right-position (apply max right-positions)]
    (+ (:x position) farthest-right-position column-width)))

(defn- toggle-pose [{:keys [pose] :as invasion}]
  (if (= :closed pose)
    (assoc invasion :pose :open)
    (assoc invasion :pose :closed)))

(defn- update-position [{:keys [position direction] :as invasion} bounds]
  (let [{:keys [x y]} position
        new-x (+ x (* (left-right-multiplier direction) velocity))
        new-y (+ y (* (up-down-multiplier direction) velocity))]
    (assoc invasion :position {:x new-x :y new-y})))

(defn- beyond-right-bounds [invasion bounds]
  (let [right-edge (right-edge invasion)]
    (and (>= right-edge (:right bounds)))))

(defn- update-direction [{:keys [position direction] :as invasion} bounds]
  (let [{:keys [x y]} position
        right-edge (right-edge invasion)
        new-direction (cond
                        (and (beyond-right-bounds invasion bounds)
                             (= :right direction)) :down
                        (and (beyond-right-bounds invasion bounds)
                             (= :down direction)) :left
                        (and (<= x (:left bounds)) (= :left direction)) :down
                        (and (<= x (:left bounds)) (= :down direction)) :right
                        :default direction)]
    (assoc invasion :direction new-direction)))

(defn- move [invasion bounds]
  (-> (toggle-pose invasion)
      (update-direction bounds)
      (update-position bounds)
      (assoc :since-last-move 0)))

(defn update [invasion {:keys [delta bounds]}]
  (let [since-last-move (-> (get invasion :since-last-move 0)
                            (+ delta))]
    (if (>= since-last-move (:time-to-move invasion))
      (move invasion bounds)
      (assoc invasion :since-last-move since-last-move))))

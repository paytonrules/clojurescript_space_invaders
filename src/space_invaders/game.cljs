(ns space-invaders.game
  (:require [cljs.core.async :as ca]
            [clojure.string :as string]
            [space-invaders.transitions :as transitions]
            [util.game-loop :as game-loop]
            [util.image-loader :as image-loader]))

(declare invader-states)
(declare invader-types)
; These are helper functions. Not sure if they belong here, but I don't have a
; clean ns yet
(defn invader->image-path [invader state]
  (str "images/" (name invader) "_" (name state) ".png"))

(defn enemy-images []
  (for [invader invader-types
        state invader-states]
    (invader->image-path invader state)))

(defn image-path->invader-state [image-path]
  (map keyword (-> (string/split image-path "/")
                   (last)
                   (string/replace ".png" "")
                   (string/split "_"))))

; Game State machine
(def row-length 11)
(def velocity 60)

(def invader-types [:small :medium :large])
(def invader-states [:open :closed])

(defonce initial-app-state
  {:ticks 0
   :invaders [(take row-length (repeat :small))
              (take row-length (repeat :medium))
              (take row-length (repeat :medium))
              (take row-length (repeat :large))
              (take row-length (repeat :large))]})

(defmulti update-game
  (fn
    ([state] (get-in state [:state :name]))
    ([state event] [(get-in state [:state :name]) (:name event)])))

(defmethod update-game nil [state]
  (-> state
      (assoc-in [:state] initial-app-state)
      (assoc-in [:state :name] :loading-images)
      (assoc :transitions [(partial transitions/load-images! (enemy-images))])))

(defmethod update-game :loading-images [state]
  state)

(defn- image->lookup [table image]
  (assoc-in table (image-path->invader-state (.-src image)) image))

(defmethod update-game [:loading-images :images-loaded] [state event]
  (let [image-lookup (->> (:images event)
                          (reduce image->lookup {}))]
    (-> (assoc-in state [:state :name] :playing)
        (assoc-in [:state :images] image-lookup))))

(defmethod update-game :playing [state]
  (->> (get-in state [:state :ticks] 0)
       (inc)
       (assoc-in state [:state :ticks])))

; Queries - useable in the view
(defn invader-position [{:keys [ticks]}]
  (if (even? (quot ticks velocity))
    :open
    :closed))

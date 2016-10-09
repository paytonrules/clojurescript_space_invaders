(ns space-invaders.game
  (:require [cljs.core.async :as ca]
            [clojure.string :as string]
            [space-invaders.invasion :as invasion]
            [space-invaders.transitions :as transitions]
            [util.game-loop :as game-loop]
            [util.image-loader :as image-loader]
            [util.time :as t]))

; This all looks like it should be moved to invasion
(defn invader->image-path [invader state]
  (str "images/" (name invader) "_" (name state) ".png"))

(defn image-lookup [{:keys [state]} image variant]
  (get-in state [:images image variant]))

(defn enemy-images []
  (for [invader invasion/invader-types
        state invasion/invader-states]
    (invader->image-path invader state)))

(defn image-path->invader-state [image-path]
  (map keyword (-> (string/split image-path "/")
                   (last)
                   (string/replace ".png" "")
                   (string/split "_"))))
; ^ This probably belongs in invasion

(def velocity 1000) ; velocity? Again


(defonce initial-app-state {:invasion invasion/initial})

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

(defn update-last-timestamp [game-state epoch]
  (assoc game-state :last-timestamp epoch))

(defn update-last-move [state epoch]
  (if-let [last-move (:since-last-move state)]
    (assoc state :since-last-move
           (+ last-move (- epoch (:last-timestamp state))))
    (assoc state :since-last-move 0)))

(defn update-ticks [game-state]
  (if (>= (:since-last-move game-state) velocity)
    (let [ticks (inc (:ticks game-state))]
      (-> (assoc game-state :since-last-move 0)
          (assoc :ticks ticks)))
    game-state))

(defmethod update-game :playing [state]
  (let [epoch (t/epoch)
        new-game (-> (:state state)
                     (update-last-move epoch)
                     (update-last-timestamp epoch)
                     (update-ticks))]
    (assoc state :state new-game)))

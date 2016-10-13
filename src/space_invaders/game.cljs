(ns space-invaders.game
  (:require [clojure.string :as string]
            [space-invaders.invasion :as invasion]
            [space-invaders.transitions :as transitions]
            [util.game-loop :as game-loop]
            [util.image-loader :as image-loader]
            [util.time :as t]))

(def bounds {:left 3 :right 214})
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

(defonce initial-app-state
  {:invasion invasion/initial
   :bounds {:left 1 :right 220}})

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

(defn update-invasion [{:keys [invasion] :as game} delta]
  (->> (invasion/update invasion {:delta delta
                                  :bounds bounds})
       (assoc game :invasion)))

(defmethod update-game :playing [state]
  (let [epoch (t/epoch)
        delta (if-let [last-timestamp (:last-timestamp (:state state))]
                (- epoch last-timestamp)
                0)
        new-game (-> (:state state)
                     (update-invasion delta)
                     (update-last-timestamp epoch))]
    (assoc state :state new-game)))

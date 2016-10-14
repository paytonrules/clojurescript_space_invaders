(ns space-invaders.game
  (:require [space-invaders.image-lookup :as image-lookup]
            [space-invaders.invasion :as invasion]
            [space-invaders.transitions :as transitions]
            [util.game-loop :as game-loop]
            [util.image-loader :as image-loader]
            [util.time :as t]))

(def bounds {:left 3 :right 212})

(defonce initial-app-state
  {:invasion invasion/initial
   :bounds {:left 1 :right 220}})

(defn all-image-paths []
  (-> (invasion/invaders-and-states)
      (image-lookup/character-states->image-path)))

(defmulti update-game
  (fn
    ([state] (get-in state [:state :name]))
    ([state event] [(get-in state [:state :name]) (:name event)])))

(defmethod update-game nil [state]
  (-> state
      (assoc-in [:state] initial-app-state)
      (assoc-in [:state :name] :loading-images)
      (assoc :transitions [(partial transitions/load-images! (all-image-paths))])))

(defmethod update-game :loading-images [state]
  state)

(defmethod update-game [:loading-images :images-loaded] [state event]
  (let [image-lookup (image-lookup/image-list->lookup-table (:images event))]
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

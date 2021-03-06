(ns space-invaders.game
  (:require [space-invaders.bullet :as bullet]
            [space-invaders.events :as events]
            [space-invaders.image-lookup :as image-lookup]
            [space-invaders.invasion :as invasion]
            [space-invaders.laser :as laser]
            [util.game-loop :as game-loop]
            [util.image-loader :as image-loader]
            [util.time :as t]))

(def bounds {:top 15 :left 3 :right 212})
(def resolution {:w 217 :h 248})

(defonce initial-app-state
  {:invasion invasion/initial
   :laser laser/initial
   :bounds {:left 1 :right 220}})

(defn all-image-paths []
  (-> (invasion/invaders-and-states)
			(conj laser/character)
      (conj bullet/character)
      (image-lookup/character-states->image-path)))

(defmulti update-game
  (fn
    ([state] (get-in state [:game :name]))
    ([state event] [(get-in state [:game :name]) (:name event)])))

(defmethod update-game nil [state]
  (-> state
      (assoc-in [:game] initial-app-state)
      (assoc-in [:game :name] :loading-images)
      (assoc :transitions [(partial events/load-images! (all-image-paths))])))

(defmethod update-game :loading-images [state]
  state)

(defmethod update-game [:loading-images :images-loaded] [state event]
  (let [image-lookup (image-lookup/image-list->lookup-table (:images event))]
    (-> (assoc-in state [:game :name] :playing)
        (assoc-in [:game :images] image-lookup))))

(defmethod update-game [:playing :move-left] [state event]
  (->> (laser/move-left (get-in state [:game :laser]))
       (assoc-in state [:game :laser])))

(defmethod update-game [:playing :move-right] [state event]
  (->> (laser/move-right (get-in state [:game :laser]))
       (assoc-in state [:game :laser])))

(defn- create-bullet [{:keys [laser]}]
   (bullet/create (laser/midpoint laser)))

(defn- bullet-present? [game]
  (:bullet game))

(defmethod update-game [:playing :fire] [{:keys [game] :as state} event]
  (if (bullet-present? game)
    state
    (assoc-in state [:game :bullet] (create-bullet game))))

(defn update-last-timestamp [game epoch]
  (assoc game :last-timestamp epoch))

(defn update-invasion [{:keys [invasion] :as game} delta]
  (->> (invasion/update invasion {:delta delta
                                  :bounds bounds})
       (assoc game :invasion)))

(defn update-laser [{:keys [laser] :as game} delta]
  (->> (laser/update laser {:delta delta :bounds bounds})
       (assoc game :laser)))

(defn- bullet-off-screen [bullet]
  (< (:y (:position bullet)) (- (:top bounds) bullet/height)))

(defn update-bullet [{:keys [bullet] :as game} delta]
  (if bullet
    (if (bullet-off-screen bullet)
      (dissoc game :bullet)
      (->> (bullet/update bullet delta)
           (assoc game :bullet)))
    game))

(defmethod update-game :playing [state]
  (let [epoch (t/epoch)
        delta (if-let [last-timestamp (:last-timestamp (:game state))]
                (- epoch last-timestamp)
                0)
        new-game (-> (:game state)
                     (update-invasion delta)
                     (update-laser delta)
                     (update-bullet delta)
                     (update-last-timestamp epoch))]
    (assoc state :game new-game)))

(ns util.game-loop
  (:require [cljs.core.async :as ca])
  (:require-macros [cljs.core.async.macros :refer [go-loop]]))

(defrecord GameState [state transitions quit])

(defn ->initial-game-state
  ([]
   (->initial-game-state {}))
  ([state]
    (->GameState state [] false)))

(defn ->quit-state
  ([] (->quit-state {} []))
  ([state transitions]
   (->GameState state transitions true)))

(def state (atom nil))
(def events (atom []))

(defn clear-events! []
  (reset! events []))

(defn fire-event! [evt]
  (swap! events conj evt))

(defn take-event! []
  (let [evt (first @events)]
    (swap! events (comp vec rest))
    evt))

(defn- update-loop! [draw update request-next-frame]
  (let [new-state (-> (reduce update @state @events)
                      (update))]
    (doseq [transition (:transitions new-state)]
      (transition))
    (reset! state (assoc new-state :transitions []))
    (clear-events!)
    (draw @state)
    (when-not (true? (:quit @state))
      (request-next-frame #(update-loop! draw update request-next-frame)))))

(defn start!
  ([options]
   (start! options js/requestAnimationFrame))

  ([{:keys [draw update]} request-next-frame]
   (clear-events!)
   (reset! state (->initial-game-state))
   (request-next-frame #(update-loop! draw update request-next-frame))))

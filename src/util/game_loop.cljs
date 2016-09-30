(ns util.game-loop
  (:require [cljs.core.async :as ca])
  (:require-macros [cljs.core.async.macros :refer [go-loop]]))

(def state (atom nil))
(def events (atom []))

(defn clear-events! []
  (reset! events []))

(defn fire-event! [evt]
  (swap! events conj evt))

(defn take-event! [cb]
  (let [evt (first @events)]
    (swap! events (comp vec rest))
    (cb evt)))

(defn- update-state! [update-fn event]
  (update-fn nil event))

(declare update-loop!)

(defn- update-next-frame! [draw update new-state request-next-frame]
  (reset! state new-state)
  (draw @state)
  (request-next-frame #(update-loop! draw update request-next-frame)))


(defn- update-loop!
  ([draw update request-next-frame]
   (let [event-added-state (reduce
                             (fn [state event]
                               (update state event))
                             @state
                             @events)
         next-state (update event-added-state :tick)] ; TODO just have a default that is :tick
     (when-not (true? (:quit next-state))
       (update-next-frame! draw update next-state request-next-frame))))
  ([draw update initial-state request-next-frame]
   (update-next-frame! draw update initial-state request-next-frame)))

(defn start!
  ([options]
   (start! options js/requestAnimationFrame))

  ([{:keys [draw update initial-state]} request-next-frame]
   (update-loop! draw update initial-state request-next-frame)))

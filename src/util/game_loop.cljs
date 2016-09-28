(ns util.game-loop)

(def state (atom nil))

(defn- update-loop
  ([draw update request-next-frame]
   (let [next-state (update @state)]
     (when-not (true? (:quit next-state))
       (reset! state next-state)
       (draw @state)
       (request-next-frame #(update-loop draw update request-next-frame)))))
  ([draw update initial-state request-next-frame]
   (reset! state initial-state)
   (draw @state)
   (request-next-frame #(update-loop draw update request-next-frame))))

(defn start!
  ([options]
   (start! options js/requestAnimationFrame))

  ([{:keys [draw update initial-state]} request-next-frame]
   (update-loop draw update initial-state request-next-frame)))

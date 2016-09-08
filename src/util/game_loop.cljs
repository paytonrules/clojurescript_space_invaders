(ns util.game-loop)

(defn start
  ([options]
   (start options js/requestAnimationFrame))

  ([{:keys [draw update state]} request-next-frame]
   (defn update-loop [current-state]
     (draw current-state)
     (let [next-state (update current-state)]
       (when-not (true? (:quit next-state))
         (request-next-frame #(update-loop (:state next-state))))))

   (update-loop state)))


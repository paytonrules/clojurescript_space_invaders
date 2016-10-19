(ns ^:figwheel-always util.game-loop-test
  (:require [cljs.test :refer-macros [async is testing]]
            [runners.devcards :refer [dev-cards-runner]]
            [util.game-loop :as gl]))

(def ^{:private true} drawn-state (atom nil))
(defn- clear-draw-spy []
  (reset! drawn-state nil))

(defn- draw-spy [state]
  (reset! drawn-state state))

(def ^{:private true} requested-frames (atom '()))
(def ^{:private true} requested-frame-count (atom 0))

(defn- request-next-frame [cb]
  (swap! requested-frames conj cb)
  (swap! requested-frame-count inc))

(defn- render-next-frame! []
  (let [frame (first @requested-frames)]
    (swap! requested-frames rest)
    (frame)))

(defn- reset-frames! []
  (reset! requested-frames '())
  (reset! requested-frame-count 0))

(defn put-one-event []
  (gl/clear-events!)
  (gl/fire-event! {:event :exists})
  (is (= {:event :exists} (gl/take-event!))))

(defn put-two-events []
  (gl/clear-events!)
  (gl/fire-event! {:event :exists})
  (gl/fire-event! {:also :exists})

  (is (= {:event :exists} (gl/take-event!)))
  (is (= {:also :exists} (gl/take-event!))))

(defn start-begins-the-loop  []
  (reset-frames!)
  (reset! gl/events ["not" "nil"])
  (reset! gl/state {:something :wrong})

  (testing "on start"
    (gl/start! {:draw identity
                :update identity}
               request-next-frame)

    (testing "all events are cleared"
      (is (= [] @gl/events)))

    (testing "the next frame is requested"
      (is (= 1 @requested-frame-count)))

    (testing "the state is set to initial state"
      (is (= (gl/->initial-game-state) @gl/state)))))

(defn draw-and-render-frame []
  (reset-frames!)
  (gl/start! {:draw draw-spy
              :update #(assoc-in % [:game :update-count]
                                 (inc (get-in % [:game :update-count] 0)))}
             request-next-frame)

  (testing "updates, draws the new state, and requests the next frame"
    (render-next-frame!)

    (is (= (gl/->initial-game-state {:update-count 1}) @drawn-state))
    (is (= 2 @requested-frame-count)))

  (testing "updates a second time, keeping track of the state"
    (render-next-frame!)

    (is (= (gl/->initial-game-state {:update-count 2}) @drawn-state))
    (is (= 3 @requested-frame-count))))

(defn ending-the-game-loop []
  (reset-frames!)
  (gl/start! {:draw draw-spy
              :update #(assoc % :quit true)}
             request-next-frame)

  (testing "it does not request another frame when the quit flag is set"
    (is (= 1 @requested-frame-count))
    (render-next-frame!)
    (is (= 1 @requested-frame-count))))

(defn- event-processing-update-func
  ([state]
   (->> (get-in state [:game :events] [])
        (count)
        (assoc-in state [:game :event-count])))
  ([state event]
   (as-> (get-in state [:game :events] []) _
         (conj _ event)
         (assoc-in state [:game :events] _))))

(defn event-processing []
  (reset-frames!)
  (gl/start! {:draw draw-spy
              :update event-processing-update-func}
             request-next-frame)

  (testing "events are processed in order, before the standard update"
    (gl/fire-event! {:any :structure})
    (gl/fire-event! {:is :fine})

    (render-next-frame!)

    (is (= (gl/->initial-game-state
             {:events [{:any :structure} {:is :fine}]
              :event-count 2})
           @drawn-state)))

  (testing "events are cleared and will not re-fire on the next update"
    (render-next-frame!)

    (is (= (gl/->initial-game-state
             {:events [{:any :structure} {:is :fine}]
              :event-count 2})
           @drawn-state))))

(def ^{:private true} transition-call-count (atom 0))
(defn- transition-func []
  (swap! transition-call-count inc))

(defn- transition-processing-update-func
  ([state]
    (as-> (:transitions state) _
          (conj _ transition-func)
          (assoc state :transitions _))))

(defn processing-transitions []
  (reset-frames!)
  (gl/start! {:draw draw-spy
              :update transition-processing-update-func}
             request-next-frame)

  (testing "A side-effect transtion is fired after it is returned by the update"
    (render-next-frame!)
    (is (= 1 @transition-call-count)))

  (testing "Transitions are reset on each fame"
    (render-next-frame!)
    (is (= 2 @transition-call-count))))

(dev-cards-runner #"util.game-loop-test")

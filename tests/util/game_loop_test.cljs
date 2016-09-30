(ns ^:figwheel-always util.game-loop-test
  (:require [cljs.test :refer-macros [async is testing]]
            [runners.devcards :refer [dev-cards-runner]]
            [util.game-loop :as game-loop]))

(def ^{:private true} requested-frames (atom '()))
(def ^{:private true} drawn-state (atom nil))

(defn- draw [state]
  (reset! drawn-state state))

(defn- request-next-frame [cb]
  (swap! requested-frames conj cb))

(defn- frames-requested []
  (count @requested-frames))

(defn- call-next-frame! []
  (let [frame (first @requested-frames)]
    (swap! requested-frames rest)
    (frame)))

(defn- reset-requested-frames! []
  (reset! requested-frames '()))

(defn put-one-event []
  (game-loop/clear-events!)
  (async done
    (game-loop/fire-event! {:event :exists})
    (game-loop/take-event!
      (fn [evt]
        (is (= {:event :exists} evt))
        (done)))))

(defn put-two-events []
  (game-loop/clear-events!)
  (async done
    (game-loop/fire-event! {:event :exists})
    (game-loop/fire-event! {:also :exists})
    (game-loop/take-event!
      (fn [evt]
        (is (= {:event :exists} evt))
        (game-loop/take-event!
          (fn [evt]
            (is (= {:also :exists} evt))
            (done)))))))

(defn game-loop-can-be-started-and-stopped []
  (reset-requested-frames!)

  (game-loop/start!
    {:draw draw
     :update (fn [state event] {:quit true})
     :state {}} request-next-frame)

  (is (= 1 (frames-requested)))

  (call-next-frame!)

  (is (= 0 (frames-requested))))

(defn start-and-update []
  (testing "starts with an initial game state"
    (game-loop/start!
      {:draw draw
       :update (fn [state event] {:quit true})
       :initial-state {:initial :state}}
      request-next-frame)

    (is (= {:initial :state} @game-loop/state)))

  (testing "draws the initial state"
    (game-loop/start!
      {:draw draw
       :update (fn [state event] {:quit true})
       :initial-state {:initial :state}}
      request-next-frame)

    (is (= {:initial :state} @drawn-state)))

  (testing "updates the state on the next frame, based on the previous state"
    (let [old-state (atom nil)]
      (reset-requested-frames!)
      (game-loop/clear-events!)

      (game-loop/start!
        {:draw draw
         :update (fn [state event]
                   (if (= state {:new :state})
                     {:quit true}
                     (do
                       (reset! old-state state)
                       {:new :state})))
         :initial-state {:initial :state}}
        request-next-frame)

      (is (= {:initial :state} @game-loop/state))

      (call-next-frame!)

      (is (= {:initial :state} @old-state))
      (is (= {:new :state} @game-loop/state))))

  (testing "treats each update as a tick event"
    (let [event (atom nil)]
      (reset-requested-frames!)

      (game-loop/start!
        {:draw draw
         :update (fn [state e] (reset! event e) {:quit true})
         :initial-state {:initial :state}}
        request-next-frame)
      (call-next-frame!)

      (is (= :tick @event))))

  (testing "draws the updated state"
    (reset-requested-frames!)

    (let [last-update (atom nil)
          update (fn [state event]
                   (if (= state {:second :state})
                     {:quit true}
                     {:second :state}))]

      (game-loop/start!
        {:draw draw
         :update update
         :initial-state {:initial :state}}
        request-next-frame)
      (call-next-frame!)

      (is (= {:second :state} @drawn-state)))))

(defn process-events []
  (testing "an event updates state before the tick"
    (game-loop/clear-events!)
    (let [update (fn [state event]
                   (if (= event :testing-event)
                     :event-based-state
                     (is (= :event-based-state state))))]

      (game-loop/fire-event! :testing-event)
      (game-loop/start!
        {:draw draw
         :update update
         :state {}}
        request-next-frame)
      (call-next-frame!)))

  (testing "all events are processed before the tick"
    (game-loop/clear-events!)
    (let [update (fn [state event]
                   (if (= event :testing-event-two)
                     :event-based-state
                     (when-not (= event :testing-event)
                       (is (= :event-based-state state)))))]

      (game-loop/fire-event! :testing-event)
      (game-loop/fire-event! :testing-event-two)
      (game-loop/start!
        {:draw draw
         :update update
         :state {}}
        request-next-frame)
      (call-next-frame!))))
(dev-cards-runner #"util.game-loop-test")

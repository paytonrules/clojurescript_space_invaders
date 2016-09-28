(ns util.game-loop-test
  (:require [cljs.test :refer-macros [is testing use-fixtures]]
            [devcards.core :refer-macros [deftest]]
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

(defn start-and-update []
  (testing "starts with an initial game state"
    (game-loop/start!
      {:draw draw
       :update (fn [state] {:new :state})
       :initial-state {:initial :state}}
      request-next-frame)

    (is (= {:initial :state} @game-loop/state)))

  (testing "draws the initial state"
    (game-loop/start!
      {:draw draw
       :update (fn [state] {:new :state})
       :initial-state {:initial :state}}
      request-next-frame)

    (is (= {:initial :state} @drawn-state)))

  (testing "updates the state on the next frame, based on the previous state"
    (let [old-state (atom nil)]
      (reset-requested-frames!)

      (game-loop/start!
        {:draw draw
         :update (fn [state]
                   (reset! old-state state)
                   {:new :state})
         :initial-state {:initial :state}}
        request-next-frame)

      (is (= {:initial :state} @game-loop/state))

      (call-next-frame!)

      (is (= {:initial :state} @old-state))
      (is (= {:new :state} @game-loop/state))))

  (testing "draws the updated state"
    (reset-requested-frames!)

    (let [last-update (atom nil)
          update (fn [state] {:second :state})]

      (game-loop/start!
        {:draw draw
         :update update
         :initial-state {:initial :state}}
        request-next-frame)
      (call-next-frame!)

      (is (= {:second :state} @drawn-state)))))

(defn start-will-quit []
  (reset-requested-frames!)

  (testing "do not request the next frame when update returns quit"
    (game-loop/start!
      {:draw draw
       :update (fn [] {:quit true})
       :state {}} request-next-frame)

    (is (= 1 (frames-requested)))

    (call-next-frame!)

    (is (= 0 (frames-requested)))))

(deftest game-loop-tests
  "# start-will-quit"
  (start-will-quit)
  "# start-and-update"
  (start-and-update))

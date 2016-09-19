(ns util.game-loop-test
  (:require [cljs.test :refer-macros [is testing use-fixtures]]
            [devcards.core :refer-macros [deftest]]
            [util.game-loop :as game-loop]))

(def requested-frames (atom '()))
(def drawn-state (atom nil))

(defn draw [state]
  (reset! drawn-state state))

(defn request-next-frame [cb]
  (swap! requested-frames conj cb))

(defn frames-requested []
  (count @requested-frames))

(defn call-next-frame! []
  (let [frame (first @requested-frames)]
    (swap! requested-frames rest)
    (frame)))

(use-fixtures :each {:before #(reset! requested-frames '())})

(deftest start-and-update
  (testing "draw, update then request the next frame"
    (let [last-update (atom nil)
          update #(reset! last-update %)]
      (game-loop/start {:draw draw :update update :state "initial game state"}
                       request-next-frame)

      (is (= "initial game state" @drawn-state))
      (is (= "initial game state" @last-update))
      (is (= 1 (frames-requested))))))

(deftest start-will-quit
  (testing "do not request the next frame when update returns quit"
    (game-loop/start
      {:draw draw :update (fn [] {:quit true}) :state {}} request-next-frame)

    (is (= 0 (frames-requested)))))

(deftest the-next-update
  (testing "the next frame should use the new state from update from drawing and updating"
    (let [last-update (atom nil)
          update (fn [state] (reset! last-update state) {:state "new game state"})]
      (game-loop/start {:draw draw :update update :state {}} request-next-frame)

      (call-next-frame!)

      (is (= "new game state" @drawn-state))
      (is (= "new game state" @last-update)))))

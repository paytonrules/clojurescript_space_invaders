(ns ^:figwheel-always space-invaders.invaders-test
  (:require [cljs.test :refer-macros [is testing]]
            [runners.devcards :refer-macros [dev-cards-runner]]
            [space-invaders.invaders :as invaders]
            [util.game-loop :as game-loop]))

(defn test-toggle-back-and-forth []
  (testing "the invaders toggle between two states based on the number of tics"
    (is (= :open (invaders/pose 0)))
    (is (= :closed (invaders/pose 1)))))

(defn should-calculate-positions []
  (testing "starts at the upper-left corner from the ticks"
    (is (= invaders/start-position (invaders/position 0))))

  (testing "moves to the right on the first tick"
    (is (= (+ invaders/velocity (:x invaders/start-position))
           (:x (invaders/position 1))))
    (is (= (:y invaders/start-position) (:y (invaders/position 1)))))

  (testing "gets x from type, ticks and column"
    (is (= (+ (:x invaders/start-position))
           (invaders/x-position {:invader :small :column 0 :ticks 0}))))

  (testing "can put an invader in each column, handling padding"
    (is (= (+ (:x invaders/start-position) invaders/column-width))
           (invaders/x-position {:column 1 :ticks 0}))
    (is (= (+ (:x invaders/start-position) (* 2 invaders/column-width)))
           (invaders/x-position {:column 2 :ticks 0})))

  (testing "gets y from start-point and row-height, from the row"
    (is (= (:y invaders/start-position) (invaders/y-position 0)))
    (is (= (+ (:y invaders/start-position) invaders/row-height)
           (invaders/y-position 1)))
    (is (= (+ (:y invaders/start-position) (* 2 invaders/row-height))
           (invaders/y-position 2)))))



(dev-cards-runner #"space-invaders.invaders-test")



(ns ^:figwheel-always space-invaders.invasion-test
  (:require [cljs.test :refer-macros [is testing]]
            [runners.devcards :refer-macros [dev-cards-runner]]
            [space-invaders.invasion :as invasion]
            [util.game-loop :as game-loop]))

(defn test-toggle-back-and-forth []
  (testing "the invaders toggle between two states based on the number of tics"
    (is (= :open (invasion/pose 0)))
    (is (= :closed (invasion/pose 1)))))

(defn should-calculate-positions []
  (testing "starts at the upper-left corner from the ticks"
    (is (= invasion/start-position (invasion/position 0))))

  (testing "moves left or right based on velocity"
    (is (= (+ invasion/velocity (:x invasion/start-position))
           (:x (invasion/position {:ticks 1 :direction :right}))))
    (is (= (- (:x invasion/start-position) invasion/velocity )
           (:x (invasion/position {:ticks 1 :direction :left})))))

  (testing "y's position is always based on row, for now"
    (is (= (:y invasion/start-position) (:y (invasion/position 1)))))

  (testing "gets x from type, ticks and column"
    (is (= (+ (:x invasion/start-position))
           (invasion/x-position {:invader :small :column 0 :ticks 0}))))

  (testing "can put an invader in each column, handling padding"
    (is (= (+ (:x invasion/start-position) invasion/column-width))
           (invasion/x-position {:column 1 :ticks 0}))
    (is (= (+ (:x invasion/start-position) (* 2 invasion/column-width)))
           (invasion/x-position {:column 2 :ticks 0})))

  (testing "gets y from start-point and row-height, from the row"
    (is (= (:y invasion/start-position) (invasion/y-position 0)))
    (is (= (+ (:y invasion/start-position) invasion/row-height)
           (invasion/y-position 1)))
    (is (= (+ (:y invasion/start-position) (* 2 invasion/row-height))
           (invasion/y-position 2)))))

(defn should-calculate-the-right-most-edge []
  (let [start-in-x (:x invasion/start-position)]
    (testing "with only one row remaining"
      (testing "from one invader"
        (is (= (+ start-in-x invasion/column-width)
               (invasion/right-edge {:invaders [[:small]] :ticks 0}))))

      (testing "from two invaders"
        (is (= (+ start-in-x (* 2 invasion/column-width))
               (invasion/right-edge {:invaders [[:small :small]] :ticks 0})))))

    (testing "with multiple rows it uses the longest row"
      (is (= (+ start-in-x (* 2 invasion/column-width))
             (invasion/right-edge {:invaders [[:small]
                                              [:small :small]] :ticks 0}))))

    (testing "account for the movement of the invasion"
      (is (= (+ start-in-x invasion/column-width invasion/velocity)
             (invasion/right-edge {:invaders [[:small]] :ticks 1 :direction :right}))))))

(dev-cards-runner #"space-invaders.invasion-test")



(ns ^:figwheel-always space-invaders.invasion-test
  (:require [cljs.test :refer-macros [is testing]]
            [runners.devcards :refer-macros [dev-cards-runner]]
            [space-invaders.invasion :as invasion]
            [util.game-loop :as game-loop]))

(defn test-toggle-back-and-forth []
  (testing "the invaders toggle between two states based on the number of tics"
    (is (= :open (invasion/pose {:ticks 0})))
    (is (= :closed (invasion/pose {:ticks 1})))))

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
    (is (= (:x invasion/start-position)
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

(defn should-calcluate-the-next-state []
  (testing "accumulate time since last move based on delta"
    (testing "on the first update store the delta"
      (let [delta 5
            new-state (invasion/update invasion/initial delta)]
        (is (= delta (:since-last-move new-state)))))

    (testing "on subsequent updates accumulate delta"
      (let [delta 5
            new-state (-> (invasion/update invasion/initial delta)
                          (invasion/update delta))]
        (is (= (* 2 delta) (:since-last-move new-state)))))

    (testing "toggle pose every time-to-move"
      (let [time-to-move 1000
            original-state  (-> invasion/initial
                                 (assoc :pose :open)
                                 (assoc :since-last-move 999)
                                 (assoc :time-to-move time-to-move))]

        (testing "when below time-to-move don't toggle pose"
          (let [delta 0
                new-state (invasion/update original-state delta)]
            (is (= :open (:pose new-state)))))

        (testing "when at time-to-move toggle pose"
          (let [delta 1
                new-state (invasion/update original-state delta)]
            (is (= :closed (:pose new-state)))))

        (testing "when crossing time-to move toggle pose"
          (let [delta 2
                new-state (invasion/update original-state delta)]
            (is (= :closed (:pose new-state)))))

        (testing "when closed, toggle the pose to open"
          (let [delta 1
                closed-state (assoc original-state :pose :closed)
                new-state (invasion/update closed-state delta)]
            (is (= :open (:pose new-state)))))

        (testing "only toggle every 'time-to-move' by resetting"
          (let [delta 1
                new-state (-> (invasion/update original-state delta)
                              (invasion/update delta))]
            (is (= :closed (:pose new-state)))))



        )
      )
    )
  )

(dev-cards-runner #"space-invaders.invasion-test")

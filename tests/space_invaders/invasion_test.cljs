(ns ^:figwheel-always space-invaders.invasion-test
  (:require [cljs.test :refer-macros [is testing]]
            [runners.devcards :refer-macros [dev-cards-runner]]
            [space-invaders.invasion :as invasion]
            [util.game-loop :as game-loop]))

(defn test-toggle-back-and-forth []
  (testing "the invaders toggle between two states based on the number of tics"
    (is (= :open (invasion/pose {:pose :open})))
    (is (= :closed (invasion/pose {:pose :closed})))))

(defn should-provide-invader-positions []
  (let [invasion {:position {:x 10 :y 10}}]
    (testing "starts at the invasion position for 0, 0"
      (is (= {:x 10 :y 10} (invasion/invader-position
                             invasion
                             {:row 0 :col 0}))))

    (testing "each column is column-width"
      (is (= {:x (+ 10 invasion/column-width) :y 10}
             (invasion/invader-position invasion {:row 0 :col 1})))
      (is (= {:x (+ 10 (* 2 invasion/column-width)) :y 10}
             (invasion/invader-position invasion {:row 0 :col 2}))))

    (testing "each row is row-height"
      (is (= {:x 10 :y (+ 10 invasion/row-height)}
             (invasion/invader-position invasion {:row 1 :col 0}))))))

(defn should-calculate-the-right-most-edge []
  (let [initial-invasion {:position {:x 2}}]
    (testing "with only one row"
      (testing "one invader"
        (let [invasion (assoc initial-invasion :invaders [[:small]])]
          (is (= (+ 2 invasion/column-width)
                 (invasion/right-edge invasion)))))

      (testing "many invaders"
        (let [invasion (assoc initial-invasion :invaders [[:small :small]])]
          (is (= (+ 2 (* 2 invasion/column-width))
                 (invasion/right-edge invasion))))))

    (testing "with multiple rows it uses the longest row"
      (let [invasion (assoc initial-invasion :invaders [[:small]
                                                        [:small :small]])]
        (is (= (+ 2 (* 2 invasion/column-width))
               (invasion/right-edge invasion)))))))

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

        (testing "only toggle every 'time-to-move'"
          (let [delta 1
                new-state (-> (invasion/update original-state delta)
                              (invasion/update delta))]
            (is (= :closed (:pose new-state)))))))

    (testing "move on every update - based on time-to-move"
      (let [time-to-move 1000
            original-state (-> invasion/initial
                               (assoc :since-last-move 999)
                               (assoc :time-to-move time-to-move)
                               (assoc :position {:x 0 :y 0})
                               (assoc :direction :right))]

        (testing "doesn't move before it is time"
          (let [delta 0
                new-state (invasion/update original-state delta)]
            (is (= {:x 0 :y 0} (:position new-state)))))

        (testing "moves the correct direction on time-to-move"
          (let [delta 1
                new-state (invasion/update original-state delta)]
            (is (= {:x invasion/velocity :y 0} (:position new-state)))))

        (testing "moves the correct direction crossing time-to-move"
          (let [delta 2
                new-state (invasion/update original-state delta)]
            (is (= {:x invasion/velocity :y 0} (:position new-state)))))

        (testing "moves respect the direction"
          (let [delta 1
                moving-left (assoc original-state :direction :left)
                new-state (invasion/update moving-left delta)]
            (is (= {:x (- invasion/velocity) :y 0} (:position new-state)))))


        ))
    ))

(dev-cards-runner #"space-invaders.invasion-test")

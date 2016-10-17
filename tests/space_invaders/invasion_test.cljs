(ns ^:figwheel-always space-invaders.invasion-test
  (:require [cljs.test :refer-macros [is testing]]
            [runners.devcards :refer-macros [dev-cards-runner]]
            [space-invaders.invasion :as invasion]))

(defn should-convert-a-matrix-to-names-and-row-col []
  (testing "a matrix with one row, one column"
    (is (= [{:character :small :row 0 :col 0}] (invasion/character-matrix->vector [[:small]]))))

  (testing "a matrix with one row, two columns"
    (let [expected-list [{:character :small :row 0 :col 0} {:character :small :row 0 :col 1}]]
      (is (= expected-list (invasion/character-matrix->vector [[:small :small]])))))

  (testing "a matrix with two rows, one column"
    (let [original-list [[:small] [:medium]]
          expected-list [{:character :small :row 0 :col 0}
                         {:character :medium :row 1 :col 0}]]
      (is (= expected-list (invasion/character-matrix->vector original-list))))))

(defn should-convert-the-row-col-to-offsets []
  (testing "maps the row/col to actual offsets"
    (let [row-col-hash [{:character :small :row 3 :col 2}]
          list-with-offsets (invasion/row-cols->offsets row-col-hash)
          entry (first list-with-offsets)]
      (is (= :small (:character entry)))
      (is (= (* 3 invasion/row-height) (get-in entry [:offset :y])))
      (is (= (* 2 invasion/column-width) (get-in entry [:offset :x]))))))

(defn should-provide-the-invaders-and-states []
  (testing "created from types and states"
    (is (some #(= {:character :small :state :closed}) (invasion/invaders-and-states)))
    (is (some #(= {:character :small :state :open}) (invasion/invaders-and-states)))
    (is (some #(= {:character :medium :state :open}) (invasion/invaders-and-states)))))

(defn test-toggle-back-and-forth []
  (testing "the invaders toggle between two states based on the number of tics"
    (is (= :open (invasion/pose {:pose :open})))
    (is (= :closed (invasion/pose {:pose :closed})))))

(defn should-provide-invader-positions []
  "These tests require the known order. The clients shouldn't."
  (let [invasion (-> invasion/initial
                     (assoc :position {:x 10 :y 10}))]
    (testing "starts at the invasion position for 0, 0"
      (is (= {:character :small :position {:x 10 :y 10}}
             (first (invasion/invader-positions invasion)))))

    (testing "each column is column-width"
      (is (= {:character :small :position {:x (+ 10 invasion/column-width) :y 10}}
             (second (invasion/invader-positions invasion)))))

    (testing "each row is row-height"
      (let [second-row (nth (invasion/invader-positions invasion)
                            invasion/row-length)]
        (is (= {:character :medium :position {:x 10 :y (+ 10 invasion/row-height)}}
               second-row))))))

(defn should-calculate-the-right-most-edge []
  (let [initial-invasion {:position {:x 2}}]
    (testing "with only one row"
      (testing "one invader"
        (let [one-invader [{:character :small :offset {:x 10 :y 0}}]
              invasion (assoc initial-invasion :invaders one-invader)]
          (is (= (+ 2 10 invasion/column-width)
                 (invasion/right-edge invasion)))))

      (testing "many invaders uses the largest x"
        (let [many-invaders [{:character :small :offset {:x 10 :y 0}}
                             {:character :small :offset {:x 50 :y 0}}]
              invasion (assoc initial-invasion :invaders many-invaders)]
          (is (= (+ 2 50 invasion/column-width)
                 (invasion/right-edge invasion))))))))

(defn should-calcluate-the-next-state []
  (testing "accumulate time since last move based on delta"
    (testing "on the first update store the delta"
      (let [options {:delta 5 :bounds {:right 10000}}
            new-state (invasion/update invasion/initial options)]
        (is (= 5 (:since-last-move new-state)))))

    (testing "on subsequent updates accumulate delta"
      (let [options {:delta 5}
            new-state (-> (invasion/update invasion/initial options)
                          (invasion/update options))]
        (is (= 10 (:since-last-move new-state)))))

    (testing "toggle pose every time-to-move"
      (let [time-to-move 1000
            original-state  (-> invasion/initial
                                (assoc :pose :open)
                                (assoc :since-last-move 999)
                                (assoc :time-to-move time-to-move))]

        (testing "when below time-to-move don't toggle pose"
          (let [options {:delta 0}
                new-state (invasion/update original-state options)]
            (is (= :open (:pose new-state)))))

        (testing "when at time-to-move toggle pose"
          (let [options {:delta 1}
                new-state (invasion/update original-state options)]
            (is (= :closed (:pose new-state)))))

        (testing "when crossing time-to move toggle pose"
          (let [options {:delta 2}
                new-state (invasion/update original-state options)]
            (is (= :closed (:pose new-state)))))

        (testing "when closed, toggle the pose to open"
          (let [options {:delta 1}
                closed-state (assoc original-state :pose :closed)
                new-state (invasion/update closed-state options)]
            (is (= :open (:pose new-state)))))

        (testing "only toggle every 'time-to-move'"
          (let [options {:delta 1}
                new-state (-> (invasion/update original-state options)
                              (invasion/update options))]
            (is (= :closed (:pose new-state)))))))

    (testing "move on every update - based on time-to-move"
      (let [time-to-move 1000
            original-state (-> invasion/initial
                               (assoc :since-last-move 999)
                               (assoc :time-to-move time-to-move)
                               (assoc :position {:x 0 :y 0})
                               (assoc :direction :right))]

        (testing "doesn't move before it is time"
          (let [options {:delta 0}
                new-state (invasion/update original-state options)]
            (is (= {:x 0 :y 0} (:position new-state)))))

        (testing "moves right on first time-to-move"
          (let [options {:delta 1 :bounds {:right 100000}}
                new-state (invasion/update original-state options)]
            (is (= {:x invasion/velocity :y 0} (:position new-state)))))

        (testing "moves right crossing time-to-move"
          (let [options {:delta 2 :bounds {:right 100000}}
                new-state (invasion/update original-state options)]
            (is (= {:x invasion/velocity :y 0} (:position new-state)))))

        (testing "can move left"
          (let [options {:delta 1 :bounds {:left -100000 :right 10000}}
                moving-left (assoc original-state :direction :left)
                new-state (invasion/update moving-left options)]
            (is (= {:x (- invasion/velocity) :y 0} (:position new-state)))))

        (testing "moving down"
          (testing "on the right edge"
            (let [right-bounds 100
                  position (- right-bounds invasion/column-width)
                  options {:delta 1 :bounds {:right right-bounds}}
                  state-at-edge (-> original-state
                                    (assoc :direction :right)
                                    (assoc :invaders [[:small]])
                                    (assoc-in [:position :x] position))]

              (testing "move y down when on the right edge, and moving right"
                (let [new-state (-> state-at-edge
                                   (invasion/update options))]
                  (is (= {:x position :y invasion/velocity}
                         (:position new-state)))))

              (testing "move y down when beyond the right edge, and moving right"
                (let [new-state (-> state-at-edge
                                    (assoc-in [:position :x] (inc position))
                                    (invasion/update options))]
                  (is (= {:x (inc position) :y invasion/velocity}
                         (:position new-state)))))

              (testing "after moving down, move to the left"
                (let [new-state (-> state-at-edge
                                    (invasion/update options)
                                    (assoc :since-last-move 999)
                                    (invasion/update options))]
                  (is (= {:x (- position invasion/velocity) :y invasion/velocity}
                         (:position new-state)))))

            (testing "move y down when on the left bounds, and moving left"
              (let [left-bounds 3
                    options {:delta 1 :bounds {:left left-bounds :right 1000}}
                    new-state (-> original-state
                                  (assoc :direction :left)
                                  (assoc-in [:position :x] left-bounds)
                                  (invasion/update options))]
                (is (= {:x left-bounds :y invasion/velocity}
                       (:position new-state)))))

            (testing "move y down when beyond left bounds, and moving left"
              (let [left-bounds 3
                    options {:delta 1 :bounds {:left left-bounds :right 4}}
                    new-state (-> original-state
                                  (assoc :direction :left)
                                  (assoc-in [:position :x] (dec left-bounds))
                                  (invasion/update options))]
                (is (= {:x (dec left-bounds) :y invasion/velocity}
                       (:position new-state)))))))

          (testing "after moving down, move to the right"
            (let [left-bounds 0
                  options {:delta 1 :bounds {:left left-bounds :right 10000}}
                  new-state (-> original-state
                                (assoc :direction :left)
                                (assoc-in [:position :x] left-bounds)
                                (invasion/update options)
                                (assoc :since-last-move 999)
                                (invasion/update options))]
              (is (= {:x invasion/velocity :y invasion/velocity}
                     (:position new-state))))))))))

(dev-cards-runner #"space-invaders.invasion-test")

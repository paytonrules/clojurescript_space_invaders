(ns ^:figwheel-always space-invaders.laser-test
  (:require [cljs.test :refer-macros [is testing]]
            [runners.devcards :refer-macros [dev-cards-runner]]
            [space-invaders.laser :as laser]))

(defn should-be-a-default-laser []
  (is (= {:character :laser :state :default} laser/character)))

(defn should-update-position-based-on-velocity-and-delta []
  (let [right 900
        left 0
        bounds {:left left :right right}
        delta 1
        options {:bounds bounds :delta delta}]
    (testing "does nothing with no velocity"
      (is (= (:position laser/initial)
             (:position (laser/update laser/initial options)))))

    (testing "it moves in the direction of velocity - in x"
      (let [old-position (-> laser/initial
                             (assoc :velocity 5))
            new-position (laser/update old-position options)]
        (is (= (+ 5 (get-in laser/initial [:position :x]))
               (get-in new-position [:position :x])))))

    (testing "it stops at the bounds (accounting for laser width on the right"
      (testing "left bounds"
        (let [old-position (-> laser/initial
                               (assoc :velocity -5)
                               (assoc-in [:position :x] 2))
              new-position (laser/update old-position options)]
          (is (= 0 (get-in new-position [:position :x])))))

      (testing "right bounds"
        (let [old-position (-> laser/initial
                               (assoc :velocity 5)
                               (assoc-in [:position :x]
                                         (- right laser/width 4)))
              new-position (laser/update old-position options)]
          (is (= (- right laser/width)
                 (get-in new-position [:position :x]))))))

    (testing "accounts for delta (velocity is per millisecond"
      (let [old-state (-> laser/initial
                          (assoc :velocity 5))
            different-delta (assoc options :delta 2)
            old-position (get-in old-state [:position :x])
            new-position (get-in (laser/update old-state different-delta) [:position :x])]
        (is (= (+ old-position 10) new-position))))))

(defn should-update-velocity []
  (testing "moving-left"
    (testing "adds negative velocity"
      (is (= (- laser/speed)
             (:velocity (laser/move-left laser/initial)))))

    (testing "accumulates"
      (let [moving-right-laser (assoc laser/initial :velocity laser/speed)]
        (is (= 0 (:velocity (laser/move-left moving-right-laser))))))

    (testing "respects the max-speed"
      (is (= (- laser/speed)
             (:velocity (-> (laser/move-left laser/initial)
                            (laser/move-left)))))))

  (testing "moving-right"
    (testing "adds positive velocity"
      (is (= laser/speed
             (:velocity (laser/move-right laser/initial)))))

    (testing "respects the max speed"
      (is (= laser/speed
             (:velocity (-> (laser/move-right laser/initial)
                            (laser/move-right))))))))

(dev-cards-runner #"space-invaders.laser-test")

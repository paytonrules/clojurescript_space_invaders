(ns ^:figwheel-always space-invaders.view-test
  (:require [space-invaders.view :as view])
  (:require-macros [cljs.test :refer [is testing]]
                   [runners.devcards :refer [dev-cards-runner]]))

(defn test-invaders-to-position []
  (testing "without any tics"
    (testing "no invaders"
      (is (= [] (view/invaders-to-position [[]] 0))))

    (testing "one invader"
      (is (= [{:x view/padding :y view/top}]
             (view/invaders-to-position [[:triangle]] 0))))

    (testing "two invaders in the same row"
      (is (= [{:x view/padding :y view/top}
              {:x (+ (* view/padding 2) view/invader-width) :y 20}]
             (view/invaders-to-position [[:triangle :triangle]] 0))))

    (testing "an invader on the second row"
      (is (= [{:x view/padding :y view/top}
              {:x view/padding :y (+ (* view/top 2) view/invader-height)}]
             (view/invaders-to-position [[:triangle] [:triangle]] 0))))))

(dev-cards-runner #"space-invaders.view-test")

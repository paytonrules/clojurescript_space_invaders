(ns space-invaders.view-test
  (:require [cljs.test :refer-macros [deftest is testing run-tests]]
            [space-invaders.view :as view]))

(deftest test-invaders-to-position-no-invaders
  (is (= [] (view/invaders-to-position [[]] 0))))

(deftest test-invaders-to-position-one-invader-no-tics
  (is (= [{:x view/padding :y view/top}]
         (view/invaders-to-position [[:triangle]] 0))))

(deftest test-two-invaders-in-same-row
  (is (= [{:x view/padding :y view/top}
          {:x (+ (* view/padding 2) view/invader-width) :y 30}]
         (view/invaders-to-position [[:triangle :triangle]] 0))))

(deftest test-an-invader-on-the-second-row
  (is (= [{:x view/padding :y view/top}
          {:x view/padding :y (+ (* view/top 2) view/invader-height)}]
         (view/invaders-to-position [[:triangle] [:triangle]] 0))))

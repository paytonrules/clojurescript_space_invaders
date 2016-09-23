(ns space-invaders.view-test
  (:require [cljs.test :refer-macros [is testing]]
            [devcards.core :refer-macros [deftest]]
            [space-invaders.view :as view]))

(deftest test-invaders-to-position
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

(deftest invader-image
  (testing "convert to open image"
    (is (= "images/small_open.png" (view/image-path :small :open)))
    (is (= "images/small_closed.png" (view/image-path :small :closed)))
    (is (= "images/medium_open.png" (view/image-path :medium :open)))))

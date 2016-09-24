(ns ^:figwheel-always space-invaders.view-test
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

(deftest invader->image-path
  (testing "convert to open image"
    (is (= "images/small_open.png" (view/invader->image-path :small :open)))
    (is (= "images/small_closed.png" (view/invader->image-path :small :closed)))
    (is (= "images/medium_open.png" (view/invader->image-path :medium :open)))))

(deftest image-path->invader-state
  (testing "convert to invader state"
    (is (= [:small :open]
           (view/image-path->invader-state "images/small_open.png")))
    (is (= [:small :closed]
           (view/image-path->invader-state "images/small_closed.png")))
    (is (= [:medium :closed]
           (view/image-path->invader-state "images/medium_closed.png")))))

(deftest make-image-lookup
  (testing "draw no enemies"
    (let [draw-fn #()
          state {:enemies []}])))

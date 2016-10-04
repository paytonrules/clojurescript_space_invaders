(ns ^:figwheel-always space-invaders.presentation-test
  (:require [util.game-loop :as gl]
            [space-invaders.game :as game]
            [space-invaders.presentation :as view])
  (:require-macros [cljs.test :refer [is testing]]
                   [runners.devcards :refer [dev-cards-runner]]))

(defn should-map-invaders-to-images-and-positions []
  (testing "map invders on each row and column"
    (let [small-image (js-obj)
          medium-image (js-obj)
          large-image (js-obj)
          state (gl/->initial-game-state
                  {:ticks 0
                   :invaders ['(:small :medium)
                              '(:large)]
                   :images {:small {:open small-image}
                            :medium {:open medium-image}
                            :large {:open large-image}}})
          images (view/images-with-position state)]

      (testing "image in upper left corner"
        (let [image-with-position (first images)]
          (is (= small-image (:image image-with-position) ))
          (is (= view/padding (:x image-with-position)))
          (is (= view/top (:y image-with-position)))))

      (testing "image to its immediate right"
        (let [image-with-position (second images)]
          (is (= medium-image (:image image-with-position)))
          (is (= (+ view/padding view/invader-width view/padding)
                 (:x image-with-position)))
          (is (= view/top (:y image-with-position)))))

      (testing "image on the second row"
        (let [image-with-position (nth images 2)]
          (is (= large-image (:image image-with-position)))
          (is (= view/padding (:x image-with-position)))
          (is (= (+ view/top view/top view/invader-height)
                 (:y image-with-position))))))))

(defn should-map-invaders-to-closed-state []
  (testing "map invaders closed state"
    (let [small-image (js-obj)
          state (gl/->initial-game-state
                  {:ticks game/velocity
                   :invaders ['(:small)]
                   :images {:small {:closed small-image}}})
          images (view/images-with-position state)]

      (testing "image can be closed"
        (let [image-with-position (first images)]
          (is (= small-image (:image image-with-position))))))))

(dev-cards-runner #"space-invaders.presentation-test")

(ns ^:figwheel-always space-invaders.presentation-test
  (:require [util.game-loop :as gl]
            [space-invaders.game :as game]
            [space-invaders.invasion :as invasion]
            [space-invaders.presentation :as view])
  (:require-macros [cljs.test :refer [is testing]]
                   [runners.devcards :refer [dev-cards-runner]]))

(defn should-map-invaders-to-images-and-positions []
  (testing "map invaders on each row and column"
    (let [small-image (js-obj)
          medium-image (js-obj)
          large-image (js-obj)
          position {:x 5 :y 7}
          state (gl/->initial-game-state
                  {:invasion
                   {:pose :open
                    :position position
                    :invaders ['(:small :medium)
                               '(:large)]}
                   :images {:small {:open small-image}
                            :medium {:open medium-image}
                            :large {:open large-image}}})
          images (view/images-with-position state)]

      (testing "image in upper left corner"
        (let [{:keys [image x y]} (first images)]
          (is (= small-image image))
          (is (= 5 x))
          (is (= 7 y))))

      (testing "image to its immediate right"
        (let [{:keys [image x y]} (second images)]
          (is (= medium-image image))
          (is (= (+ 5 invasion/column-width) x))
          (is (= 7 y))))

      (testing "image on the second row"
        (let [{:keys [image x y]} (nth images 2)]
          (is (= large-image image))
          (is (= 5 x))
          (is (= (+ 7 invasion/row-height) y)))))))

(defn should-map-invaders-to-closed-state []
  (testing "map invaders closed state"
    (let [small-image (js-obj)
          state (gl/->initial-game-state
                  {:invasion
                   {:position {:x 0 :y 0}
                    :pose :closed
                    :invaders ['(:small)]}
                   :images {:small {:closed small-image}}})
          images (view/images-with-position state)]

      (testing "image can be closed"
        (let [image-with-position (first images)]
          (is (= small-image (:image image-with-position))))))))

(dev-cards-runner #"space-invaders.presentation-test")

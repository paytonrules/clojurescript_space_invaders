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
          laser-image (js-obj)
          position {:x 5 :y 7}
          laser-position {:x 30 :y 40}
          state (gl/->initial-game-state
                  {:invasion
                   {:pose :open
                    :position position
                    :invaders [{:character :small :offset {:x 10 :y 10}}
                               {:character :medium :offset {:x 20 :y 10}}
                               {:character :large :offset {:x 10 :y 20}}]}
                   :laser {:position laser-position}
                   :images {:small {:open small-image}
                            :medium {:open medium-image}
                            :large {:open large-image}
                            :laser {:default laser-image}}})
          images (view/images-with-position state)]

      (testing "image in upper left corner"
        (let [{:keys [image position]} (first images)]
          (is (= small-image image))
          (is (= 15 (:x position)))
          (is (= 17 (:y position)))))

      (testing "image to its immediate right"
        (let [{:keys [image position]} (second images)]
          (is (= medium-image image))
          (is (= 25 (:x position)))
          (is (= 17 (:y position)))))

      (testing "image on the second row"
        (let [{:keys [image position]} (nth images 2)]
          (is (= large-image image))
          (is (= 15 (:x position)))
          (is (= 27 (:y position)))))

      (testing "displays the laser image - this is order dependent"
        (let [{:keys [image position]} (nth images 3)]
          (is (= laser-image image))
          (is (= 30 (:x position)))
          (is (= 40 (:y position)))))
     )))


(defn should-map-invaders-to-closed-state []
  (testing "map invaders closed state"
    (let [small-image (js-obj)
          state (gl/->initial-game-state
                  {:invasion
                   {:position {:x 0 :y 0}
                    :pose :closed
                    :invaders [{:character :small :offset {:x 0 :y 0}}]}
                   :images {:small {:closed small-image}}})
          images (view/images-with-position state)]

      (testing "image can be closed"
        (let [image-with-position (first images)]
          (is (= small-image (:image image-with-position))))))))

(dev-cards-runner #"space-invaders.presentation-test")

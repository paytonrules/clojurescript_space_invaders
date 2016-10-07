(ns ^:figwheel-always space-invaders.invaders-test
  (:require [cljs.test :refer-macros [is testing]]
            [runners.devcards :refer-macros [dev-cards-runner]]
            [space-invaders.invaders :as invaders]
            [util.game-loop :as game-loop]))

(defn test-toggle-back-and-forth []
  (testing "the invaders toggle between two states based on the number of tics"
    (is (= :open (invaders/pose 0)))
    (is (= :closed (invaders/pose 1)))))

(dev-cards-runner #"space-invaders.invaders-test")



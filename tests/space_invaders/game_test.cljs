(ns space-invaders.game-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [space-invaders.game :as game]))

(deftest test-update-game
  (testing "increments ticks"
    (is (= 1 (get-in (game/update-game {:ticks 0}) [:state :ticks])))
    (is (= 2 (get-in (game/update-game {:ticks 1}) [:state :ticks])))))

(deftest test-initial-app-state
  (testing (str "each row of invaders is " game/row-length " long")
    (is (every? #(= game/row-length %) (map count (:invaders game/initial-app-state))))))

(deftest test-toggle-back-and-forth
  (testing "the invaders toggle between two states based on the velocity"
    (is (= :open (game/invader-position {:ticks 0})))
    (is (= :closed (game/invader-position {:ticks game/velocity})))))


(ns ^:figwheel-always space-invaders.game-test
  (:require [cljs.test :refer-macros [is testing]]
            [runners.devcards :refer-macros [dev-cards-runner]]
            [space-invaders.game :as game]
            [util.image-loader :as image-loader]))

(defn enemy-images []
  (testing "are created from types and states"
    (is (some #(re-find #"small_closed.png$" %) (game/enemy-images)))
    (is (some #(re-find #"small_open.png$" %) (game/enemy-images)))
    (is (some #(re-find #"medium_open.png$" %) (game/enemy-images)))))

(defn update-game []
  (testing "in :starting"
    (let [load-images (atom nil)
          stub-load-images (fn [param] (reset! load-images param))]
      (with-redefs [image-loader/load-images stub-load-images]
        (testing "loads images"
          (game/update-game {:state :starting})

          (is (= (game/enemy-images) @load-images)))

        (testing "moves to loading-images"
          (is (= :loading-images
                 (:state (game/update-game {:state :starting}))))))))

  (testing "in :playing"
    (testing "increments ticks"
      (is (= 1 (:ticks (game/update-game {:state :playing :ticks 0})))
      (is (= 2 (:ticks (game/update-game {:state :playing :ticks 1}))))))))

(defn test-initial-app-state []
  (testing (str "each row of invaders is " game/row-length " long")
    (is (every? #(= game/row-length %) (map count (:invaders game/initial-app-state))))))

(defn test-toggle-back-and-forth []
  (testing "the invaders toggle between two states based on the velocity"
    (is (= :open (game/invader-position {:ticks 0})))
    (is (= :closed (game/invader-position {:ticks game/velocity})))))

(dev-cards-runner #"space-invaders.game-test")

(ns ^:figwheel-always space-invaders.game-test
  (:require [cljs.test :refer-macros [is testing]]
            [runners.devcards :refer-macros [dev-cards-runner]]
            [space-invaders.game :as game]
            [space-invaders.invasion :as invasion]
            [util.game-loop :as game-loop]
            [util.time :as t]))

(defn initial-enemy-images []
  (testing "are created from types and states"
    (is (some #(re-find #"small_closed.png$" %) (game/enemy-images)))
    (is (some #(re-find #"small_open.png$" %) (game/enemy-images)))
    (is (some #(re-find #"medium_open.png$" %) (game/enemy-images)))))

(defn update-game []

  (testing "first game update"
    (let [new-state (game/update-game (game-loop/->initial-game-state))]
      (testing "begins loading images"
        (is (= :loading-images (get-in new-state [:state :name])))
        (is (= 1 (count (:transitions new-state)))))

      (testing (str "each row of invaders is " invasion/row-length " long")
        (is (every? #(= invasion/row-length %)
                    (map count (:invaders game/initial-app-state)))))))

  (testing "leave the state as is when loading images"
    (let [original-state (game-loop/->initial-game-state {:name :loading-images})
          new-state (game/update-game original-state)]
      (is (= original-state new-state))))

  (testing "images are loaded"
    (let [image-one (js-obj "src" "http://www.example.com/small_open.png")
          image-two (js-obj "src" "http://www.example.com/medium_closed.png")
          event {:name :images-loaded
                 :images [image-one image-two]}
          original-state (game-loop/->initial-game-state {:name :loading-images})]
      (testing "moves the state to playing"
        (is (= :playing
               (get-in (game/update-game original-state event) [:state :name]))))

      (testing "loads all the images in a lookup table"
        (let [new-state (game/update-game original-state event)]
          (is (= image-one (game/image-lookup new-state :small :open)))
          (is (= image-two (game/image-lookup new-state :medium :closed)))))))

  (testing "in :playing"
    (testing "we track time since last move"
      (testing "starting at 0 on the first update"
        (with-redefs [t/epoch (fn [] 1)]
          (let [playing-state (game-loop/->initial-game-state {:name :playing})
                new-state (:state (game/update-game playing-state))]
            (is (= 1 (:last-timestamp new-state)))
            (is (= 0 (:since-last-move new-state))))))

      (testing "updates last timestamp and time since last move"
        (with-redefs [t/epoch (fn [] 2)]
          (let [playing-state (game-loop/->initial-game-state
                                {:name :playing
                                 :last-timestamp 1
                                 :since-last-move 0})
                new-state (:state (game/update-game playing-state))]
            (is (= 2 (:last-timestamp new-state)))
            (is (= 1 (:since-last-move new-state))))))

      (testing "when the since-last-move hits velocity, add a tick"
        (with-redefs [t/epoch (fn [] game/velocity)]
          (let [playing-state (game-loop/->initial-game-state
                                {:name :playing
                                 :last-timestamp 0
                                 :since-last-move 0 })
                new-state (:state (game/update-game playing-state))]
            (is (= 0 (:since-last-move new-state)))
            (is (= 1 (:ticks new-state))))))

      (testing "when the since-last-move is greater than velocity, add a tick"
        (with-redefs [t/epoch (fn [] (inc game/velocity))]
          (let [playing-state (game-loop/->initial-game-state
                                {:name :playing
                                 :last-timestamp 0
                                 :since-last-move 0})
                new-state (:state (game/update-game playing-state))]
            (is (= 0 (:since-last-move new-state)))
            (is (= 1 (:ticks new-state))))))

      (testing "increment ticks beyond 1"
        (with-redefs [t/epoch (fn [] 1)]
          (let [playing-state (game-loop/->initial-game-state
                                {:name :playing
                                 :ticks 1
                                 :since-last-move game/velocity})
                new-state (:state (game/update-game playing-state))]
            (is (= 2 (:ticks new-state)))))))))

(defn invader->image-path []
  (testing "convert to open image"
    (is (= "images/small_open.png" (game/invader->image-path :small :open)))
    (is (= "images/small_closed.png" (game/invader->image-path :small :closed)))
    (is (= "images/medium_open.png" (game/invader->image-path :medium :open)))))

(defn image-path->invader-state []
  (testing "convert to invader state"
    (is (= [:small :open]
           (game/image-path->invader-state "http://example.com/images/small_open.png")))
    (is (= [:small :closed]
           (game/image-path->invader-state "http://example.com/images/small_closed.png")))
    (is (= [:medium :closed]
           (game/image-path->invader-state "http://example.com/images/medium_closed.png")))))

(dev-cards-runner #"space-invaders.game-test")

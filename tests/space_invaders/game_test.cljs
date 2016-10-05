(ns ^:figwheel-always space-invaders.game-test
  (:require [cljs.test :refer-macros [is testing]]
            [runners.devcards :refer-macros [dev-cards-runner]]
            [space-invaders.game :as game]
            [util.game-loop :as game-loop]))

(defn initial-enemy-images []
  (testing "are created from types and states"
    (is (some #(re-find #"small_closed.png$" %) (game/enemy-images)))
    (is (some #(re-find #"small_open.png$" %) (game/enemy-images)))
    (is (some #(re-find #"medium_open.png$" %) (game/enemy-images)))))

(defn update-game []
  (testing "first game update"
    (let [new-state (game/update-game (game-loop/->initial-game-state))]
      (testing "starts with initial-app state"
        (is (= 0 (get-in new-state [:state :ticks])))
        )
      (testing "begins loading images"
        (is (= :loading-images (get-in new-state [:state :name])))
        (is (= 1 (count (:transitions new-state)))))

      (testing (str "each row of invaders is " game/row-length " long")
        (is (every? #(= game/row-length %)
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
    (testing "increments ticks"
      (let [playing-state (game-loop/->initial-game-state {:name :playing})
            with-ticks (assoc-in playing-state [:state :ticks] 5)]
        (is (= 1 (get-in (game/update-game playing-state) [:state :ticks])))
        (is (= 6 (get-in (game/update-game with-ticks) [:state :ticks])))))))

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

(defn test-toggle-back-and-forth []
  (testing "the invaders toggle between two states based on the velocity"
    (is (= :open (game/invader-position (game-loop/->initial-game-state {:ticks 0}))))
    (is (= :closed (game/invader-position (game-loop/->initial-game-state
                                                {:ticks game/velocity}))))))

(defn should-calculate-position []
  (testing "gets x from invader-width, column and padding"
    (is (= game/padding (game/invader-x-position 0)))
    (is (= (+ game/invader-width game/padding game/padding)
           (game/invader-x-position 1))))

  (testing "gets y from top, invader-height and row"
    (is (= game/top (game/invader-y-position 0))))
    (is (= (+ game/top game/top game/invader-height)
           (game/invader-y-position 1))))

(dev-cards-runner #"space-invaders.game-test")

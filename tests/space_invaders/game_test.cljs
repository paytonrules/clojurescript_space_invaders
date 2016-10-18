(ns ^:figwheel-always space-invaders.game-test
  (:require [cljs.test :refer-macros [is testing]]
            [runners.devcards :refer-macros [dev-cards-runner]]
            [space-invaders.game :as game]
            [space-invaders.image-lookup :as image-lookup]
            [space-invaders.invasion :as invasion]
            [space-invaders.laser :as laser]
            [util.game-loop :as game-loop]
            [util.time :as t]))

(defn should-have-the-image-paths []
  (testing "created from types and states"
    (is (some #(re-find #"small_closed.png$" %) (game/all-image-paths)))
    (is (some #(re-find #"small_open.png$" %) (game/all-image-paths)))
    (is (some #(re-find #"medium_open.png$" %) (game/all-image-paths)))
    (is (some #(re-find #"laser_default.png$" %) (game/all-image-paths)))))

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
        (let [{:keys [state]} (game/update-game original-state event)]
          (is (= image-one (image-lookup/->image state :small :open)))
          (is (= image-two (image-lookup/->image state :medium :closed)))))))

  (testing "in :playing"
    (defn- setup-playing-state [& attrs]
      (-> (apply assoc game/initial-app-state :name :playing attrs)
          (game-loop/->initial-game-state)))

    (testing "we track the last timestamp"
      (testing "starting at 0 on the first update"
        (with-redefs [t/epoch (fn [] 1)]
          (let [{:keys [state] } (game/update-game (setup-playing-state))]
            (is (= 1 (:last-timestamp state))))))

      (testing "updates last timestamp"
        (with-redefs [t/epoch (fn [] 2)]
          (let [playing-state (setup-playing-state
                                 :last-timestamp 1
                                 :since-last-move 0)
                {:keys [state]} (game/update-game playing-state)]
            (is (= 2 (:last-timestamp state)))))))

    (testing "update the invasion with 0 as the delta the first update, regardless of epoch"
      (with-redefs [t/epoch (fn [] 10000)]
        (let [{:keys [state]} (game/update-game (setup-playing-state))
              since-last-move (get-in state [:invasion :since-last-move])]
          (is (= 0 since-last-move)))))

    (testing "update the invasion with the delta on subsequent updates"
      (with-redefs [t/epoch (fn [] 2)]
        (let [playing-state (setup-playing-state :last-timestamp 1)
              {:keys [state]} (game/update-game playing-state)
              since-last-move (get-in state [:invasion :since-last-move])]
          (is (= 1 since-last-move)))))

    (testing "uses the bounds when updating the invasion"
      (with-redefs [t/epoch (fn [] 10000)]
        (let [playing-state (-> (setup-playing-state :last-timestamp 1)
                                (assoc-in [:state :invasion :direction] :right)
                                (assoc-in [:state :invasion :position :x] (:right game/bounds)))
              {:keys [state]} (game/update-game playing-state)
              direction (get-in state [:invasion :direction])]
          (is (= :down direction)))))

    (testing "updating laser"
      (with-redefs [t/epoch (fn [] 2)]
        (let [playing-state (-> (setup-playing-state :last-timestamp 1)
                                (assoc-in [:state :laser :velocity] 5))
              {:keys [state]} (game/update-game playing-state)
              position (get-in state [:laser :position :x])
              expected-position (+ 5 (get-in laser/initial [:position :x]))]
          (is (= expected-position position)))))

    (testing "move-left events"
      (let [state (setup-playing-state)
            velocity (-> (game/update-game state {:name :move-left})
                         (get-in [:state :laser :velocity]))]
        (is (= (- laser/speed) velocity))))

    (testing "move-right events"
      (let [state (setup-playing-state)
            velocity (-> (game/update-game state {:name :move-right})
                         (get-in [:state :laser :velocity]))]
        (is (= laser/speed velocity))))))

(dev-cards-runner #"space-invaders.game-test")

(ns ^:figwheel-always space-invaders.transitions-test
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :as ca]
            [cljs.test :refer-macros [async is testing]]
            [util.game-loop :as gl]
            [runners.devcards :refer-macros [dev-cards-runner]]
            [space-invaders.transitions :as transitions]))

(def ^{:private true} images-done-chan (ca/chan))
(def ^{:private true} images-loaded (atom nil))
(defn- fake-image-loader [image-paths]
  (reset! images-loaded image-paths)
  images-done-chan)

(defn loads-images-from-their-paths []
  (let [image-paths ["path/one.png" "path/two.png"]]
    (transitions/load-images! image-paths fake-image-loader)
    (is (= @images-loaded image-paths))))


(defn- check-in-the-future [comparison done]
  (.setTimeout js/window #(do (is (comparison)) (done)) 10))

(defn sends-an-event-when-images-are-loaded []
  (gl/clear-events!)
  (async done
    (let [image-one (js-obj)
          image-two (js-obj)
          expected-event {:name :images-loaded
                          :images [image-one image-two]}]

      (transitions/load-images! ["one" "two"] fake-image-loader)
      (go (ca/>! images-done-chan [image-one image-two]))

      (check-in-the-future #(= expected-event (gl/take-event!)) done))))

(defn should-fire-move-events []
  (testing "moves right on ArrowRight down"
    (gl/clear-events!)
    (transitions/key-down! (js-obj "key" "ArrowRight"))
    (let [expected-event {:name :move-right}]
      (is (= expected-event (gl/take-event!)))))

  (testing "moves left on ArrowLeft down"
    (gl/clear-events!)
    (transitions/key-down! (js-obj "key" "ArrowLeft"))
    (let [expected-event {:name :move-left}]
      (is (= expected-event (gl/take-event!)))))

  (testing "moves left on ArrowRight up"
    (gl/clear-events!)
    (transitions/key-up! (js-obj "key" "ArrowRight"))
    (let [expected-event {:name :move-left}]
      (is (= expected-event (gl/take-event!)))))

  (testing "moves left on ArrowLeft down"
    (gl/clear-events!)
    (transitions/key-up! (js-obj "key" "ArrowLeft"))
    (let [expected-event {:name :move-right}]
      (is (= expected-event (gl/take-event!))))))

(defn should-fire-bullets-on-spacebar []
  (gl/clear-events!)
  (transitions/key-down! (js-obj "key" " "))
  (let [expected-event {:name :fire}]
    (is (= expected-event (gl/take-event!)))))

(dev-cards-runner #"space-invaders.transitions-test")

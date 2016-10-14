(ns ^:figwheel-always space-invaders.image-lookup-test
  (:require [cljs.test :refer-macros [is testing]]
            [runners.devcards :refer-macros [dev-cards-runner]]
            [space-invaders.image-lookup :as lookup]))

(defn should-convert-characters-to-images []
  (testing "convert to open image"
    (is (= "images/small_open.png" (lookup/->image-path :small :open)))
    (is (= "images/small_closed.png" (lookup/->image-path :small :closed)))
    (is (= "images/medium_open.png" (lookup/->image-path :medium :open)))))

(defn image-path->invader-state []
  (testing "convert to invader state"
    (is (= [:small :open]
           (lookup/image-path->character-state "http://example.com/images/small_open.png")))
    (is (= [:small :closed]
           (lookup/image-path->character-state "http://example.com/images/small_closed.png")))
    (is (= [:medium :closed]
           (lookup/image-path->character-state "http://example.com/images/medium_closed.png")))))

(dev-cards-runner #"space-invaders.image-lookup-test")

(ns ^:figwheel-always space-invaders.bullet-test
  (:require [cljs.test :refer-macros [is testing]]
            [runners.devcards :refer-macros [dev-cards-runner]]
            [space-invaders.bullet :as bullet]))

(defn should-be-created []
  (testing "create a bullet"
    (is (= {:position {:x 30 :y 30}}
           (bullet/create {:x 30 :y 30})))))

(defn should-move-up-the-screen []
  (testing "update moves up"
    (let [bullet (bullet/create {:x 0 :y 0})
          next-position (bullet/update bullet 1)]
      (is (= bullet/velocity (get-in next-position [:position :y])))))

  (testing "update uses the delta"
    (let [bullet (bullet/create {:x 0 :y 0})
          next-position (bullet/update bullet 2)]
      (is (= (* 2 bullet/velocity) (get-in next-position [:position :y]))))))

(dev-cards-runner #"space-invaders.bullet-test")

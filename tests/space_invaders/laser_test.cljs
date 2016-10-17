(ns ^:figwheel-always space-invaders.laser-test
  (:require [cljs.test :refer-macros [is testing]]
            [runners.devcards :refer-macros [dev-cards-runner]]
            [space-invaders.laser :as laser]))

(defn should-be-a-default-laser []
  (is (= {:character :laser :state :default} laser/character)))


(dev-cards-runner #"space-invaders.laser-test")
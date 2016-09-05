(ns ^:figwheel-always runners.browser-test
  (:require [cljs.test :refer-macros [run-all-tests]]
            [runners.tests]))

(enable-console-print!)

(run-all-tests #"space-invaders\..*-test")

(ns runners.doo-test
  (:require [doo.runner :refer-macros [doo-all-tests]]
            [runners.tests]))

(doo-all-tests #"(space-invaders|util)\..*-test")

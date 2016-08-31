(ns space-invaders.test
  (:require [doo.runner :refer-macros [doo-tests]]
            [space-invaders.core-test]))

(enable-console-print!)

(doo-tests 'space-invaders.core-test)

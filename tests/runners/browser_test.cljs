(ns runners.browser-test
  (:require [runners.tests])
  (:require-macros [runners.whatever-test :as wt :refer [dev-cards-runner]]))

(enable-console-print!)
(dev-cards-runner #"(util|space-invaders)\..*-test")

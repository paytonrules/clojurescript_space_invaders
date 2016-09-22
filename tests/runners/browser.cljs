(ns runners.browser
  (:require [runners.tests])
  (:require-macros [runners.devcards :refer [dev-cards-runner]]))

(enable-console-print!)
(dev-cards-runner #"(util|space-invaders)\..*-test")

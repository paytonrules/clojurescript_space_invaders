(ns util.image-loader
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [cljs.core.async :as a]))

(defn load-images! [& filenames]
  (let [chan (a/chan)]
    (go
      (a/>! chan "joy"))))



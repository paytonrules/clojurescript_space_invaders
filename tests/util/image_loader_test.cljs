(ns util.image-loader-test
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :as a]
            [cljs.test :refer-macros [is testing async]]
            [devcards.core :refer-macros [deftest]]
            [util.image-loader :as image-loader]))

(deftest load-images
  (async done
         (let [chan (image-loader/load-images!)]
           (go
             (if (a/<! chan)
               (done))))))

               ;(is (true? false))
               ;(is false "error pulling from channel"))
             ;(done))
           ;(throw "aw poop"))))

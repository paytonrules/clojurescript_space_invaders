(ns util.image-loader
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [cljs.core.async :as a]))

(defn create-empty-image []
  (js/Image.))

(defn load-image
  ([src] (load-image src create-empty-image))
  ([src create-image]
   (let [c (a/chan)
         image (create-image)]
     (set! (.-src image) src)
     (set! (.-onload image) #(go (a/>! c image)))
     c)))

(defn load-images
  ([image-paths] (load-images image-paths create-empty-image))
  ([image-paths create-image]
   (let [complete (cljs.core.async/chan)
         loading-images (mapv #(load-image % create-image) image-paths)]
     (if (empty? loading-images)
       (go (cljs.core.async/>! complete []))
       (go-loop [images []]
         (let [[loaded-image _] (a/alts! loading-images)
               loaded-images (conj images loaded-image)]
           (if (= (count image-paths) (count loaded-images))
             (cljs.core.async/>! complete loaded-images)
             (recur loaded-images)))))
     complete)))

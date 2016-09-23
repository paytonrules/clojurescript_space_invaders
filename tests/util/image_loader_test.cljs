(ns util.image-loader-test
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :as a]
            [cljs.test :refer-macros [is testing async]]
            [devcards.core :refer-macros [deftest]]
            [util.image-loader :as image-loader]))

(defn create-known-images [images]
  (let [image (last @images)]
    (swap! images pop)
    image))

(defn setup-created-images [image-list]
  (partial create-known-images (atom image-list)))

(deftest load-image
  (testing "created image is returned on a channel"
    (let [fake-image (js-obj)
          create-image (setup-created-images [fake-image])]
      (async done
        (let [chan (image-loader/load-image "source" create-image)]
          (go
            (when-let [image (a/<! chan)]
              (is (re-find #"source$" (.-src image))))
              (done))

          (.onload fake-image))))))

(deftest load-images
  (testing "completes with no images"
    (async done
      (let [chan (image-loader/load-images '())]
        (go
          (when-let [images (a/<! chan)]
            (is (= [] images) "no images should be loaded")
            (done))))))

  (testing "completes with one image"
    (async done
      (let [fake-image (js-obj)
            create-image (setup-created-images [fake-image])
            chan (image-loader/load-images '("first") create-image)]
        (go
          (when-let [images (a/<! chan)]
            (is (= 1 (count images)))
            (is (= "first" (.-src (first images))))
            (done)))

        (.onload fake-image))))

  (testing "completes with many images"
    (let [fake-image-first (js-obj)
          fake-image-second (js-obj)
          create-image (setup-created-images [fake-image-second fake-image-first])]
      (async done
        (let [chan (image-loader/load-images '("first" "second") create-image)]
          (go
            (when-let [images (a/<! chan)]
              (is (= 2 (count images)))
              (is (= "first" (.-src (first images))))
              (is (= "second" (.-src (second images))))
              (done)))

          (.onload fake-image-first)
          (.onload fake-image-second))))))

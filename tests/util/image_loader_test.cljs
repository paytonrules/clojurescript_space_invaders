(ns util.image-loader-test
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :as a]
            [cljs.test :refer-macros [is testing async]]
            [runners.devcards :refer-macros [dev-cards-runner]]
            [util.image-loader :as image-loader]))

(defn- create-known-images [images]
  (let [image (last @images)]
    (swap! images pop)
    image))

(defn- setup-created-images [image-list]
  (partial create-known-images (atom image-list)))

(defn load-image []
  (testing "created image is returned on a channel"
    (let [fake-image (js-obj)
          create-image (setup-created-images [fake-image])]
      (async done
        (let [chan (image-loader/load-image "source" create-image)]
          (.onload fake-image)

          (a/take! chan (fn [image]
                          (is (re-find #"source$" (.-src image)))
                          (done))))))))

(defn load-images-no-images []
  (testing "completes with no images"
    (async done
      (let [chan (image-loader/load-images '())]
        (a/take! chan (fn [images]
                        (is (= [] images) "no images should be loaded")
                        (done)))))))

(defn load-images-one-image []
  (testing "completes with one image"
    (async done
      (let [fake-image (js-obj)
            create-image (setup-created-images [fake-image])
            chan (image-loader/load-images '("first") create-image)]
        (.onload fake-image)

        (a/take! chan (fn [images]
                        (is (= 1 (count images)))
                        (is (= "first" (.-src (first images))))
                        (done)))))))

(defn load-images-two-images []
  (testing "completes with many images"
    (async done
           (let [fake-image-first (js-obj)
                 fake-image-second (js-obj)
                 create-image (setup-created-images [fake-image-second fake-image-first])
                 chan (image-loader/load-images '("first" "second") create-image)]
             (.onload fake-image-first)
             (.onload fake-image-second)

             (a/take! chan (fn [images]
                             (is (= 2 (count images)))
                             (is (= "first" (.-src (first images))))
                             (is (= "second" (.-src (second images))))
                             (done)))))))

(dev-cards-runner #"util.image-loader-test")

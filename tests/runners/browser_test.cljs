(ns ^:figwheel-always runners.browser-test
  (:require [cljs.test :refer-macros [run-all-tests]]
            [reagent.core :as r]
            [runners.tests]))

; This is taken straight out of cljs.test source to get a list of the test names.
; As such it's pretty unstable.
; I don't really know why there would ever be more than one test name
(defn- current-test-names []
  (reverse (map #(:name (meta %))
                (:testing-vars (cljs.test/get-current-env)))))

(def results
  (r/atom {:failures '()
           :successes '()}))

(defn success? []
  (= 0 (count (:failures @results))))

(defn result-block []
  [:div {:id "summary" :class (if (success?) "success" "failure")}
    [:p (str (count (:successes @results))
             " ran successfully, "
             (count (:failures @results))
             " failed.")]])

(defn result-view []
  [:div
   (for [failure (:failures @results)]
     ^{:key (:test-names failure)}
     [:div {:class "failure"}
      [:p
       [:span "Test(s): "] (clojure.string/join "," (:test-names failure))]
      [:p
       [:span "Expected: "] (str (:expected failure))]
      [:p
       [:span "Actual: "] (str (:actual failure))]
      [:p
       [:span "Filename: " ] (:file failure)]])
   (for [success (:successes @results)]
     ^{:key (:test-names success)}
     [:div {:class "success"}
      [:p
       [:span "Test(s): "] (clojure.string/join "," (:test-names success))]])
   [result-block]])

(r/render [result-view]
          (js/document.getElementById "results"))

(defn- add-result [k m]
  (let [message-with-test-name (assoc m :test-names (current-test-names))]
    (as-> (k @results) _
      (conj _ message-with-test-name)
      (assoc @results k _)
      (reset! results _))))

(defmethod cljs.test/report [:cljs.test/default :fail] [m]
  (js/console.log m)
  (add-result :failures m))

(defmethod cljs.test/report [:cljs.test/default :pass] [m]
  (add-result :successes m))

; Probably only needs to exist once
(enable-console-print!)

(cljs.test/run-all-tests #"space-invaders\..*-test")

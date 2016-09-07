(ns ^:figwheel-always runners.browser-test
  (:require [cljs.test :refer-macros [run-all-tests]]
            [reagent.core :as r]
            [runners.tests]))

; MODEL - sure why not?
; Result stucture is:
; Hash of
; :"test-name" {
;               :file
;               :line
;               :contexts {
;                 "context" {
;                   :successes '({:expected :actual :message})
;                   :failures '({:expected :actual :message})
;                 }
;              }
;
; Don't defonce this - you want to reset on each reload
(def total-results (r/atom {}))

(defn update-results [results]
  (reset! total-results results))

(defn add-result [results result {:keys [name file line context expected actual message]}]
  (as-> (get-in results [name :contexts context result] '()) _
        (conj _ {:expected expected :actual actual :message message})
        (assoc-in results [name :contexts context result] _)
        (assoc-in _ [name :file] file)
        (assoc-in _ [name :line] line)))

(defn total-for-test [test-results result-code]
  (->> (:contexts test-results)
       (vals)
       (map result-code)
       (map count)
       (reduce +)))

(defn total [result-code]
  (->> (vals @total-results)
       (map #(total-for-test % result-code))
       (reduce +)))

(defn successful-test? [test-result]
  (= 0 (+
        (total-for-test test-result :failure)
        (total-for-test test-result :error))))

(defn failed-test? [test-result]
  (not (successful-test? test-result)))

(defn success? []
  (= 0 (+ (total :failures) (total :errors))))

; EVENTS
(defn- update-new-result [result-code m]
  "This takes care of handling the report of errror messages, assuming one fail for every 'm'. The variable name 'm' is chosen because it matches what cljs.test uses internally.

  m contains the :expected, :actual and error messagse if present
  The file and line number come from the (:testing-vars (cljs.test/get-current-env))  - note the mapping of metadata below. Finally the context comes from (:testing-contexts of (cljs.test/get-current-env).

  Interestingly the :testing-vars report a list of tests, but I haven't found a way this will happen. Just the same if more than one tests are reported in the testing-vars, then the failure will appear twice with different names."
  (->> (:testing-vars (cljs.test/get-current-env))
       (map #(select-keys (meta %) [:name :file :line]))
       (map #(merge % (select-keys m [:expected :actual :message])))
       (map #(assoc % :context (clojure.string/join ", " (:testing-contexts (cljs.test/get-current-env)))))
       (reduce (fn [results result] (add-result results result-code result)) @total-results)

       (update-results)))

(defmethod cljs.test/report [:cljs.test/default :fail] [m]
  (update-new-result :failures m))

(defmethod cljs.test/report [:cljs.test/default :pass] [m]
  (update-new-result :successes m))

(defmethod cljs.test/report [:cljs.test/default :error] [m]
  (update-new-result :errors m))

; VIEW
(defn result-block []
  [:div {:id "summary" :class (if (success?) "success" "failure")}
   [:p (str (total :successes)
            " ran successfully, "
            (total :failures)
            " failed, and "
            (total :errors)
            " errored")]])

(defn test-details [context-results result-code]
  [:div
   (for [result (result-code context-results)]
     ^{:key result}
     [:div
       [:p
        [:span "Expected: "] (str (:expected result))]
       [:p
        [:span "Actual: "] (str (:actual result))]
       (when-not (empty? (:message result))
         [:p
          [:span "Message: "] (str (:message result))])])])

(defn result-view []
  [:div
   [result-block]
   (for [[test-name test-results] @total-results]
     ^{:key test-name}
     [:div {:class (if (failed-test? test-results) "failure" "success")}
       [:p
         [:span "Test: "] test-name]
       [:p
         [:span "File: "] (:file test-results)]
       [:p
         [:span "Line No: "] (:line test-results)]
       (for [[context-name context-results] (:contexts test-results)]
         ^{:key context-name}
         [:div
           [:p
             [:span "Context: "] context-name]
             [test-details context-results :failures]
             [test-details context-results :errors]
          ])])])

(r/render [result-view]
          (js/document.getElementById "results"))

; Probably only needs to exist once
(enable-console-print!)

(cljs.test/run-all-tests #"space-invaders\..*-test")

(ns runners.browser-test
  (:require [cljs.test :as ct]
            [devcards.core]
            [devcards.system :as ds]
            [reagent.core :as reagent]
            [runners.tests])
  (:require-macros [cljs.test :as ct :refer [run-all-tests]]
                   [devcards.core :as dc :refer [defcard defcard-rg]]))

(enable-console-print!)

(defonce results (reagent/atom {:fail-count 0
                                :error-count 0
                                :pass-count 0
                                :test-count 0
                                :failures []
                                :errors []}))

(defn current-tests []
  (map #(select-keys (meta %) [:name :ns]) (:testing-vars (ct/get-current-env))))

(defmethod ct/report [:cljs.test/default :fail] [m]
  (ct/inc-report-counter! :fail)

  (->> {:tests (current-tests)
        :message (:message m)}
       (conj (:failures @results))
       (swap! results assoc :failures)))

(defmethod cljs.test/report [:cljs.test/default :error] [m]
  (ct/inc-report-counter! :error)
 	(as-> {:failure (str "ERROR in" (ct/testing-vars-str m))
         :message (:message m)} _
      (conj (:errors @results) _)
      (assoc @results :errors _)
      (reset! results _))
 (throw (:actual m)))

(defmethod ct/report [:cljs.test/default :end-run-tests] [m]
  (swap! results assoc :fail-count (:fail m)
                       :error-count (:error m)
                       :test-count (:test m)
                       :pass-count (:pass m)))

(defn result-summary [_]
  [:div
   [:h1 "Summary"]
   [:p (str "Tests " (:test-count @results))]
   [:p (str "Passing " (:pass-count @results))]
   [:p (str "Failing " (:fail-count @results))]
   [:p (str "Errors " (:error-count @results))]
   (when-not (empty? (:failures @results))
     [:div {:class "failure"}
      [:h2 "Failures"]
      [:ul
       (for [failure (:failures @results)]
         ^{:key failure}
         [:li
          (for [t (:tests failure)]
            ^{:key t} [:p (str (:ns t) "/" (:name t))])
          [:p (:message failure)]])]])])

(dc/defcard-rg full-results
  [result-summary])

(defn clear-results []
  (reset! results {:failures []
                   :errors []
                   :test-count 0
                   :pass-count 0
                   :error-count 0
                   :fail-count 0}))

(defn run-tests []
  (clear-results)
  (js/setTimeout
    #(ct/run-all-tests #"(util|space-invaders)\..*-test") 500))

(run-tests)

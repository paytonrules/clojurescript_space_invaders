(ns runners.browser-test
  (:require [cljs.env]
            [cljs.analyzer :as ana]
            [cljs.analyzer.api :as ana-api]
            [cljs.test :as ct]
            [devcards.core :as dc]
            [reagent.core :as reagent]
            [runners.tests])
  (:require-macros [cljs.test :as ct :refer [run-all-tests]]
                   [devcards.core :as dc :refer [defcard defcard-rg]]
                   [runners.whatever-test :as wt :refer [my-bs-macro]]))

(enable-console-print!)
(my-bs-macro)

(defonce results (reagent/atom {}))

(defn current-tests []
  (map #(select-keys (meta %) [:name :ns])
       (:testing-vars (ct/get-current-env))))

(defmethod ct/report [:cljs.test/default :begin-test-ns] [m]
  (swap! results assoc :testing (name (:ns m))))

(defmethod ct/report [:cljs.test/default :fail] [m]
  (ct/inc-report-counter! :fail)

  (->> {:tests (current-tests)
        :message (:message m)}
       (conj (:failures @results))
       (swap! results assoc :failures)))

(defmethod ct/report [:cljs.test/default :error] [m]
  (ct/inc-report-counter! :error)
  (->> {:tests (current-tests)
        :message (:message m)}
       (conj (:errors @results))
       (swap! results assoc :errors))
  (throw (:actual m)))

(defmethod ct/report [:cljs.test/default :end-run-tests] [m]
  (swap! results assoc :testing ""
                       :fail-count (:fail m)
                       :error-count (:error m)
                       :test-count (:test m)
                       :pass-count (:pass m)))

(defn list-failures [failures heading]
  [:div {:class heading}
   [:h2 heading]
   [:ul
    (for [failure failures]
      ^{:key failure}
      [:li
       (for [t (:tests failure)]
         ^{:key t} [:p (str (:ns t) "/" (:name t))])
       [:p (:message failure)]])]])

(defn result-summary [_]
  (if (empty? (:testing @results))
    [:div
     [:h1 "Summary"]
     [:p (str "Tests " (:test-count @results))]
     [:p (str "Passing " (:pass-count @results))]
     [:p (str "Failing " (:fail-count @results))]
     [:p (str "Errors " (:error-count @results))]
     (when-not (empty? (:failures @results))
       [list-failures (:failures @results) "Failures"])
     (when-not (empty? (:errors @results))
       [list-failures (:errros @results) "Errors"])]
    [:div
     [:p (str "Running Test Namespace " (:testing @results))]]))

(defn clear-results []
  (reset! results {:failures []
                   :errors []
                   :testing ""
                   :test-count 0
                   :pass-count 0
                   :error-count 1
                   :fail-count 0}))

(dc/defcard-rg full-results
  [result-summary])

(dc/deftest all-tests
  "fuck")

;(ana-api/empty-env)

;  (prn (clojure.repl/dir space-invaders.game-test)))
  ;(space-invaders.game-test/test-update-game)
  ;(space-invaders.game-test/test-initial-app-state)
  ;(space-invaders.game-test/test-toggle-back-and-forth))

;  (quote space-invaders.game-test/test-update-game))
;
(defn list-test-namespaces []
  (prn "keys" (.keys (js->clj (.-space_invaders (js/Object js/window))))))
;  (prn (js->clj (.-space_invaders (js/Object js/window)))))


(list-test-namespaces)

(defn run-tests []
  (clear-results)
  (js/setTimeout
    #(ct/run-all-tests #"(util|space-invaders)\..*-test") 500))

(run-tests)

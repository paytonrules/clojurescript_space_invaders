(defproject space-invaders "0.1.0-SNAPSHOT"
	:dependencies [[lein-doo "0.1.7"]
                 [devcards "0.2.1-7"]
                 [org.clojure/clojure "1.8.0"]
                 [org.clojure/core.async "0.2.391"]
								 [org.clojure/clojurescript "1.9.227"]
                 [reagent "0.6.0-rc"]]
	:plugins [[lein-cljsbuild "1.1.4" :exclusions [[org.clojure/clojure]]]
            [lein-doo "0.1.7"]
            [lein-figwheel "0.5.6"]]
	:clean-targets ^{:protect false} [:target-path "out" "resources/public/cljs"]
  :source-paths ["src" "tests"]
	:cljsbuild {
    :builds [{:id "dev"
              :source-paths ["src"]
              :figwheel true
              :compiler {:main space_invaders.core
                         :asset-path "cljs/out"
                         :output-to "resources/public/cljs/main.js"
                         :output-dir "resources/public/cljs/out"
                         :source-map true}
              },
             {:id "test"
              :source-paths ["src" "tests"]
              :compiler {:main runners.doo_test
                         :optimizations :none
                         :output-to "resources/public/cljs/tests/all-tests.js"}},
             {:id "figwheel-test"
              :source-paths ["src" "tests"]
              :figwheel {:devcards true}
              :compiler {:main runners.browser_test
                         :optimizations :none
                         :asset-path "cljs/tests/out"
                         :output-dir "resources/public/cljs/tests/out"
                         :output-to "resources/public/cljs/tests/all-tests.js"
							           :source-map-timestamp true}}
             ]
    :test-commands {"test" ["phantomjs"
                            "resources/tests/test.js"
                            "resources/tests/test.html"]}}
  :figwheel {
    :css-dirs ["resources/public/css"]
  })

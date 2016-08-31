(defproject space-invaders "0.1.0-SNAPSHOT"
	:dependencies [[org.clojure/clojure "1.8.0"]
								 [org.clojure/clojurescript "1.9.227"]]
	:plugins [[lein-cljsbuild "1.1.4" :exclusions [[org.clojure/clojure]]]
            [lein-doo "0.1.7"]
            [lein-figwheel "0.5.6"]]
	:clean-targets ^{:protect false} [:target-path "out" "resources/public/cljs"]
	:cljsbuild {
    :builds [{:id "dev"
              :source-paths ["src"]
              :figwheel true
              :compiler {:main "space-invaders.core"
                         :asset-path "cljs/out"
                         :output-to "resources/public/cljs/main.js"
                         :output-dir "resources/public/cljs/out"}
              },
             {:id "test"
              :source-paths ["src" "tests"]
              :compiler {:main space_invaders.test
                         :output-to "resources/tests/all-tests.js"
                         :optimizations :none}}]
    :test-commands {"test" ["phantomjs"
                            "resources/tests/test.js"
                            "resources/tests/test.html"]}}
  :figwheel {
    :css-dirs ["resources/public/css"]
  })

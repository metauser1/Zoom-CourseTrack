(def project 'udelcoursetrack)
(def version "0.1.0-SNAPSHOT")

(set-env! :resource-paths #{"resources" "src"}
          :source-paths   #{"test"}
          :dependencies   '[[adzerk/boot-test "RELEASE" :scope "test"]
                            [org.clojure/clojure "1.8.0"]
                            [enlive "1.1.6"]
                            [http-kit "2.1.18"]
                            [com.draines/postal "2.0.2"]])

(task-options!
 aot {:namespace   #{'udelcoursetrack.core}}
 pom {:project     project
      :version     version
      :description "FIXME: write description"
      :url         "http://example/FIXME"
      :scm         {:url "https://github.com/yourname/udelcoursetrack"}
      :license     {"Eclipse Public License"
                    "http://www.eclipse.org/legal/epl-v10.html"}}
 jar {:main        'udelcoursetrack.core
      :file        (str "udelcoursetrack-" version "-standalone.jar")})

(deftask build
  "Build the project locally as a JAR."
  [d dir PATH #{str} "the set of directories to write to (target)."]
  (let [dir (if (seq dir) dir #{"target"})]
    (comp (aot) (pom) (uber) (jar) (target :dir dir))))

(deftask run
  "Run the project."
  [a args ARG [str] "the arguments for the application."]
  (require '[udelcoursetrack.core :as app])
  (apply (resolve 'app/-main) args))

(require '[adzerk.boot-test :refer [test]])

(require 'boot.repl)

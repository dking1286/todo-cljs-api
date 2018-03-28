(defproject todo-cljs-api "0.1.0-SNAPSHOT"
  :description "The API server for the world's most overbuilt todo application"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.9.0"]
                 [environ "1.1.0"]
                 [ring "1.6.3"]
                 [ring-logger "0.7.7"]
                 [compojure "1.6.0"]
                 [com.cognitect/transit-clj "0.8.300"]
                 [buddy/buddy-core "1.4.0"]
                 [buddy/buddy-auth "2.1.0"]
                 [buddy/buddy-hashers "1.3.0"]
                 [org.postgresql/postgresql "9.4.1212.jre7"]
                 [org.clojure/java.jdbc "0.7.5"]
                 [honeysql "0.9.1"]
                 [ragtime "0.7.2"]
                 [cheshire "5.8.0"]] ; Needed to resolve a dependency issue

  :plugins [[lein-environ "1.1.0"]
            [lein-ring "0.12.3"]]

  :source-paths ["src"]
  :test-paths ["test"]
  :target-path "target/%s"
  :clean-targets ^{:protect false} [:target-path]
  :repl-options {:port 3001}
  :uberjar-name "todo-cljs-api-standalone.jar"

  :ring {:handler todo-cljs-api.core/app}

  :profiles
  {:dev {:env {:environment "development"}
         :dependencies [[cljfmt "0.5.7"]]}

   :test {:env {:environment "test"}
          :dependencies [[pjstadig/humane-test-output "0.8.3"]]
          :plugins [[com.jakemccrary/lein-test-refresh "0.12.0"]]
          :injections [(require 'pjstadig.humane-test-output)
                       (pjstadig.humane-test-output/activate!)]
          :test-refresh {:quiet true
                         :changes-only true
                         :watch-dirs ["src" "test"]}}

   :prod {:env {:environment "production"}}}

  :aliases
  {"repl:dev" ["do" "clean" ["with-profile" "+dev,+local-dev" "repl"]]
   "run:dev" ["with-profile" "+dev,+local-dev" "ring" "server-headless"]
   "test:watch" ["with-profile" "+test,+local-test" "test-refresh"]
   "test:once" ["with-profile" "+test,+local-test" "test" ":all"]
   "build:prod" ["do" "clean" ["with-profile" "prod" "ring" "uberjar"]]})
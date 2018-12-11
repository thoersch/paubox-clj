(defproject paubox-clj "0.1.0-SNAPSHOT"
  :description "JVM Based PauBox SDK"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/data.json "0.1.2"]
                 [clj-http "0.3.5"]]
  :main paubox-clj.core
  :pom-addition [:developers [:developer
                              [:name "Tyler Hoersch"]
                              [:url "http://github.com/thoersch"]
                              [:email "thoersch@gmail.com"]
                              [:timezone "-5"]]])

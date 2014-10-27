(defproject etl "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [com.netflix.pigpen/pigpen "0.2.11"]
                 [clojure-csv/clojure-csv "2.0.1"]]
  :profiles {:dev {:dependencies [[org.apache.pig/pig "0.11.1"]
                                  [org.apache.hadoop/hadoop-core "1.1.2"]]}})

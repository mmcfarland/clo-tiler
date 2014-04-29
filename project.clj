(defproject clo-tiler "0.1.0-SNAPSHOT"
  :description "Simple map tiler"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"],
                 [org.clojure/math.numeric-tower "0.0.4"]
                 [org.clojure/java.jdbc "0.3.2"]
                 [postgresql "9.1-901.jdbc4"]
                 [http-kit "2.1.18"]
                 [ring "1.2.1"]
                 [compojure "1.1.6"]
                 [com.vividsolutions/jts "1.11"]]
  :main clo-tiler.core)

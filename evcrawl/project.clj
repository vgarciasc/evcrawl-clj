(defproject evcrawl "1.0.0"
  :description "A mini-crawler for the brazilian website \"Estante Virtual\"."
  :url "http://github.com/vgarciasc/estante-virtual-minicrawler"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [enlive "1.1.6"]
                 [clj-http "3.10.0"]
                 [cheshire "5.10.0"]
                 [compojure "1.6.1"]
                 [http-kit "2.3.0"]
                 [ring/ring-defaults "0.3.2"]
                 [ring/ring-json "0.5.0"]
                 [ring-cors "0.1.13"]
                 [org.clojure/data.json "0.2.6"]]
  :repl-options {:init-ns evcrawl.encrawl}
  :main evcrawl.encrawl
  :aot [evcrawl.encrawl])

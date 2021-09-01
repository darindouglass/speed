(defproject speed "0.1.0"
  :description "A crux-based speedrun leaderboard"
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [com.walmartlabs/lacinia "0.39-alpha-9"]
                 [functionalbytes/redelay "1.1.0"]
                 [metosin/malli "0.5.1"]
                 [pro.juxt.crux/crux-core "1.18.0"]]
  :profiles
  {:dev {:dependencies [[org.clojure/test.check "0.10.0"]]}})

(ns speed.models.runner
  (:require [clojure.spec.alpha :as s]
            [speed.db :as db]
            [speed.specs :as specs]))

(defn retrive-all
  "Returns all runners"
  []
  (db/query
   '{:find [r]
     :where [r :runnder/handle]}))

(defn create!
  [runner]
  (when-not (s/valid? ::specs/runner-manifest runner)
    (throw (ex-info "invalid runner" {:runner runner})))
  (db/add-manifest! runner))

(ns speed.schema
  "Contains custom resolvers and a function to provide the full schema."
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [com.walmartlabs.lacinia.schema :as schema]
            [com.walmartlabs.lacinia.util :as util]))

(defn resolver-map
  []
  {:query/game-by-id (constantly nil)})

(defn load-schema
  []
  (-> (io/resource "schema.edn")
      (slurp)
      (edn/read-string)
      (util/attach-resolvers (resolver-map))
      (schema/compile)))

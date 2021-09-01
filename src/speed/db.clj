(ns speed.db
  (:require [crux.api :as crux]
            [redelay.core :refer [defstate] :as redelay]))

(defstate node
  :start (crux/start-node {})
  :stop (.close this))

(defn query
  [query]
  (crux/q (crux/db @node) query))

(defn add-manifest!
  [manifest]
  (crux/submit-tx @node [[:crux.tx/put manifest]]))

(comment
  (add-game! {:crux.db/id :zelda
              :foo "bar"})
  (crux/entity (crux/db @node) :zelda)
  )

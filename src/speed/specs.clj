(ns speed.specs
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.string :as str]
            [malli.core :as m])
  (:import [java.time Instant]))

(def non-empty-string?
  (s/and string? (complement str/blank?)))
;; (defn non-empty-string?
;;   [x]
;;   (and (string? x)
;;        (not (str/blank? x))))

;; inst? maps to java.util.Date, which is no bueno
(def instant?
  (let [->Instant #(Instant/ofEpochMilli %)]
    (s/with-gen #(instance? Instant %)
      #(gen/fmap ->Instant (gen/large-integer)))))

(def platforms #{:wii :wii-vc})

(s/def ::db-id keyword?)

(s/def :game/id ::db-id)
(s/def :game/title non-empty-string?)
(s/def :game/publication-year pos-int?)
(s/def :game/platform platforms)
(s/def :game/platforms (s/coll-of :game/platform
                                  :kind set?
                                  :min-count 1
                                  :distinct true))

(s/def :runner/id ::db-id)
(s/def :runner/handle non-empty-string?)
(s/def :runner/country keyword?)

(s/def :run/time-ms integer?)
(s/def :run/timestamp instant?)

(s/def :game/manifest (s/keys :req [:game/title :game/publication-year :game/platforms]))
(s/def :runner/manifest (s/keys :req [:runner/handle :runner/country]))
(s/def :run/manifest ^:test (s/keys :req [:game/id :game/platform
                                          :runner/id
                                          :run/time-ms :run/timestamp]))

(ns speed.api.resource
  (:require [clojure.set :as set]
            [liberator.core :as liberator]
            [muuntaja.core :as m]
            [reitit.ring :as ring]))

(defn deep-merge
  "lol"
  [left right]
  (cond
    (map? left)
    (reduce-kv (fn [acc key value]
                 (assoc acc key (deep-merge (get left value) value)))
               left
               right)

    (vector? left)
    (mapv deep-merge left right)

    :else (or left right)))

(defn- ns-keyword
  "Namespaces `symbol` with `*ns*`."
  [symbol]
  (keyword (name (ns-name *ns*)) (name symbol)))

(defn- assoc-handler
  "Adds `var` as the `:handler` for all http methods in `opts`."
  [var opts]
  (reduce (fn [acc method]
            (cond-> acc
              (contains? acc method)
              (assoc-in [method :handler] var)))
          opts
          ring/http-methods))

(defn request-method
  "Returns the request method."
  [context]
  (get-in context [:request :request-method]))

(defn parameters
  "Returns parameters from the request."
  [context key]
  (get-in context [:request :parameters key]))

(def resource
  "The base resource for all liberator handlers"
  {;; allow for muuntaja to deal with content negotiation
   :available-media-types (m/encodes m/instance)
   :as-response (fn [data _context]
                  (when data
                    {:body data}))})

(defmacro defresource
  "Defines a liberator resource with some extra router-based metadata.

  `_router` is a key that maps to `reitit` route data."
  [symbol & {:keys [_router] :as kwargs}]
  ;; base the `:allowed-methods` value on the reitit router data
  (let [allowed-methods (->> _router
                             (keys)
                             (set)
                             (set/intersection ring/http-methods))
        kwargs (assoc kwargs :allowed-methods allowed-methods)]
    `(def ~symbol
       (vary-meta
        (liberator/resource
         resource
         ~@(apply concat (dissoc kwargs :_router)))
        merge
        ;; this extra metadata will be used by `handler` to define
        ;; the data for our `reitit` handlers.
        {:_router (assoc ~_router :name ~(ns-keyword symbol))}))))

(defn handler
  "Builds a `reitit` data handler from `var` and `opts`."
  [var & [opts]]
  (let [{:keys [_router]} (meta @var)]
    (->> _router
         (deep-merge opts)
         (assoc-handler var))))

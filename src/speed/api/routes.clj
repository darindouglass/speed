(ns speed.api.routes
  (:require [speed.api.middleware :as middleware]
            [speed.api.resource :as resource]
            [speed.api.runner :as runner]
            [speed.specs :as specs]
            [muuntaja.core :as m]
            [reitit.coercion.spec :as spec]
            [reitit.swagger-ui :as swagger-ui]
            [reitit.ring :as ring]
            [reitit.ring.coercion :as coercion]
            ;; [reitit.ring.middleware.dev :as dev]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.parameters :as parameters]
            [reitit.swagger :as swagger]))

(defn path-params
  "Returns path parameters based on `specs`."
  [& specs]
  {:parameters {:path (reduce (fn [acc spec]
                                (assoc acc (keyword (name spec)) spec))
                              {}
                              specs)}})

(def swagger-route
  ["/swagger.json"
   {:get {:no-doc true
          :swagger {:info {:title "Speed"}
                    :basePath "/"}
          :handler (swagger/create-swagger-handler)}}])

(def v2-routes
  ["/v1" {:swagger {:tags ["v2"]}}
   ["/runners"
    ["/query" (resource/handler #'/resource {})]]])

(defn swagger-enabled?
  "Returns true iff swagger is enabled"
  []
  true)

(def all-routes
  (cond-> [v2-routes]
    (swagger-enabled?) (conj swagger-route)))

(def router
  (ring/router
   all-routes
   {;; uncomment for useful request/response printing during the middleware chain
    ;;:reitit.middleware/transform dev/print-request-diffs
    :data {:coercion spec/coercion
           :muuntaja m/instance
           :middleware [middleware/request-logging-middleware
                        middleware/decision-tracing-middleware
                        parameters/parameters-middleware
                        ;; liberator just does response content-negotiation but if
                        ;; we want request-body validation we need request content-negotiation
                        ;; as well.
                        ;;
                        ;; muuntaja does both (and supports more) so we use it for both directions.
                        muuntaja/format-middleware
                        middleware/remove-liberator-negotiation
                        middleware/exception-middleware
                        coercion/coerce-response-middleware
                        coercion/coerce-request-middleware
                        coercion/coerce-exceptions-middleware]}}))

(def app
  (ring/ring-handler
   router
   (ring/routes
    (when (swagger-enabled?)
      (swagger-ui/create-swagger-ui-handler {:path "/"}))
    (ring/redirect-trailing-slash-handler))))

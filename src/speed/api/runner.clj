(ns speed.api.runner
  (:require [speed.api.resource :refer [defresource] :as request]
            [speed.models.runner :as runner]))

(defn exists?
  [context]
  (let [{:keys [access-token-id]} (request/parameters context :path)]
    (when-let [account (account/find-by :id access-token-id)]
      {::result (select-keys account [:id :created-at])})))

(defresource resource
  :_router {:get {:summary "Returns information about an access-token-id"}}
  :exists? exists?
  :handle-ok ::result)

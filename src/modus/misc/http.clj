(ns modus.misc.http)

(defn status-response? [status response] (= status (:status response)))

(defn ok-response?
  [response] (status-response? 200 response))

(defn created-response?
  [response] (status-response? 201 response))

(defn no-content-response?
  [response] (status-response? 204 response))

(defn bad-request?
  [response] (status-response? 400 response))

(defn unauthorized-request?
  [response] (status-response? 401 response))

(defn permission-denied-response?
  [response] (status-response? 403 response))

(defn not-found-response?
  [response] (status-response? 404 response))

(defn conflict-response?
  [response] (status-response? 409 response))

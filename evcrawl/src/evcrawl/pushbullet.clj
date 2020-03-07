(ns evcrawl.pushbullet
  (:require [clj-http.client :as client]
            [cheshire.core :refer [parse-string]]
            [evcrawl.dbkeys]))

; for this script to work, there must be a dbkeys.clj
; file containing the following line:
; (def pushbullet-api-key "your-key-here")
(def ^:private api-key evcrawl.dbkeys/pushbullet-api-key)
(def ^:private api-url "https://api.pushbullet.com/v2")

(defn send-push
  "Sends a PushBullet message to the owner of the key"
  [title body]
  (let [params {:type  "note"
                :title title
                :body  body}]
    (client/post (str api-url "/pushes")
                 {:headers     {"Access-Token" api-key}
                  :form-params params})))
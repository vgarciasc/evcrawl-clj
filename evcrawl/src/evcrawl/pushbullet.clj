(ns evcrawl.pushbullet
  (:require [clj-http.client :as client]
            [cheshire.core :refer [parse-string]]
            [evcrawl.poordb :as db]))

(def ^:private api-url "https://api.pushbullet.com/v2")
(def token-filepath "pb-token.txt")

(defn check-token
  "Guarantees that the token file is correct"
  []
  (when (db/is-file-nonexistent? token-filepath)
    (spit token-filepath '"your-token-here")
    (throw (Throwable. (str "Please fill the token in file '" token-filepath "'")))))

(defn send-push
  "Sends a PushBullet message to the owner of the key"
  [title body]
  (check-token)
  (let [token (slurp token-filepath)]
    (let [params {:type  "note"
                  :title title
                  :body  body}]
      (client/post (str api-url "/pushes")
                   {:headers     {"Access-Token" token}
                    :form-params params}))))
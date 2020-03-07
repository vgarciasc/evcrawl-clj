(ns evcrawl.encrawl
  (:require [net.cgrand.enlive-html :as html]
            [clojure.string :as s]
            [evcrawl.hushpuppy :as hp]
            [evcrawl.pushbullet :as pb]
            [evcrawl.poordb :as db]
            [evcrawl.resty])
  (:gen-class))

(def ^:dynamic *base-url* "https://www.estantevirtual.com.br/busca?q=")

(defn get-book-url
  "Returns the URL of a search for a certain book"
  [book]
  (str *base-url* (s/join "+" (:tags book))))

(defn fetch-url
  "Fetches the content of the URL"
  [url]
  (html/html-resource (java.net.URL. url)))

(def memoize-fetch-url (memoize fetch-url))

(defn filter-price-tags
  "Filters the price tags from the page HTML"
  [html-content]
  (map html/text
       (html/select html-content #{[:.m-min] [:.m-max]})))

(defn parse-prices-as-floats
  [html-content]
  (->> (filter-price-tags html-content)
       (map #(re-find #"\d(.*)" %))
       (map first)
       (map #(s/replace % "," "."))
       (map #(Float/parseFloat %))
       (set)
       (sort)))

(defn fetch-book-prices
  "Fetches the book's current listed prices"
  [book]
  (->> (get-book-url book)
       (memoize-fetch-url)
       (parse-prices-as-floats)))

(defn filter-notable-prices
  "Compare old listed prices to new prices, and decides
   which of the new prices are notification-worthy"
  [old-prices new-prices]
  (if (empty? old-prices)
    new-prices
    (filter #(< % (apply min old-prices)) new-prices)))

(defn stringify-prices-as-list
  [prices]
  (->> prices
       (map hp/to-currency-BRL)
       (map #(str "\t * " %))
       (s/join "\n")))

(defn stringify-entry-notification
  [notification]
  (str (get-in notification [:entry :book :name])
       "\n"
       (stringify-prices-as-list
         (:to-notify notification))))

(defn notify
  "Sends the notifications"
  [notifications]
  (when (not (empty? notifications))
    (->> notifications
         (map stringify-entry-notification)
         (s/join "\n")
         (pb/send-push "THE VOID BECKONS...")))
  "success")

(defn run-script
  "Runs the entire script"
  []
  (let [data (db/read-from-file)]
    (loop [entry (first data)
           entries (rest data)
           notifications []
           new-entries []]
      (if (not (nil? entry))
        (let [new-prices (fetch-book-prices (:book entry))
              notable-prices (filter-notable-prices (:prices entry) new-prices)]
          (recur (first entries)
                 (rest entries)
                 (if (not (empty? notable-prices))
                   (conj notifications
                         {:entry     entry
                          :to-notify notable-prices})
                   notifications)
                 (conj new-entries
                       {:book   (:book entry)
                        :prices new-prices
                        :time   (if (not (empty? notable-prices))
                                  (hp/current-timestamp)
                                  (:time entry))})))
        (do
          (db/save-to-file new-entries)
          (notify notifications))))))

(defn -main
  [& args]
  (when (not= (count args) 1)
    (throw (Throwable. "Please use one flag and one flag only.")))
  (case (first args)
    "-server" (evcrawl.resty/-main)
    "-script" (run-script)))
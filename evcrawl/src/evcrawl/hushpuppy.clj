(ns evcrawl.hushpuppy)

(defn current-timestamp
  "Gets current timestamp in a friendly format"
  []
  (.format (java.text.SimpleDateFormat. "dd-MM-yyyy, HH:mm") (new java.util.Date)))

(defn to-currency-BRL
  "Converts float to currency string in BRL"
  [input]
  (str "R$ " (format "%.2f" input)))
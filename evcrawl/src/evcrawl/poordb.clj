(ns evcrawl.poordb
  (:require [clojure.string :as s]
            [clojure.java.io :as io]))

(def ^:dynamic *filepath* "data.txt")

(defn is-file-nonexistent?
  [filepath]
  (not (.exists (io/file filepath))))

(defn creates-if-nonexistent
  "Creates file if it doesn't exist."
  ([filepath] (creates-if-nonexistent filepath '()))
  ([filepath default]
   (if (is-file-nonexistent? filepath)
     (spit filepath default))))

(defn save-to-file
  "Saves entries to file"
  [entries]
  (creates-if-nonexistent *filepath*)
  (->> entries
       (clojure.pprint/pprint)
       (with-out-str)
       (spit *filepath*)))

(defn read-from-file
  "Reads entries from file"
  []
  (creates-if-nonexistent *filepath*)
  (->> (slurp *filepath*)
       (clojure.edn/read-string)
       (flatten)))

(defn create-book
  "Constructs new book data"
  [name tags]
  {:name name
   :tags (if (string? tags)
           (s/split tags #"\s")
           tags)})

(defn create-entry
  [book]
  {:book book :prices ()})

(defn add-book
  "Adds a new book"
  [name tags]
  (-> (read-from-file)
      (conj (create-entry (create-book name tags)))
      (save-to-file)))

(defn remove-book
  "Removes a book by its name"
  [name]
  (->> (read-from-file)
       (remove #(= name (get-in % [:book :name])))
       (save-to-file)))

(defn update-book
  "Updates a book by its name"
  [old-name new-name new-tags]
  (let [entries (read-from-file)]
    (let [entry (filter #(= old-name (get-in % [:book :name])) entries)]
      (when entry
        (let [updated (conj
                        (remove #(= old-name (get-in % [:book :name])) entries)
                        (create-entry (create-book new-name new-tags)))]
          (save-to-file updated))))))

(defn list-books []
  (read-from-file))
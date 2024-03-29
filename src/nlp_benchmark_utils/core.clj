(ns nlp-benchmark-utils.core
  (:require [clojure.string :as string]
            [amazonica.aws.dynamodbv2 :as db]))

(def path-to-files "/home/echavis/nlp-benchmark-utils/dataset/")
(def files (map #(str path-to-files %)
                ["wiki1.dev.qa" "wiki1.test.qa" "wiki1.train.qa"]))
(def table-name "original-sentences")
(def table-writes-per-second 5)

(defn- get-lines [filename]
  (with-open [rdr (clojure.java.io/reader filename)]
    (reduce conj [] (line-seq rdr))))

(defn- get-sentence [sentence-lines]
  {:label (first (string/split (first sentence-lines) #"\t"))
   :sentence (second sentence-lines)})

(defn- get-sentences [lines]
  (let [first-and-rest (split-with #(not (empty? %)) lines)]
    (if (empty? (first first-and-rest))
      []
      (conj (get-sentences (rest (second first-and-rest)))
            (get-sentence (first first-and-rest))))))

(defn- get-sentence-ids [num-sentences]
  (repeatedly num-sentences #(str (java.util.UUID/randomUUID))))

(defn- get-next-sentence-ids [sentence-ids]
  (conj (vec (rest sentence-ids)) (first sentence-ids)))

(defn- get-sentences-with-ids [sentences]
  (let [sentence-ids (get-sentence-ids (count sentences))
        next-sentence-ids (get-next-sentence-ids sentence-ids)]
    (map (fn [sentence sentence-id next-sentence-id]
           (assoc sentence
                  :sentence-id sentence-id
                  :next-sentence-id next-sentence-id))
         sentences
         sentence-ids
         next-sentence-ids)))

(defn- store-sentence! [sentence]
  (db/put-item :table-name table-name
               :item sentence))

(defn store-all-sentences! [filenames]
  (map (fn [filename]
         (map (fn [sentence]
                (Thread/sleep (/ 1000 table-writes-per-second))
                (store-sentence! sentence))
              (get-sentences-with-ids (get-sentences (get-lines filename)))))
       filenames))


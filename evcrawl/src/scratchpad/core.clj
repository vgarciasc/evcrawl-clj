(ns scratchpad.core)

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

(defn palindrome
  [x]
  (if (= (count x) 0)
    true
    (if (= (first x) (last x))
      (palindrome (drop-last 1 (drop 1 x)))
      false)))

(defn fibonacci
  [quantity]
  (loop [f1 1 f2 1 k 0 result '()]
    (if (< k quantity)
      (recur f2 (+ f1 f2) (inc k) (conj result f1))
      (sort result))))
;(println (fibonacci 5))

(defn get-max [& params]
  (loop [args params, max 0]
    (if (not (empty? args))
      (recur (drop 1 args)
             (let [k (first args)]
               (if (> k max) k max)))
      max)))
;(println (get-max 1 7 3 2 9 4 5))

(defn get-caps [str]
  (clojure.string/join (filter #(Character/isUpperCase %) str)))
;(println (get-caps "HeLlO, WoRlD!"))

(defn duplicate-sequence [x]
  (reduce #(conj (conj %1 %2) %2) '() x))
;(println (duplicate-sequence [1 2 3]))

(defn remove-duplicates [x]
  (loop [xs x ys []]
    (if (not (empty? xs))
      (let [i (first xs) j (first (drop 1 xs))]
        (if (= i j)
          (recur (drop 1 xs) ys)
          (recur (drop 1 xs) (conj ys i))))
      ys)))
;(println (remove-duplicates [1 1 2 3 3 2 2 3]))

(defn my-range [min max]
  (loop [i min res []]
    (if (< i max)
      (recur (inc i) (conj res i))
      res)))
;(println (my-range 2 9))

(defn factorial [x]
  (loop [i 1 acc 1]
    (if (<= i x)
      (recur (inc i) (* acc i))
      acc)))
;(println (factorial 3))

((comp inc *) 2 3)
(-> 2 (dec) (inc) (* 7))

(defn two-comp
  [f g]
  (fn [& args]
    (f (apply g args))))
((two-comp inc *) 2 3)

(defmacro infix
  [[op1 operand op2]]
  (list operand op1 op2))

(defmacro criticize-code
  [criticism code]
  `(println ~criticism (quote ~code)))

(defmacro code-critic
  [bad good]
  `(do ~(criticize-code "bad code: " bad)
       ~(criticize-code "good code: " good)))
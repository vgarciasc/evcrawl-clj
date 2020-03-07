(ns scratchpad.tictactoe)

(def DEFAULT-TILE "-")

; rendering

(defn row-start [row] (* row 3))

(defn create-board
  "Creates a tic-tac-toe board"
  []
  (vec (repeat 9 DEFAULT-TILE)))

(defn render-row-elem
  "Renders a single elemnt of the board"
  [pos elem]
  (case (mod pos 3)
    (0 1) (str elem "|")
    (2) (str elem)))

(defn render-row-board
  "Renders a row of the board"
  [board row-num]
  (loop [col 0 res ""]
    (if (< col 3)
      (let [pos (+ col (* row-num 3))]
        (recur
          (inc col)
          (str res (render-row-elem pos (nth board pos)))))
      res)))

(defn print-board
  "Renders the board in a pretty manner"
  [board]
  (doseq [row (range 0 3)]
    (println (render-row-board board row))))

;(println (render-board (create-board)))

; playing

(defn is-space-empty?
  "Checks if space is empty"
  [board pos]
  (= (nth board pos) DEFAULT-TILE))

(defn is-valid-play?
  "Checks if play is valid"
  [board pos]
  (and
    (integer? pos)
    (< pos 9) (>= pos 0)
    (is-space-empty? board pos)))
;(println (is-valid-play? (create-board) 0))

(defn create-player
  "Generates a new player"
  [name icon]
  {:name name :icon icon})

(defn make-play
  "Executes player move in board"
  [board pos player]
  (assoc board pos (:icon player)))

(defn get-input
  ([] (get-input nil))
  ([default]
   (let [input (clojure.string/trim (read-line))]
     (if (empty? input)
       default
       (clojure.string/lower-case input)))))

(defn prompt-move
  [board player]
  (println (str (:name player) ", please select a position."))
  (let [pos (Integer/parseInt (get-input))]
    (if (is-valid-play? board pos)
      pos
      (do
        (println "That isn't a valid position. Please try again.")
        (prompt-move board player)))))

(defn is-seq-victor?
  [seq]
  (if (apply = seq) (first seq) nil))

(defn is-row-victor?
  [board row-num]
  (let [row (nth (partition 3 board) row-num)]
    (is-seq-victor? row)))

(defn is-col-victor?
  [board col-num]
  (let [col (take-nth 3 (drop col-num board))]
    (is-seq-victor? col)))

(defn get-diag
  [board diag-num]
  (case diag-num
    0 [(nth board 0) (nth board 4) (nth board 8)]
    1 [(nth board 2) (nth board 4) (nth board 6)]
    nil))

(defn is-diag-victor?
  [board diag-num]
  (is-seq-victor? (get-diag board diag-num)))

(defn has-victor?
  [board]
  (let [victors (list (reduce #(conj %1 (is-row-victor? board %2)) [] (range 0 3))
                      (reduce #(conj %1 (is-col-victor? board %2)) [] (range 0 3))
                      (reduce #(conj %1 (is-diag-victor? board %2)) [] (range 0 2)))]
    (->> victors
         (flatten)
         (filter #(not (nil? %)))
         (first))))

(defn are-there-valid-moves?
  [board]
  (->> (range 0 9)
       (map #(is-valid-play? board %))
       (filter #(= % true))
       (first)))

(def PLAYER-1 (create-player "Human Player" "H"))
(def PLAYER-2 (create-player "Machine Player" "M"))

(defn toggle-player
  [player players]
  (first (filter #(not= (:icon %) (:icon player)) players)))

(defn game-loop
  []
  (let [players [PLAYER-1 PLAYER-2]]
    (loop [board (create-board)
           curr-player (first players)]
      (let [winner (has-victor? board)]
        (print-board board)
        (if (and (not= winner nil) (not= winner DEFAULT-TILE))
          (println "Game over. Winner is " winner "!")
          (if (not (are-there-valid-moves? board))
            (println "Game over. It's a tie!")
            (recur (make-play
                     board
                     (prompt-move board curr-player)
                     curr-player)
                   (toggle-player curr-player players)))))
      )))

(game-loop)

;(println (get-diag (create-board) 0))
;(println (is-diag-victor? (create-board) 0))

(def BOARD_WIN_H
  (-> (create-board)
      (make-play 0 PLAYER-2)
      (make-play 1 PLAYER-2)
      (make-play 5 PLAYER-2)
      (make-play 4 PLAYER-1)
      (make-play 8 PLAYER-1)
      (make-play 3 PLAYER-2)
      (make-play 2 PLAYER-1)
      (make-play 6 PLAYER-1)))
(def BOARD_NOWIN
  (-> (create-board)
      (make-play 0 PLAYER-2)
      (make-play 1 PLAYER-2)
      (make-play 5 PLAYER-2)
      (make-play 8 PLAYER-1)
      (make-play 3 PLAYER-2)
      (make-play 2 PLAYER-1)
      (make-play 6 PLAYER-1)))

;(defn is-game-over?
;  [board]
;  (or
;    (= (nth board 0) (nth board 1) (nth board 2))))
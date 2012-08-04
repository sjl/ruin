(ns ruin.buildings
  (:use [ruin.util :only [colorize]]))


(defonce bid (atom 1))

(defn get-building-id []
  (swap! bid inc))


(defmulti get-building-sheet :type)

(defmethod get-building-sheet :house [building]
  (colorize :red ["/`\\"
                  "|_|"]))

(defmethod get-building-sheet :farm [building]
  (colorize :yellow ["~~~~~"
                     "~~~~~"
                     "~~~~~"]))

(defmethod get-building-sheet :silo [building]
  (let [middle (if (zero? (:contents building))
                 \space
                 \≈)]
    ["+-+"
     [\|
      [middle {:fg :yellow}]
      \|]
     "+-+"]))


(defn make-house []
  (agent {:id (get-building-id)
          :capacity 4
          :type :house
          :location [(rand-int 50) (rand-int 30)]
          :size [3 2]}))

(defn make-silo
  ([] (make-silo 0))
  ([contents]
   (agent {:id (get-building-id)
           :contents contents
           :capacity 50
           :location [(rand-int 50) (rand-int 30)]
           :size [3 3]
           :type :silo})))

(defn make-farm []
  (agent {:id (get-building-id)
          :location [(rand-int 50) (rand-int 30)]
          :size [5 3]
          :type :farm}))


(defn start-building [b]
  nil)

(ns ruin.buildings)


(defonce bid (atom 1))

(defn get-building-id []
  (swap! bid inc))


(defn make-house []
  (agent {:id (get-building-id)
          :capacity 4
          :type :house
          :location [(rand-int 50) (rand-int 30)]
          :size [3 2]
          :get-sheet (fn [house]
                       ["/`\\"
                        "|_|"])}))

(defn make-silo
  ([] (make-silo 0))
  ([contents]
   (agent {:id (get-building-id)
           :contents contents
           :capacity 50
           :location [(rand-int 50) (rand-int 30)]
           :size [3 3]
           :get-sheet (fn [silo]
                        ["+-+"
                         [\| [(if (zero? (:contents silo))
                                \space
                                \≈)
                              {:fg :yellow}] \|]
                         "+-+"])
           :color :yellow
           :type :silo})))


(defn start-building [b]
  nil)

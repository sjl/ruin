(ns ruin.buildings)


(defonce bid (atom 1))

(defn get-building-id []
  (swap! bid inc))


(defn make-house []
  (agent {:id (get-building-id)
          :capacity 4
          :type :house}))

(defn make-silo
  ([] (make-silo 0))
  ([contents]
   (agent {:id (get-building-id)
           :contents contents
           :capacity 50
           :type :silo})))


(defn start-building [b]
  nil)

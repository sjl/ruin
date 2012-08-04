(ns ruin.entities)


(defonce eid (atom 1))

(defn get-entity-id []
  (swap! eid inc))

(defn make-person []
  (agent {:nutrition 100
          :id (get-entity-id)
          :location [(rand-int 40) (rand-int 40)]
          :type :person
          :glyph "@"
          :happiness 50}))


(defn start-person [p]
  nil)

(ns ruin.entities)


(defonce eid (atom 1))

(defn get-entity-id []
  (swap! eid inc))

(defn make-person []
  (agent {:nutrition 100
          :id (get-entity-id)
          :type :person
          :happiness 50}))


(defn start-person [p]
  nil)

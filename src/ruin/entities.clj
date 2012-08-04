(ns ruin.entities
  (:use [ruin.state :only [game]]
        [ruin.util :only [dir-to-offset]]))


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


(defn walk-random [person]
  (let [dir (rand-nth [:n :s :e :w :ne :nw :se :sw])
        offset (dir-to-offset dir)
        new-location (map + offset (:location person))]
    (assoc person :location new-location)))

(defn tick-person [person tick]
  (dosync
    (when (:state @game)
      (send-off *agent* #'tick-person (inc tick)))
    (Thread/sleep 1000)
    (if (< (rand) 0.5)
      (walk-random person)
      person)))


(defn start-person [p]
  (send-off p tick-person 0))

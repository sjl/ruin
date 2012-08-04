(ns ruin.entities
  (:use [ruin.state :only [game]]
        [ruin.util :only [dir-to-offset]]))


(defonce eid (atom 1))

(defn get-entity-id []
  (swap! eid inc))

(defn make-person [location]
  (agent {:nutrition (rand-int 5)
          :id (get-entity-id)
          :location location
          :type :person
          :glyph "@"
          :happiness 50}))


(defn walk-random [person]
  (let [dir (rand-nth [:n :s :e :w :ne :nw :se :sw])
        offset (dir-to-offset dir)
        new-location (map + offset (:location person))]
    (assoc person :location new-location)))

(defn try-walking [person]
  (if (< (rand) 0.5)
    (walk-random person)
    person))

(defn try-eating [person tick]
  (if (zero? (mod tick 2))
    (update-in person [:nutrition] dec)
    person))

(defn check-starved [person]
  (if-not (pos? (:nutrition person))
    (do
      (alter (:entities @game) dissoc (:id person))
      (assoc person :dead true))
    person))


(defn tick-person [person tick]
  (Thread/sleep 1000)
  (dosync
    (when (and (:state @game)
               (not (:dead person)))
      (send-off *agent* #'tick-person (inc tick)))
    (-> person
      try-walking
      (try-eating tick)
      check-starved)))


(defn start-person [p]
  (send-off p tick-person 0))

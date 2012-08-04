(ns ruin.ui)


(defrecord UI [kind])

(defn push-ui [game ui]
  (dosync
    (alter game update-in [:uis] conj ui)))

(defn pop-ui [game]
  (dosync
    (let [result (last (:uis @game))]
      (alter game update-in [:uis] pop)
      result)))

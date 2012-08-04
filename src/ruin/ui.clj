(ns ruin.ui
  (:use [ruin.state :only [game]]))


(defrecord UI [kind])

(defn push-ui
  "Push the UI onto the UI stack of the game.

  Executes in a transaction, but make sure to use a transaction of your own if
  you want to synchronize it with another action, like popping a UI.

  "
  [ui]
  (dosync
    (alter game update-in [:uis] conj ui)))

(defn pop-ui
  "Pop the last UI on the UI stack of the game off, and return it.

  Executes in a transaction, but make sure to use a transaction of your own if
  you want to synchronize it with another action, like pushing another UI.

  "
  []
  (dosync
    (let [result (last (:uis @game))]
      (alter game update-in [:uis] pop)
      result)))

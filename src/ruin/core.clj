(ns ruin.core
  (:require [lanterna.screen :as s]))


(defonce game (ref nil))


(defn create-fresh-game []
  (dosync
    (ref-set game {})
    (let [scr (s/get-screen)]
      (alter game assoc :screen scr)
      (s/start scr))))


(defn run-game []
  (s/stop (:screen @game)))

(defn main []
  (create-fresh-game)
  (run-game))

(defn -main [& args]
  (main))


(comment

  (main)

  )


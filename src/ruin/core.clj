(ns ruin.core
  (:use [ruin.state :only [game]]
        [ruin.entities :only [make-person]]
        [ruin.buildings :only [make-house make-silo make-farm]]
        [ruin.ui :only [->UI]]
        [ruin.drawing :only [draw-ui]]
        [ruin.input :only [process-input]])
  (:require [lanterna.screen :as s]))


(defn calc-score []
  (letfn [(count-things [source kind]
            (count (filter #(= kind (:type @%))
                           (vals @(source @game)))))]
    (dosync
      (+ (* 100 (count-things :entities :person))
         (*  10 (count-things :buildings :silo))
         (*  15 (count-things :buildings :house))))))


(defn -score-loop []
  (let [score (calc-score)]
    (dosync
      (let [[peak _] @(:score @game)]
        (ref-set (:score @game)
                 [(dec (max peak score)) (dec score)]))))
  (Thread/sleep 500))

(defn score-loop
  "Continually update the game's score, until the game stops running."
  []
  (-score-loop)
  
  (when (:state @game)
    (recur)))


(defn gen-things [n f]
  (for [thing (repeatedly n f)]
    [(:id @thing) thing]))


(defn create-initial-population []
  (into {}
        (gen-things 10 make-person)))

(defn create-initial-buildings []
  (into {}
        (concat (gen-things 4 make-house)
                (gen-things 1 make-farm)
                (gen-things 2 #(make-silo 25)))))


(defn create-fresh-game
  "Refresh the game var with a new game.

  For now the screen is refreshed too.  This may change.

  "
  []
  (dosync
    (let [scr (s/get-screen :swing {:cols 120
                                    :rows 35
                                    :font "Menlo"
                                    :font-size 16})]
      (ref-set game {:screen scr
                     :entities (ref (create-initial-population))
                     :buildings (ref (create-initial-buildings))
                     :resources (ref 20)
                     :viewport-origin [0 0]
                     :score (ref [0 0])
                     :state :running
                     :uis [(->UI :start)]})
      (s/start scr))))


(defn draw-uis
  "Draw each UI in the game in turn."
  []
  (s/clear (:screen @game))
  (dorun (map draw-ui (:uis @game)))
  (s/redraw (:screen @game)))

(defn draw-loop
  "Continually draw all the game's UIs, until the game stops running."
  []
  (draw-uis)
  (Thread/sleep 300)
  (when (:state @game)
    (recur)))


(defn input-loop
  "Continually process input, until the game stops running."
  []
  (process-input (last (:uis @game))
                 (io! (s/get-key-blocking (:screen @game))))
  (when (:state @game)
    (recur)))


(defn run-game
  "Start all the various loops of the game, then wait for it to be done."
  []
  (let [screen (:screen @game)]
    (future (draw-loop))
    (future (input-loop))
    (future (score-loop))
    ))


(defn main []
  (create-fresh-game)
  (run-game))

(defn -main [& args]
  (main))


(comment
  (main))

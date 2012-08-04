(ns ruin.core
  (:use [ruin.state :only [game]]
        [ruin.entities :only [make-person start-person]]
        [ruin.buildings :only [make-house make-silo make-farm]]
        [ruin.ui :only [->UI]]
        [ruin.drawing :only [draw-ui]]
        [ruin.input :only [process-input]])
  (:require [lanterna.screen :as s]))


(defn entities-at [coord]
  (filter #(= coord (:location @%))
          (vals @(:entities @game))))

(defn buildings-collide? [a b]
  (let [[ax ay] (:location a)
        [bx by] (:location b)
        [aw ah] (:size a)
        [bw bh] (:size b)
        ax' (+ ax aw)
        ay' (+ ay ah)
        bx' (+ bx bw)
        by' (+ by bh)]
    (not (or (< ay' by)
             (> ay by')
             (< ax' bx)
             (> ax bx')))))


(defn add-building [f]
  (let [existing (vals @(:buildings @game))
        building (f)]
    (if (some #(buildings-collide? @building @%)
              existing)
      (recur f)
      (dosync
        (alter (:buildings @game) assoc (:id @building) building)))))

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
  (let [population (into {}
                         (gen-things 10 make-person))]
    (dorun (map start-person (vals population)))
    population))

(defn create-initial-buildings []
  (dorun (repeatedly 4 #(add-building make-house)))
  (dorun (repeatedly 2 #(add-building (partial make-silo 25))))
  (dorun (repeatedly 1 #(add-building make-farm))))


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
                     :buildings (ref {})
                     :resources (ref 20)
                     :viewport-origin [0 0]
                     :score (ref [0 0])
                     :state :running
                     :uis [(->UI :start)]})
      (create-initial-buildings)
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
  (Thread/sleep 100)
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

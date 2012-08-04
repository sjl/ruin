(ns ruin.core
  (:use [ruin.ui :only [->UI]]
        [ruin.drawing :only [draw-ui]]
        [ruin.input :only [process-input]])
  (:require [lanterna.screen :as s]))


(defonce game (ref nil))

(defn create-fresh-game
  "Refresh the game var with a new game.

  For now the screen is refreshed too.  This may change.

  "
  []
  (dosync
    (let [scr (s/get-screen :swing {:cols 120
                                    :rows 45
                                    :font "Menlo"
                                    :font-size 16})]
      (ref-set game {:screen scr
                     :entities {}
                     :state :running
                     :uis [(->UI :start)]})
      (s/start scr))))


(defn draw-uis
  "Draw each UI in the game in turn."
  []
  (let [game @game]
    (s/clear (:screen game))
    (dorun (map #(draw-ui % game) (:uis game)))
    (s/redraw (:screen game))))

(defn draw-loop
  "Continually draw all the game's UIs, until the game stops running."
  []
  (draw-uis)
  (Thread/sleep 500)
  (when (:state @game)
    (recur)))


(defn input-loop
  "Continually process input, until the game stops running."
  []
  (process-input (last (:uis @game))
                 game
                 (s/get-key-blocking (:screen @game)))
  (when (:state @game)
    (recur)))


(defn run-game
  "Start all the various loops of the game, then wait for it to be done."
  []
  (let [screen (:screen @game)]
    (future (draw-loop))
    (future (input-loop))))


(defn main []
  (create-fresh-game)
  (run-game))

(defn -main [& args]
  (main))


(comment
  (main))

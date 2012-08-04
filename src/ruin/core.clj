(ns ruin.core
  (:use [ruin.ui :only [->UI]]
        [ruin.drawing :only [draw-ui]]
        [ruin.input :only [process-input]])
  (:require [lanterna.screen :as s]))


(defonce game (ref nil))

(defn create-fresh-game []
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



(defn draw-uis []
  (let [game @game]
    (s/clear (:screen game))
    (dorun (map #(draw-ui % game) (:uis game)))
    (s/redraw (:screen game))))

(defn draw-loop []
  (draw-uis)
  (Thread/sleep 500)
  (when (:state @game)
    (recur)))


(defn input-loop []
  (process-input (last (:uis @game))
                 game
                 (s/get-key-blocking (:screen @game)))
  (when (:state @game)
    (recur)))


(defn run-game []
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

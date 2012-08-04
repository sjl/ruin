(ns ruin.core
  (:use [ruin.ui :only [draw-ui ->UI]])
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


(defn push-ui [])
(defn pop-ui [])

(defn draw-uis []
  (let [game @game]
    (dorun (map #(draw-ui % game) (:uis game)))
    (s/redraw (:screen game))))

(defn draw-loop []
  (draw-uis)
  (Thread/sleep 500)
  (when (:state @game)
    (recur)))


(defn run-game []
  (let [screen (:screen @game)]
    (future (draw-loop))
    (s/get-key-blocking screen)
    (dosync (alter game dissoc :state))
    (s/stop screen)))


(defn main []
  (create-fresh-game)
  (run-game))

(defn -main [& args]
  (main))


(comment
  (main))

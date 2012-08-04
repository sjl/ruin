(ns ruin.core
  (:use [ruin.ui :only [draw-ui ->UI]])
  (:require [lanterna.screen :as s]))


(defonce game (ref nil))

(defn create-fresh-game []
  (dosync
    (ref-set game {})
    (let [scr (s/get-screen)]
      (s/start scr)
      (alter game merge {:screen scr
                         :entities {}
                         :uis [(->UI :start)]}))))

(defn draw-uis [uis game]
  (dorun (map #(draw-ui % game) uis))
  (s/redraw (:screen game)))

(defn run-game []
  (let [screen (:screen @game)]
    (draw-uis (:uis @game) @game)
    (s/get-key-blocking screen)
    (s/stop screen)))


(defn main []
  (create-fresh-game)
  (run-game))

(defn -main [& args]
  (main))


(comment

  (main))

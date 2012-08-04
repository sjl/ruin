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
                     :uis [(->UI :start)]})
      (s/start scr))))

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

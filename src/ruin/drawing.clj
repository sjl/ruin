(ns ruin.drawing
  (:use [ruin.state :only [game]])
  (:require [lanterna.screen :as s]))

(def SIDEBAR-WIDTH 40)

(defmulti draw-ui
  "Draw the UI to the console.  Does not clear or refresh."
  (fn [ui]
    (:kind ui)))


(defmethod draw-ui :start [ui]
  (io!
    (s/put-sheet (:screen @game) 0 0
                 [" _____  _    _ _____ _   _"
                  "|  __ \\| |  | |_   _| \\ | |"
                  "| |__) | |  | | | | |  \\| |"
                  "|  _  /| |  | | | | | . ` |"
                  "| | \\ \\| |__| |_| |_| |\\  |"
                  "|_|  \\_\\\\____/|_____|_| \\_|"
                  ""
                  "press any key to begin..."])))

(defmethod draw-ui :win [ui]
  (io!
    (s/put-sheet (:screen @game) 0 0
                 ["Congratulations, you've won!"
                  ""
                  "press any key to continue..."])))

(defmethod draw-ui :lose [ui]
  (io!
    (s/put-sheet (:screen @game) 0 0
                 ["Sorry, you lost."
                  ""
                  "press any key to continue..."])))


(defn draw-map []
  (let [screen (:screen @game)
        [cols rows] (s/get-size screen)
        map-height rows
        map-width (- cols (inc SIDEBAR-WIDTH))]
    (s/put-sheet screen 0 0
                 (repeat map-height (repeat map-width \.)))))

(defmethod draw-ui :play [ui]
  (io!
    (draw-map)
    (s/put-sheet (:screen @game) 0 0
                 ["You are playing."
                  ""
                  "press enter to win, anything else to lose"])))

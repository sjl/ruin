(ns ruin.drawing
  (:require [lanterna.screen :as s]))


(defmulti draw-ui
  "Draw the UI to the console.  Does not clear or refresh."
  (fn [ui game]
    (:kind ui)))


(defmethod draw-ui :start [this {:keys [screen]}]
  (io!
    (s/put-sheet screen 0 0 [" _____  _    _ _____ _   _"
                             "|  __ \\| |  | |_   _| \\ | |"
                             "| |__) | |  | | | | |  \\| |"
                             "|  _  /| |  | | | | | . ` |"
                             "| | \\ \\| |__| |_| |_| |\\  |"
                             "|_|  \\_\\\\____/|_____|_| \\_|"
                             ""
                             "press any key to begin..."])))

(defmethod draw-ui :win [this {:keys [screen]}]
  (io!
    (s/put-sheet screen 0 0 ["Congratulations, you've won!"
                             ""
                             "press any key to continue..."])))

(defmethod draw-ui :lose [this {:keys [screen]}]
  (io!
    (s/put-sheet screen 0 0 ["Sorry, you lost."
                             ""
                             "press any key to continue..."])))


(defmethod draw-ui :play [this {:keys [screen]}]
  (io!
    (s/put-sheet screen 0 0 ["You are playing."
                             ""
                             "press enter to win, anything else to lose"])))


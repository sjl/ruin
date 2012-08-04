(ns ruin.ui
  (:require [lanterna.screen :as s]))


(defrecord UI [kind])

(defmulti draw-ui :kind)

(defmethod draw-ui :start [this {:keys [screen]}]
  (s/put-sheet screen 0 0 [" _____  _    _ _____ _   _"
                           "|  __ \\| |  | |_   _| \\ | |"
                           "| |__) | |  | | | | |  \\| |"
                           "|  _  /| |  | | | | | . ` |"
                           "| | \\ \\| |__| |_| |_| |\\  |"
                           "|_|  \\_\\\\____/|_____|_| \\_|"
                           ""
                           "press any key to begin..."]))

(defmethod draw-ui :play [this {:keys [screen]}]
  (s/put-sheet screen 0 0 ["You are playing."
                           ""
                          "press enter to win, anything else to lose"]))

(defmethod draw-ui :win [this {:keys [screen]}]
  (s/put-sheet screen 0 0 ["Congratulations, you've won!"
                           ""
                           "press any key to continue..."]))

(defmethod draw-ui :lose [this {:keys [screen]}]
  (s/put-sheet screen 0 0 ["Sorry, you lost."
                           ""
                           "press any key to continue..."]))

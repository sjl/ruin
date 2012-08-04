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


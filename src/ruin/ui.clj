(ns ruin.ui
  (:require [lanterna.screen :as s]))


(defrecord UI [kind])

(defmulti draw-ui :kind)

(defmethod draw-ui :start [this {:keys [screen]}]
  (println "wat")
  (s/put-string screen 0 0 "RUIN"))



(ns ruin.input
  (:use [ruin.ui :only [push-ui pop-ui ->UI]])
  (:require [lanterna.screen :as s]))


(defmulti process-input
  "Handle input from the console."
  (fn [ui game input]
    (:kind ui)))


(defmethod process-input :start [ui game input]
  (cond
    (#{:escape \q} input) (do
                            (dosync (alter game dissoc :state))
                            (s/stop (:screen @game)))
    :else (dosync
            (pop-ui game)
            (push-ui game (->UI :play)))))

(defmethod process-input :win [ui game input]
  (dosync
    (pop-ui game)
    (push-ui game (->UI :start))))

(defmethod process-input :lose [ui game input]
  (dosync
    (pop-ui game)
    (push-ui game (->UI :start))))


(defmethod process-input :play [ui game input]
  (case input
    :enter (dosync
             (pop-ui game)
             (push-ui game (->UI :win)))
    (dosync
      (pop-ui game)
      (push-ui game (->UI :lose)))))


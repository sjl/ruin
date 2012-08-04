(ns ruin.input
  (:use [ruin.state :only [game]]
        [ruin.ui :only [push-ui pop-ui ->UI]])
  (:require [lanterna.screen :as s]))


(defmulti process-input
  "Handle input from the console."
  (fn [ui input]
    (:kind ui)))

(defn quit-game []
  (do
    (dosync (alter game dissoc :state))
    (s/stop (:screen @game))))

(defmethod process-input :start [ui input]
  (cond
    (#{:escape \q} input) (quit-game)
    :else (dosync
            (pop-ui)
            (push-ui (->UI :play)))))

(defmethod process-input :win [ui input]
  (dosync
    (pop-ui)
    (push-ui (->UI :start))))

(defmethod process-input :lose [ui input]
  (dosync
    (pop-ui)
    (push-ui (->UI :start))))


(defmethod process-input :play [ui input]
  (case input
    \Q (quit-game)
    :enter (dosync
             (pop-ui)
             (push-ui (->UI :win)))
    (dosync
      (pop-ui)
      (push-ui (->UI :lose)))))


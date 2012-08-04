(ns ruin.input
  (:use [ruin.util :only [dir-to-offset]]
        [ruin.state :only [game]]
        [ruin.ui :only [push-ui pop-ui ->UI]])
  (:require [lanterna.screen :as s]))


(defn quit-game []
  (do
    (dosync (alter game dissoc :state))
    (s/stop (:screen @game))))


(defmulti process-input
  "Handle input from the console."
  (fn [ui input]
    (:kind ui)))


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


(defn scroll-map
  ([dir] (scroll-map dir 1))
  ([dir amount]
   (dosync
     (alter game update-in [:viewport-origin]
            #(map + (map (partial * amount)
                         (dir-to-offset dir))
                  %)))))


(defmethod process-input :play [ui input]
  (case input
    \Q (quit-game)
    \h (scroll-map :w)
    \j (scroll-map :s)
    \k (scroll-map :n)
    \l (scroll-map :e)
    \y (scroll-map :nw)
    \u (scroll-map :ne)
    \b (scroll-map :sw)
    \n (scroll-map :se)
    \H (scroll-map :w 6)
    \J (scroll-map :s 6)
    \K (scroll-map :n 6)
    \L (scroll-map :e 6)
    \Y (scroll-map :nw 6)
    \U (scroll-map :ne 6)
    \B (scroll-map :sw 6)
    \N (scroll-map :se 6)
    :enter (dosync
             (pop-ui)
             (push-ui (->UI :win)))
    (dosync
      (pop-ui)
      (push-ui (->UI :lose)))))


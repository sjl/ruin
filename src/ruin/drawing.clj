(ns ruin.drawing
  (:use [ruin.state :only [game]])
  (:require [lanterna.screen :as s]))

(def SIDEBAR-WIDTH 35)
(def STATUS-HEIGHT 10)

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

(defn draw-status []
  (let [game @game
        screen (:screen game)
        [cols rows] (s/get-size screen)
        x (- cols SIDEBAR-WIDTH)
        total-building-aspect (fn [aspect kind]
                                (reduce + (map #(aspect @%)
                                               (filter #(= kind (:type @%))
                                                       (vals @(:buildings game))))))
        population (count (filter #(= :person (:type @%))
                                  (vals @(:entities game))))
        housing (total-building-aspect :capacity :house)
        food (total-building-aspect :contents :silo)
        food-storage (total-building-aspect :capacity :silo)
        resources @(:resources game)
        [score-peak score-current] @(:score game)]
    (s/put-sheet screen x 0
                 ["SCORE"
                  (str score-peak " - " score-current " = " (- score-peak score-current))
                  ""
                  "STATUS"
                  "Time:         Year 45, Month 12"
                  (str "Population:   " population " / " housing)
                  (str "Food:         " food " / " food-storage)
                  "Satisfaction: 80%"
                  (str "Resources:    " resources)])))

(defn draw-main-menu []
  (let [screen (:screen @game)
        [cols rows] (s/get-size screen)
        x (- cols SIDEBAR-WIDTH)
        y (inc STATUS-HEIGHT)]
    (s/put-sheet screen x y
                 ["MENU"
                  "hjkl: move view"
                  "Q:    quit game"]))
  )
(defn draw-sidebar []
  (draw-status)
  (draw-main-menu))

(defmethod draw-ui :play [ui]
  (io!
    (draw-map)
    (draw-sidebar)
    (s/put-sheet (:screen @game) 0 0
                 ["You are playing."
                  ""
                  "press enter to win, anything else to lose"])))


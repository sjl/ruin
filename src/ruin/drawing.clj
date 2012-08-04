(ns ruin.drawing
  (:use [ruin.state :only [game]]
        [ruin.buildings :only [get-building-sheet]])
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


(defn draw-entities []
  (let [[ox oy] (:viewport-origin @game)
        screen (:screen @game)
        [cols rows] (s/get-size screen)
        map-height rows
        map-width (- cols (inc SIDEBAR-WIDTH))]
    (doseq [entity (vals @(:entities @game))]
      (let [{:keys [location glyph]} @entity
            [x y] location
            vx (- x ox)
            vy (- y oy)]
        (when (and (<= 0 vy (dec map-height))
                   (<= 0 vx (dec map-width)))
          (s/put-string screen vx vy glyph))))))

(defn draw-buildings []
  (let [[ox oy] (:viewport-origin @game)
        screen (:screen @game)
        [cols rows] (s/get-size screen)
        map-height rows
        map-width (- cols (inc SIDEBAR-WIDTH))]
    (doseq [building (vals @(:buildings @game))]
      (let [{:keys [location size]} @building
            [x y] location
            [width height] size
            vx (- x ox)
            vx' (+ vx width)
            vy (- y oy)
            vy' (+ vy height)]
        (when (and (<= 0 vy')
                   (<= 0 vx')
                   (< vy map-height)
                   (< vx map-width))
          (s/put-sheet screen vx vy (get-building-sheet @building)))))))

(defn draw-map []
  (let [screen (:screen @game)
        [cols rows] (s/get-size screen)
        map-height rows
        map-width (- cols (inc SIDEBAR-WIDTH))
        ]
    (s/put-sheet screen 0 0
                 (repeat map-height (repeat map-width \.)))))

(defn clear-sidebar []
  (let [game @game
        screen (:screen game)
        [cols rows] (s/get-size screen)
        x (- cols SIDEBAR-WIDTH 1)]
    (s/put-sheet screen x 0
                 (repeat rows (repeat cols \space)))))

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
                  "hjklyubn: move view"
                  "HJKLYUBN: move view faster"
                  "Q:        quit game"])))

(defn draw-sidebar []
  (clear-sidebar)
  (draw-status)
  (draw-main-menu))


(defmethod draw-ui :play [ui]
  (io!
    (draw-map)
    (draw-buildings)
    (draw-entities)
    (draw-sidebar)))


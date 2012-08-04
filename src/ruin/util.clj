(ns ruin.util)


(defn colorize [fg sheet]
  (map #(for [ch %]
          [ch {:fg fg}])
       sheet))

(defn dir-to-offset [dir]
  (case dir
    :w [-1 0]
    :e [1 0]
    :s [0 1]
    :n [0 -1]
    :nw [-1 -1]
    :ne [1 -1]
    :sw [-1 1]
    :se [1 1]))

(ns clo-tiler.core
  (:require [clojure.math.numeric-tower :as math]
            [org.httpkit.server :as http])
  (:import (com.vividsolutions.jts.geom Envelope Point Coordinate)))

;; webmerc origin point
(def origin-point (Coordinate. -20037508.34789244 20037508.34789244))
(def map-size (* (.x origin-point) 2))

(defn tile-to-envelope [x y z]
  "Transform a TMS x/y/zoom the equivalent bounding envelope"
  (let [size (/ map-size (math/expt 2 z))]
    (letfn [(x-at [n] 
              (+ (.x origin-point) (* n size)))
            (y-at [n] 
              (- (.y origin-point) (* n size)))]
      (Envelope. 
        (x-at x) 
        (x-at (inc x)) 
        (y-at (inc y))
        (y-at y)))))

(defn app [req]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body (str (.getMaxX (tile-to-envelope 100 200 10)))
  })

(defn -main []
  (http/run-server app {:port 8080}))

(ns clo-tiler.core
  (:require [clo-tiler.render :as render]
            [clojure.math.numeric-tower :as math]
            [org.httpkit.server :as http]
            [compojure.handler :refer [site]]
            [compojure.core :refer [defroutes GET context]])
  (:import (com.vividsolutions.jts.geom Envelope Point Coordinate)))

;; webmerc origin point
(def origin-point (Coordinate. -20037508.34789244 20037508.34789244))
(def map-size (* (.y origin-point) 2))

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
      
(defn tile-handler [table x y z]
  "Return an image response for a TMS request"
  {:status 200
    :headers {"Content-Type" "image/png"}
    :body (render/render-tile table (tile-to-envelope x y z))})

(defroutes routes
  (GET "/" [] (str "ghost mouse")) ;; it's working
  (GET ["/tile/:table/:z/:x/:y/", :z #"[0-9]+" :x #"[0-9]+" :y #"[0-9]+"]
      [table z x y] 
        (let [xx (read-string x) yy (read-string y) zz (read-string z)]
          (tile-handler table xx yy zz ))))

(defn -main []
  "Startup the webserver and define the routes to listen for"
  (println "Listening on port 8080")
  (http/run-server (site #'routes)  {:port 8080}))

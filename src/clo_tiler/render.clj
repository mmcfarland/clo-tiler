(ns clo-tiler.render
  (:require [clo-tiler.query :as q])
  (:import (com.vividsolutions.jts.geom Coordinate)
           (java.awt.image BufferedImage)
           (java.io ByteArrayOutputStream ByteArrayInputStream)
           (java.awt Color)
           (javax.imageio ImageIO)))
  
(def tile-width 256)
(def tile-height 256)

(defn- buffer-amt [envelope px] 
  (* px ( / (.getWidth envelope) tile-width)))

(defn- to-image-point [geo envelope]
  "Take a geo coordinate and convert to an image coordinate"
  (let [left (.getMinX envelope) 
        top (.getMaxY envelope)
        geoW (.getWidth envelope)
        geoH (.getHeight envelope)
        image-x (/ (- (.x geo) left) (/ geoW tile-width))
        image-y (/ (- top (.y geo)) (/ geoH tile-height))]
    (Coordinate. image-x image-y )))

(defn make-image [geoms] 
  "Draw vector of wkb byte arrays to image"
  (let [img (BufferedImage. tile-width tile-height BufferedImage/TYPE_INT_ARGB)
        baos (ByteArrayOutputStream.)]
    (doto (.getGraphics img)
      (.setColor Color/BLUE)
      (.fillRect 0 0 100 100))
    (ImageIO/write img "png" baos)
    (println geoms)
    (ByteArrayInputStream. (.toByteArray baos))))

(defn render-tile [table envelope]
  "Return an image for all features of :table in the bounds of envelope"
  (make-image (->> (buffer-amt envelope 10)
                   (q/get-tile-features table envelope))))


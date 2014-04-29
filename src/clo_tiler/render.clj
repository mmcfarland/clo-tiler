(ns clo-tiler.render
  (:require [clojure.java.jdbc :as sql])
  (:import (com.vividsolutions.jts.geom Envelope Point Coordinate)
           (java.awt.image BufferedImage)
           (java.io ByteArrayOutputStream ByteArrayInputStream)
           (java.awt Color)
           (javax.imageio ImageIO)))
  

(def conn "postgresql://localhost:5432/tyler")
(def w 256)
(def h 256)

(defn buffer-amt [envelope px] 
  (* px ( / (.getWidth envelope) w)))

(defn make-buffer-sql [buff env] 
  "Buffer envelope sql clause. Assumemes webm srid"
  (format "ST_Buffer(ST_MakeEnvelope(%f,%f,%f,%f, 3857), %s)"
    (.getMinX env) (.getMinY env) (.getMaxX env) (.getMaxY env) buff))

(defn make-spatial-query [buff-sql table]
  "Generate spatial query to select buffered bbox features"
  (format "SELECT ST_AsBinary(ST_Intersection(geom, %s)) FROM %s WHERE ST_Intersects(geom, %s);"
          buff-sql table buff-sql))
          
(defn get-tile-features [table envelope buff]
  "Gets list of WKB bytes for features in :table with a bbox 
   of :envelope buffered by :buff pixels"
  (into [] 
      (sql/query conn [(->
         (buffer-amt envelope buff)
         (make-buffer-sql envelope)
         (make-spatial-query table))])))

(defn make-image [] 
  (let [img (BufferedImage. w h BufferedImage/TYPE_INT_ARGB)
        baos (ByteArrayOutputStream.)]
    (doto (.getGraphics img)
      (.setColor Color/BLUE)
      (.fillRect 0 0 100 100))
    (ImageIO/write img "png" baos)
    (ByteArrayInputStream. (.toByteArray baos))))

(defn render-tile [table envelope]
  "Return an image for all features of :table in the bounds of envelope"
  (make-image))
  ;;(str (count (get-tile-features table envelope 10)))) 


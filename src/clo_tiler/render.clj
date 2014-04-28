(ns clo-tiler.render
  (:require [quil.core :refer :all]
            [clojure.java.jdbc :as sql])
  (:import (com.vividsolutions.jts.geom Envelope Point Coordinate)))

(def conn "postgresql://localhost:5432/tyler")
(def w 256)
(def h 256)

(defn buffer [envelope px] 
  (* px ( / (.getWidth envelope) w)))

(defn make-buffer-sql [env buff] 
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
         (make-buffer-sql envelope buff)
         (make-spatial-query table))])))

(defn render-tile [table envelope]
  "Return an image for all features of :table in the bounds of envelope"
  (str (count (get-tile-features table envelope 10)))) ;;TODO: Symbology width buffer

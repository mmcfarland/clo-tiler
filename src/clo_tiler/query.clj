(ns clo-tiler.query
  (:require [clojure.java.jdbc :as sql])
  (:import (com.vividsolutions.jts.geom Envelope Point Coordinate)))

(def conn "postgresql://localhost:5432/tyler")

(defn- make-buffer-sql [buff env] 
  "Buffer envelope sql clause. Assumemes webm srid"
  (format "ST_Buffer(ST_MakeEnvelope(%f,%f,%f,%f, 3857), %s)"
    (.getMinX env) (.getMinY env) (.getMaxX env) (.getMaxY env) buff))

(defn- make-spatial-query [buff-sql table]
  "Generate spatial query to select buffered bbox features"
  (format "SELECT ST_AsBinary(ST_Intersection(geom, %s)) AS wkb FROM %s WHERE ST_Intersects(geom, %s);"
          buff-sql table buff-sql))
          
(defn get-tile-features [table envelope buff]
  "Gets list of WKB bytes for features in :table with a bbox 
   of :envelope buffered by :buff pixels"
  (into [] 
      (sql/query conn [(->
         (make-buffer-sql buff envelope)
         (make-spatial-query table))])))

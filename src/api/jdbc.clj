(ns api.jdbc
  (:require
   [clojure.java.jdbc :as jdbc]
   [clj-time.coerce :as time-coerce]
   [clojure.data.json :as json])
  (:import
   [org.postgresql.util PGobject]))

;; Disclaimer: there is probably a cleaner way to set all this up probably via a library
;; but I couldn't any that supported exactly these extensions specifically to convert
;; clojure data to and from SQL

;; support (de)serializing postgres jsonb column
;; based on https://gist.github.com/zelark/3b484e9b16ad55c97b4ed6f6ea13986b
(defn- ->pg-object [^String type ^String value]
  (doto (PGobject.)
    (.setType type)
    (.setValue value)))

(defn- clj->jsonb-pg-object [x]
  (->pg-object "jsonb"
               (if (instance? clojure.lang.IPersistentMap x)
                 (json/write-str x)
                 x)))

(defn- jsonb-pg-object->clj [^PGobject pg-obj]
  (json/read-str (.getValue pg-obj)))

;; to SQL
(extend-protocol jdbc/IResultSetReadColumn PGobject
                 (result-set-read-column [pg-obj _ _]
                   (case (.getType pg-obj)
                     "jsonb" (jsonb-pg-object->clj pg-obj)
                     pg-obj)))

;; from SQL
(extend-protocol jdbc/ISQLParameter clojure.lang.IPersistentMap
                 (set-parameter [^clojure.lang.IPersistentMap v ^clojure.lang.IPersistentMap stmt ^long idx]
                   (.setObject stmt idx (clj->jsonb-pg-object v))))

;; support (de)serializing postgres jdbc timestamps
;; based on http://clojure.github.io/java.jdbc/#clojure.java.jdbc/IResultSetReadColumn
;; to SQL
(extend-protocol jdbc/IResultSetReadColumn java.sql.Timestamp
                 (result-set-read-column [col _ _]
                   (-> col
                       (time-coerce/from-sql-time)
                       (time-coerce/to-string))))

;; from SQL
(extend-protocol jdbc/ISQLValue org.joda.time.DateTime
                 (sql-value [v] (time-coerce/to-sql-time v)))

;; automatically convert between Clojure vectors and SQL arrays by extending two protocols
;; ref: https://stackoverflow.com/a/25786990/1251467
;; to SQL
(extend-protocol clojure.java.jdbc/ISQLParameter clojure.lang.IPersistentVector
                 (set-parameter [v ^java.sql.PreparedStatement stmt ^long i]
                   (let [conn (.getConnection stmt)
                         meta (.getParameterMetaData stmt)
                         type-name (.getParameterTypeName meta i)]
                     (if-let [elem-type (when (= (first type-name) \_) (apply str (rest type-name)))]
                       (.setObject stmt i (.createArrayOf conn elem-type (to-array v)))
                       (.setObject stmt i v)))))
;; from SQL
(extend-protocol clojure.java.jdbc/IResultSetReadColumn java.sql.Array
                 (result-set-read-column [val _ _]
                   (into [] (.getArray val))))

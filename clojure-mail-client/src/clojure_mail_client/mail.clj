(ns clojure-mail-client.mail
  (:import (javax.mail Session Folder Flags)
           (javax.mail.search FlagTerm)
           (javax.mail Flags$Flag)
           (java.util Properties)))

(defn as-str
  ([] "")
  ([x] (if (instance? clojure.lang.Named x)
         (name x)
         (str x)))
  ([x & ys]
     ((fn [^StringBuilder sb more]
        (if more
          (recur (. sb  (append (as-str (first more)))) (next more))
          (str sb)))
      (new StringBuilder ^String (as-str x)) ys)))

; Not there is no corresponding props->map. Just destructure!
(defn ^Properties as-properties
  "Convert any seq of pairs to a java.utils.Properties instance.
   Uses as-str to convert both keys and values into strings."
  {:tag Properties}
  [m]
  (let [p (Properties.)]
    (doseq [[k v] m]
      (.setProperty p (as-str k) (as-str v)))
    p))

(defn store [protocol server user pass]
  (let [p (as-properties [["mail.store.protocol" protocol]])]
    (doto (.getStore (Session/getDefaultInstance p) protocol)
      (.connect server user pass))))


(defn folders 
  ([s] (folders s (.getDefaultFolder s)))
  ([s f]
     (let [sub? #(if (= 0 (bit-and (.getType %) 
                                   Folder/HOLDS_FOLDERS)) false true)]
       (map #(cons (.getName %) (if (sub? %) (folders s %))) (.list f)))))


(defn messages [s fd & opt]
  (let [fd (doto (.getFolder s fd) (.open Folder/READ_ONLY))
        [flags set] opt
        msgs (if opt 
               (.search fd (FlagTerm. (Flags. flags) set)) 
               (.getMessages fd))]
    (map #(vector (.getUID fd %) %) msgs)))


(defn dump [msgs]
  (doseq [[uid msg] msgs]
    (.writeTo msg (java.io.FileOutputStream. (str uid)))))

(defn escape-string [x]
  (clojure.string/replace x #"^[':\\]" "\\\\$0"))

(defn code-to-json [x]
  "jsonify any clojure data"
  (condp #(%1 %2) x
    number?  x
    symbol?  (str \' (name x))
    keyword? (str \: (name x))
    string?  (escape-string x)
    list?    (into [] (cons "list"   (map code-to-json x)))
    vector?  (into [] (cons "vector" (map code-to-json x)))
    set?     (into [] (cons "set"    (map code-to-json x)))
    map?     (into {} (map #(mapv code-to-json %) x))
    (throw (Exception. (format "Unsupported type: %s" (type x))))))

(defn json [msgs]
  "jsonify a list of IMAPMessages"
  (doseq [[uid msg] msgs]
    (code-to-json (bean msg))))

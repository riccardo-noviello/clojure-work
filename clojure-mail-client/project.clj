(defproject clojure-mail-client "0.0.1-SNAPSHOT"
  :description "FIXME: write description"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [javax.mail/mail "1.4.7"]
                 ]
  :javac-options ["-target" "1.6" "-source" "1.6" "-Xlint:-options"]
  :aot [clojure-mail-client.core]
  :main clojure-mail-client.core)

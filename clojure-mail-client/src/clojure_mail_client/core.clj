(ns clojure-mail-client.core
  (:import  (javax.mail Session Folder Flags)
            (javax.mail.search FlagTerm)
            (javax.mail Flags$Flag))
  (:require [clojure-mail-client.mail :as mail])
  (:gen-class))


(def gmail (mail/store "imaps" "imap.gmail.com" 
                  "riccardo.noviello@gmail.com" "password"))

(defn -main
  []
  (println "ready")
  
  ; prints the folders in my gmail account
  (println (mail/folders gmail))
    
  ; prints the last 3 messages from the INBOX folder
  (println (take 3 (mail/messages gmail "INBOX")))
  
  ; prints the last 5 messages from the flag UNIVERSIDAD
  (take 5 (mail/messages gmail "UNIVERSIDAD" Flags$Flag/SEEN false))
  
  ; json of the last 3 messages from the INBOX folder
  (mail/json (take 3 (mail/messages gmail "INBOX")))
  )

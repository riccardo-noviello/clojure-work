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
  
  (println (mail/folders gmail))
    
  (println (take 3 (mail/messages gmail "INBOX")))
  
  (take 5 (mail/messages gmail "UNIVERSIDAD" Flags$Flag/SEEN false))
  
  ;(mail/dump (take 3 (mail/messages gmail "INBOX")))
  
  (mail/json (take 3 (mail/messages gmail "INBOX")))
  )

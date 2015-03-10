(ns sheetdb.poller
  (:require [overtone.at-at :refer :all]
            [feedparser-clj.core :as feed]
            [clj-time.core :as t])
  (:import (java.util Date)))

(def sheet-key "1vAb70Ti_hMVyxlxgnMNsj5YQhTy4S93L2wscFslmE5w")

(defn- url [key]
  (str "https://spreadsheets.google.com/feeds/list/" key "/od6/public/values?alt=json"))

(defn- atom-feed [key]
  (str "https://spreadsheets.google.com/feeds/list/" key "/od6/public/values?alt\\u003djson"))
"https://spreadsheets.google.com/feeds/list/1vAb70Ti_hMVyxlxgnMNsj5YQhTy4S93L2wscFslmE5w/od6/public/values?alt\\u003djson"
(def url (url sheet-key))
(def atom-url (atom-feed sheet-key))

;(def my-pool (mk-pool))
;(at (+ 1000 (now)) #(println "hello from the past!") my-pool)
;(every 1000 #(println "I am cool!") my-pool)
;(stop 2 my-pool)
;(stop-and-reset-pool! my-pool)
;(def tp (mk-pool))
;(after 10000 #(println "hello") tp :desc "Hello printer")
;(every 5000 #(println "I am still alive!") tp :desc "Alive task")
;(show-schedule tp)
;(stop 2 tp)
;(stop-and-reset-pool! tp)

; atom feed as a map
(def feed (feed/parse-feed atom-url))

(def last-updated (atom {:date 0}))

(defn- feed-last-updated [feed]
  ((first (feed :entries)) :updated-date))

; want to schedule a poller to check the feed every 5 minutes for updates
(defn check-for-update [atom-url]
  (let [feed (feed/parse-feed atom-url)
        feed-updated (.getTime (feed-last-updated feed))]
    (if (> feed-updated (@last-updated :date))
      (do
        (swap! last-updated assoc :date feed-updated)
        (println "found an update!")))))

; schedule:
(defn start-poller [pool t]
  (every t #(check-for-update atom-url) pool))

(def pool (mk-pool))
(start-poller pool 10000)
(stop 1 pool)

;;
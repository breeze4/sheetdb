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
;"https://spreadsheets.google.com/feeds/list/1vAb70Ti_hMVyxlxgnMNsj5YQhTy4S93L2wscFslmE5w/od6/public/values?alt\\u003djson"

(def url (url sheet-key))
(def atom-url (atom-feed sheet-key))

(def last-updated (atom {atom-url {:date 0}}))

(defn- feed-last-updated [feed]
  ((first (feed :entries)) :updated-date))

; want to schedule a poller to check the feed every 5 minutes for updates
(defn- check-for-update [atom-url]
  (let [feed (feed/parse-feed atom-url)
        feed-updated (.getTime (feed-last-updated feed))]
    (println "polling...")
    (if (> feed-updated (@last-updated :date))
      (do
        (swap! last-updated assoc-in [atom-url :date] feed-updated)
        (println "found an update!")))))

; schedule:
(defn start-poller
  ([pool] (every 60000 #(check-for-update atom-url) pool))
  ([pool t] (every t #(check-for-update atom-url) pool)))

(def pool (atom {:pool (mk-pool)}))
(start-poller (@pool :pool))
(stop 2 (@pool :pool))

;;
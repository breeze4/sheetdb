(ns sheetdb.poller
  (:require [overtone.at-at :refer :all]
            [feedparser-clj.core :as feed]
            [clojure.core.async
             :refer [>! <! >!! <!! put! take! go go-loop chan buffer close! thread
                     alts! alts!! timeout onto-chan pipeline]]
            [clj-time.core :as t])
  (:import (java.util Date)))



(defn last-updated [feed-id] (atom {feed-id {:date 0}}))

(defn- feed-last-updated [feed]
  ((first (feed :entries)) :updated-date))

; want to schedule a poller to check the feed every 5 minutes for updates
; would be nice to not have the synchronous call to feed/parse-feed here
; could put it on a channel and take it once its available
; experiment with (thread..) macro
; what does let binding form do with a (thread) macro?

; simulate the external service call
;(def entries (feed/parse-feed atom-url))
(defn get-val []
  (do
    (Thread/sleep 2000)
    {:key "value"}))

(defn- check-for-update [<out atom-url]
  (let [feed (<!! (thread (feed/parse-feed atom-url)))
        feed-updated (.getTime (feed-last-updated feed))
        entries (feed :entries)]
    (println "polling...")
    (if (> feed-updated (get-in @last-updated [atom-url :date]))
      (do
        (swap! last-updated assoc-in [atom-url :date] feed-updated)
          (do
            (prn "found an update!" entries)
            (onto-chan <out entries) false)))))

; schedule:
(defn start-poller
  ([feed-url out-ch pool] (start-poller feed-url out-ch pool 60000))
  ([feed-url out-ch pool t] (every t #(check-for-update out-ch feed-url) pool)))

(defn start-listener [<ch]
  (go-loop []
    (let [x (<! <ch)]
      (if-not (nil? x)
        (do
          (prn x)
          (recur))))))

; create and return pool
(def pool (atom {:pool (mk-pool)}))
;(def f (feed/parse-feed atom-url))
(def c (chan))
;(start-poller (@pool :pool) 5000)
;(stop 1 (@pool :pool))

;(assoc {}
;  :value ((first ((first (f :entries)) :contents)) :value)
;  :title ((first (f :entries)) :title))

;;
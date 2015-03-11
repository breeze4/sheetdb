(ns sheetdb.subscribe
  (:require [clj-http.client :as client]
            [clojure.data.json :as json]
            [clojure.core.async
             :refer [>! <! >!! <!! put! take! go chan buffer close! thread
                     alts! alts!! timeout]]
            [feedparser-clj.core :as feed]))

(def sheet-key "1vAb70Ti_hMVyxlxgnMNsj5YQhTy4S93L2wscFslmE5w")

(defn- url [key]
  (str "https://spreadsheets.google.com/feeds/list/" key "/od6/public/values?alt=json"))

(defn- get-data
  "[sheet-key] Gets the entire spreadsheet with the given key"
  [sheet-key]
  (client/get (url sheet-key)))

(def data (json/read-str ((get-data sheet-key) :body)))

(def entries ((data "feed") "entry"))

(defn- callback [payload]
  (prn "callback: " payload))

(def <entries (chan))

(defn put-entry []
  (go (>! <entries (entries 1)))
  (prn "just put on, didn't registered callback"))

; start poller, when the poller sees there is an update to the feed, it calls
; a function
(defn subscribe [feed-url])
;(put-entry)

;(go (callback (<! <entries)))
;(prn "registered callback")
;(close! <entries)

;;
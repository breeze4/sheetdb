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

; list of the entries
; [{"gsx$name" {"$t" "sarah conner"},
;  "gsx$rank" {"$t" "chief survivor"},
;  "id" {"$t" "https://spreadsheets.google.com/feeds/list/1vAb70Ti_hMVyxlxgnMNsj5YQhTy4S93L2wscFslmE5w/od6/public/values/cokwr"},
;  "gsx$id" {"$t" "1"}},
;  {"gsx$name" {"$t" "john conner"},
;   "gsx$rank" {"$t" "robot killer"},
;   "id" {"$t" "https://spreadsheets.google.com/feeds/list/1vAb70Ti_hMVyxlxgnMNsj5YQhTy4S93L2wscFslmE5w/od6/public/values/cpzh4"},
;   "gsx$id" {"$t" "2"}}]
; want:
;{"https://spreadsheets.google.com/feeds/list/1vAb70Ti_hMVyxlxgnMNsj5YQhTy4S93L2wscFslmE5w/od6/public/values/cokwr"
; {"gsx$name" {"$t" "sarah conner"},
;  "gsx$rank" {"$t" "chief survivor"},
;  "id" {"$t" "https://spreadsheets.google.com/feeds/list/1vAb70Ti_hMVyxlxgnMNsj5YQhTy4S93L2wscFslmE5w/od6/public/values/cokwr"},
;  "gsx$id" {"$t" "1"}}
; "https://spreadsheets.google.com/feeds/list/1vAb70Ti_hMVyxlxgnMNsj5YQhTy4S93L2wscFslmE5w/od6/public/values/cpzh4"
; {"gsx$name" {"$t" "john conner"},
;  "gsx$rank" {"$t" "robot killer"},
;  "id" {"$t" "https://spreadsheets.google.com/feeds/list/1vAb70Ti_hMVyxlxgnMNsj5YQhTy4S93L2wscFslmE5w/od6/public/values/cpzh4"},
;  "gsx$id" {"$t" "2"}}}
(def entries ((data "feed") "entry"))

(defn- callback [payload]
  (prn "callback: " payload))

(def <entries (chan))

(defn put-entry []
  (go (>! <entries (entries 1)))
  (prn "just put on, didn't registered callback"))

(put-entry)

(go (callback (<! <entries)))
(prn "registered callback")
(close! <entries)

;;
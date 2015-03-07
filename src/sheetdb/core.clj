(ns sheetdb.core
  (:require [clj-http.client :as client]
            [clojure.set :as set]
            [clojure.core :refer [keyword]]
            [clojure.data.json :as json]
            [feedparser-clj.core :as feed]))

(def sheet-key "1vAb70Ti_hMVyxlxgnMNsj5YQhTy4S93L2wscFslmE5w")

(defn- url [key]
  (str "https://spreadsheets.google.com/feeds/list/" key "/od6/public/values?alt=json"))

(feed/parse-feed "https://spreadsheets.google.com/feeds/list/1vAb70Ti_hMVyxlxgnMNsj5YQhTy4S93L2wscFslmE5w/od6/public/values?alt\\u003djson")

(defn- get-data
  "[sheet-key] Gets the entire spreadsheet with the given key"
  [sheet-key]
  (client/get (url sheet-key)))

(def data (json/read-str ((get-data "1vAb70Ti_hMVyxlxgnMNsj5YQhTy4S93L2wscFslmE5w") :body)))

; list of the entries
; [{"gsx$name" {"$t" "sarah conner"},
;  "gsx$rank" {"$t" "chief survivor"},
;  "id" {"$t" "https://spreadsheets.google.com/feeds/list/1vAb70Ti_hMVyxlxgnMNsj5YQhTy4S93L2wscFslmE5w/od6/public/values/cokwr"},
;  "gsx$id" {"$t" "1"}},
;  {"gsx$name" {"$t" "john conner"},
;   "gsx$rank" {"$t" "robot killer"},
;   "id" {"$t" "https://spreadsheets.google.com/feeds/list/1vAb70Ti_hMVyxlxgnMNsj5YQhTy4S93L2wscFslmE5w/od6/public/values/cpzh4"},
;   "gsx$id" {"$t" "2"}}]
(def entries ((data "feed") "entry"))
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

; this gets the set of all row names - is that correct? maybe correct and not efficient
; an alternative way and much simpler and predictably incorrect is to use the first entry in the list
; of keys
(defn- col-names [ents] (seq (set (flatten (map #(keys %) ents)))))

(defn- get-keys [row]
  (keys (first row)))

(def gsx-prefix "gsx$")
(defn- user-col? [col-name]
  (if (.contains col-name gsx-prefix)
    ; return the name of the column or nil if not contained
    ; consumer can do a when-let, I think? if-let?
    true
    false))

(defn- get-id [ent]
  ((ent "id") "$t"))

(defn- user-col-keys
  "returns set of column names as strings"
  [row] (filter user-col? (keys row)))

(defn- user-col-keywords
  "returns set of column names as keywords"
  [row] (map #(keyword %) (user-col-keys row)))

(defn convert-row [ent]
  (let [; get the url as an id; could construct a unique ID but this is fine for now
        key (get-id ent)
        ; get a lazy sequence of the user columns, ignore the other columns
        cols (seq (user-col-keys ent))
        ; filtering which values to pluck out of the sheet by which values are user entered
        vs (map #(ent %) cols)]
    ; create a map of the key to the user values
    (assoc {} key (zipmap cols vs))))

(defn convert-rows [ents]
  (into {} (map #(convert-row %) ents)))


;;
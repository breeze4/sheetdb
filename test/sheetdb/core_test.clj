(ns sheetdb.core-test
  (:require [clojure.test :refer :all]
            [sheetdb.conversion :refer :all]))

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

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 0 1))))


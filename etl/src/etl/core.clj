(ns etl.core
  (:require [pigpen.core :as pig]
            [pigpen.fold :as fold]
            [clojure-csv.core :as csv]))



(def headers-tickets ["ITIN_ID","COUPONS","QUARTER","ORIGIN","ORIGIN_AIRPORT_ID","ORIGIN_AIRPORT_SEQ_ID","ORIGIN_CITY_MARKET_ID","ORIGIN_COUNTRY","ORIGIN_STATE_ABR","ORIGIN_WAC","ITIN_YIELD","PASSENGERS","ITIN_FARE","DISTANCE","DISTANCE_GROUP","MILES_FLOWN","ITIN_GEO_TYPE","TRASH"])
(def headers-coupons ["ITIN_ID","MKT_ID","SEQ_NUM","COUPONS","YEAR","ORIGIN_AIRPORT_ID","ORIGIN_AIRPORT_SEQ_ID","ORIGIN_CITY_MARKET_ID","QUARTER","ORIGIN","ORIGIN_STATE_ABR","ORIGIN_STATE_NM","ORIGIN_WAC","DEST_AIRPORT_ID","DEST_AIRPORT_SEQ_ID","DEST_CITY_MARKET_ID","DEST","DEST_COUNTRY","DEST_STATE_ABR","DEST_STATE_NM","DEST_WAC","COUPON_TYPE","REPORTING_CARRIER","PASSENGERS","FARE_CLASS","DISTANCE","ITIN_GEO_TYPE","COUPON_GEO_TYPE","TRASH"])

(defn load-csv
  [headers name]
  (->> (pig/load-string name)
       (pig/map (fn [line] (-> (csv/parse-csv line) first)))
       (pig/map (fn [line]
                (apply hash-map (interleave (map keyword headers) line))))))


(defn project
  [headers rel]
  (->> rel
   (pig/map (fn [line] (select-keys line headers)))))


(def joined (let [tickets (load-csv headers-tickets "../10_tickets.csv")
                  coupons (load-csv headers-coupons "../10_coupons.csv")
                  projected-tickets (->> tickets (project [:ITIN_ID :COUPONS :ITIN_FARE :DISTANCE]))
                  projected-coupons (->> coupons (project [:ITIN_ID :MKT_ID :SEQ_NUM :ORIGIN :DEST :YEAR :QUARTER]))]
              (->> (pig/join [(projected-tickets :on :ITIN_ID)
                              (projected-coupons :on :ITIN_ID :type :optional)]
                             merge))))

(->> joined
     (pig/group-by (fn [line] (select-values line [:ITIN_ID :MKT_ID]))
                {:fold (fold/juxt
                        (->> (fold/map :ORIGIN) (fold/first))
                        (->> (fold/map :DEST) (fold/first)))})
     pig/dump)

(ns urbandictionary.malli-env.core-test
  (:require
   [clojure.test :refer [deftest is deftest]]
   [urbandictionary.malli-env.core :refer [->kebab env env*]]))

(deftest ->kebab-test (is (= :asdf-1234 (->kebab "ASDF_1234"))))

(deftest simple-test-test
  (is (= [:valid {:asdf-zxcv "zxcv"}] (env* [:map [:asdf-zxcv :string]] {"ASDF_ZXCV" "zxcv"}))))

(deftest keyword-test
  (is (= [:valid {:hello :one}] (env* [:map [:hello [:and :keyword [:enum :one :two]]]] {"HELLO" "one"}))))

(deftest removes-unrecognized-keys-test
  (is (= [:valid {}] (env* [:map [:asdf {:optional true} :string]] {"HHHH" "000"}))))

(deftest decode-ints-test (is (= [:valid {:asdf 1234}] (env* [:map [:asdf :int]] {"ASDF" "1234"}))))

(deftest decode-booleans-test
  (is (= [:valid {:asdf true}] (env* [:map [:asdf :boolean]] {"ASDF" "true"})))
  (is (= [:valid {:asdf true}] (env* [:map [:asdf :boolean]] {"ASDF" "TrUE"})))
  (is (= [:valid {:asdf false}] (env* [:map [:asdf :boolean]] {"ASDF" "FaLSe"}))))

(deftest defaults-test (is (= [:valid {:asdf 555}] (env* [:map [:asdf {:default 555} :int]] {}))))

(deftest defaults2-test (is (= [:valid {}] (env* [:map [:asdf {:optional true} :int]] {}))))

(deftest range-test
  (is (re-find #"should be larger than 5" (second (env* [:map [:xxx [:and :int [:> 5]]]] {"XXX" 1})))))

(deftest regex-test
  (let [[result message] (env* [:map [:asdf #"A"] [:zxcv #"B"]] {"ASDF" "C" "ZXCV" "~~B~~"})]
    (is (= :invalid result))
    (is (re-find #"should match regex" message))))

(deftest boolean-test
  (let [[result message] (env* [:map [:flag1 :boolean] [:flag2 :boolean] [:flag3 :boolean]
                                [:flag4 :boolean] [:flag5 :boolean]]
                               {"FLAG1" "true" "FLAG2" "false" "FLAG3" "0" "FLAG4" "1" "FLAG5" nil})]
    (is (= :invalid result))
    (is (re-find #"flag3.+?should be a boolean" message))
    (is (re-find #"flag4.+?should be a boolean" message))
    (is (re-find #"flag5.+?should be a boolean" message))))

(deftest missing-key-test
  (let [[result message] (env* [:map [:asdf :int]] {"ZXCV" 999})]
    (is (= :invalid result))
    (is (re-find #"asdf.+missing required key" message))))

(deftest valid-env-test
  (let [exit-code (atom nil)]
    (is (= "" (with-out-str (env [:map [:param-1 :string]] {"PARAM_1" "value1"} #(reset! exit-code %)))))
    (is (nil? @exit-code))))

(deftest invalid-env-test
  (let [exit-code (atom nil)]
    (is (re-find #":param1.+missing required key"
                 (with-out-str (env [:map [:param1 :string]] {} #(reset! exit-code %)))))
    (is (= 1 @exit-code))))
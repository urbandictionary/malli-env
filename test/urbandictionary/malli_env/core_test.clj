(ns urbandictionary.malli-env.core-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [urbandictionary.malli-env.core :refer [->kebab env env*]]))

(deftest ->kebab-test (is (= :asdf-1234 (->kebab "ASDF_1234"))))

(deftest env*-test
  (testing "simple test"
           (is (= [:valid {:asdf-zxcv "zxcv"}] (env* [:map [:asdf-zxcv :string]] {"ASDF_ZXCV" "zxcv"}))))
  (testing "keyword"
           (is (= [:valid {:hello :one}]
                  (env* [:map [:hello [:and :keyword [:enum :one :two]]]] {"HELLO" "one"}))))
  (testing "removes unrecognized keys"
           (is (= [:valid {}] (env* [:map [:asdf {:optional true} :string]] {"HHHH" "000"}))))
  (testing "decode ints" (is (= [:valid {:asdf 1234}] (env* [:map [:asdf :int]] {"ASDF" "1234"}))))
  (testing "decode booleans"
           (is (= [:valid {:asdf true}] (env* [:map [:asdf :boolean]] {"ASDF" "true"})))
           (is (= [:valid {:asdf true}] (env* [:map [:asdf :boolean]] {"ASDF" "TrUE"})))
           (is (= [:valid {:asdf false}] (env* [:map [:asdf :boolean]] {"ASDF" "FaLSe"}))))
  (testing "defaults" (is (= [:valid {:asdf 555}] (env* [:map [:asdf {:default 555} :int]] {}))))
  (testing "defaults" (is (= [:valid {}] (env* [:map [:asdf {:optional true} :int]] {}))))
  (testing "range"
           (is (re-find #"should be larger than 5"
                        (second (env* [:map [:xxx [:and :int [:> 5]]]] {"XXX" 1})))))
  (testing "regex"
           (let [[result message] (env* [:map [:asdf #"A"] [:zxcv #"B"]] {"ASDF" "C" "ZXCV" "~~B~~"})]
             (is (= :invalid result))
             (is (re-find #"should match regex" message))))
  (testing "boolean"
           (let [[result message] (env* [:map [:flag1 :boolean] [:flag2 :boolean] [:flag3 :boolean]
                                         [:flag4 :boolean] [:flag5 :boolean]]
                                        {"FLAG1" "true" "FLAG2" "false" "FLAG3" "0" "FLAG4" "1" "FLAG5" nil})]
             (is (= :invalid result))
             (is (re-find #"flag3.+?should be a boolean" message))
             (is (re-find #"flag4.+?should be a boolean" message))
             (is (re-find #"flag5.+?should be a boolean" message))))
  (testing "missing key"
           (let [[result message] (env* [:map [:asdf :int]] {"ZXCV" 999})]
             (is (= :invalid result))
             (is (re-find #"asdf.+missing required key" message)))))

(deftest valid-env-test
  (let [exit-code (atom nil)]
    (is (= "" (with-out-str (env [:map [:param-1 :string]] {"PARAM_1" "value1"} #(reset! exit-code %)))))
    (is (nil? @exit-code)))
  )

(deftest invalid-env-test
  (let [exit-code (atom nil)]
    (is (re-find #":param1.+missing required key"
                 (with-out-str (env [:map [:param1 :string]] {} #(reset! exit-code %)))))
    (is (= 1 @exit-code))))
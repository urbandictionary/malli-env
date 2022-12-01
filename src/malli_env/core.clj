(ns malli-env.core
  (:require [clojure.string :as str]
            [malli.core :as m]
            [malli.error :as me]
            [malli.transform :as mt]))

(def ->kebab (comp keyword #(str/replace % #"_" "-") str/lower-case))

(def transformer
  (mt/transformer (mt/key-transformer {:decode ->kebab})
                  (mt/transformer
                    {:decoders {:boolean #(some-> %
                                                  str/lower-case)},
                     :name :downcase-boolean})
                  mt/string-transformer
                  mt/default-value-transformer
                  mt/strip-extra-keys-transformer))

(defn env*
  [schema input]
  (let [decoded (m/decode schema input transformer)]
    (if (m/validate schema decoded)
      [:valid decoded]
      [:invalid (pr-str (me/humanize (m/explain schema decoded)))])))

(defn env
  "Return parsed environment variables, or exit with an error message if invalid"
  ([schema] (env schema (into {} (System/getenv)) #(System/exit %)))
  ([schema input exit-fn]
   (let [[result output] (env* schema input)]
     (case result
       :valid output
       :invalid (do (println output) (exit-fn 1))))))

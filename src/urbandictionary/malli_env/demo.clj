(ns urbandictionary.malli-env.demo
  (:gen-class)
  (:require
   [urbandictionary.malli-env.core :as cli]))

(defn -main
  []
  (let [env (cli/env [:map [:param1 :int] [:param2 {:optional true} :string]
                      [:param3 {:default "hello"} :string]])]
    (println "Success!" (pr-str env))))
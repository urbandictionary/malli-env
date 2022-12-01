(defproject com.urbandictionary/malli-env "0.1.2"
  :description "Get environment variables with malli validation and coercion"
  :url "https://github.com/urbandictionary/malli-env"
  :profiles {:dev {:dependencies [[metosin/malli "0.8.4"]]},
             :provided {:dependencies [[metosin/malli "0.8.4"]]}}
  :license {:name "Eclipse Public License",
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :deploy-repositories [["clojars"
                         {:password :env/clojars_password,
                          :sign-releases false,
                          :url "https://repo.clojars.org",
                          :username :env/clojars_username}]]
  :dependencies [[org.clojure/clojure "1.10.0"]])

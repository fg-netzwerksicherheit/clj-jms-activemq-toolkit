(defproject fg-netzwerksicherheit/clj-jms-activemq-toolkit "1.99.0"
  :description "Toolkit for using the ActiveMQ JMS implementation in Clojure."
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/tools.cli "0.2.4"]
                 [com.ning/compress-lzf "1.0.0"]
                 [org.xerial.snappy/snappy-java "1.0.4.1"]
                 [com.esotericsoftware.kryo/kryo "2.22"]
                 [org.apache.activemq/activemq-broker "5.11.1"]
                 [org.apache.activemq/activemq-client "5.11.1"]
                 [org.apache.activemq/activemq-openwire-legacy "5.11.1"]
                 [org.slf4j/slf4j-simple "1.7.10"]
                 [clj-assorted-utils "1.8.0"]
                 [cheshire "5.4.0"]]
  :jvm-opts["-Djava.net.preferIPv4Stack=true"]
  :license {:name "Eclipse Public License (EPL) - v 1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"
            :distribution :repo
            :comments "This is the same license as used for Clojure."}
  :global-vars {*warn-on-reflection* true}
  :aot :all
  :java-source-paths ["src-java"]
  :prep-tasks [["compile" "clj-data-exchange.java-interfaces" "clj-jms-activemq-data-exchange.ActiveMqProducer"]
               ["javac" "src-java/clj_jms_activemq_toolkit/PooledBytesMessageProducer.java"]
               ["compile" "clj-jms-activemq-data-exchange.ActiveMqDataExchangeController"] "javac" "compile"]
  :main clj-jms-activemq-toolkit.main
  :plugins [[lein-cloverage "1.0.2"]])

# paubox-clj

A Clojure library designed to interact with PauBox's Rest API

## Usage

(with-paubox "your_api_key" "your_username"
    (-> (Email. "Test-Subject" ["to@test.com"] [] [] "from@test.com" false (Content. "text" "<html><b>html body</b></html>") nil)
        (send-email)
        println))

## License

Copyright © 2018 tyler hoersch

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

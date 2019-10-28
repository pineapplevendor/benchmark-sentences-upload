# nlp-benchmark-utils

A script to upload sentences to dynamo

## Usage

* Update the `path-to-files` in core.clj to match your directory structure  
* Fire up `lein repl` and then `(store-all-sentences files)`

## Prerequisites: 

* You'll need aws credentials set up on whatever box you run this from.
See https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-configure.html
* You'll need to set a dynamo table with a name and partition key matching what's used in the script.



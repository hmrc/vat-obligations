# VAT Obligations Microservice

[![Build Status](https://travis-ci.org/hmrc/vat-obligations.svg)](https://travis-ci.org/hmrc/vat-obligations)
[![Download](https://api.bintray.com/packages/hmrc/releases/vat-obligations/images/download.svg) ](https://bintray.com/hmrc/releases/vat-obligations/_latestVersion)

## Summary
This protected microservice provides a backend for Making Tax Digital for Businesses (MTDfB) VAT frontend services to retrieve VAT Return Obligation details for a user enrolled for MTD VAT.

## Running the application
To run this microservice, you must have SBT installed. You should then be able to start the application using:
                                       
```sbt run```

### Testing
```sbt test it:test```

### Requirements
This service is written in [Scala](http://www.scala-lang.org/) and [Play](http://playframework.com/), so needs at least a [JRE] to run.

## License
This code is open source software licensed under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0.html)
# VAT Obligations Microservice

[![Build Status](https://travis-ci.org/hmrc/vat-obligations.svg)](https://travis-ci.org/hmrc/vat-obligations)
[![Download](https://api.bintray.com/packages/hmrc/releases/vat-obligations/images/download.svg) ](https://bintray.com/hmrc/releases/vat-obligations/_latestVersion)

## Summary
This protected microservice provides a backend for Making Tax Digital for Businesses (MTDfB) VAT frontend services to retrieve VAT Return Obligation details for a user enrolled for MTD VAT.

## Endpoint Definitions (APIs)

**Method**: GET

**Path**: /vat-obligations/:vrn/obligations

Where:

* **:vatNumber** is a valid VRN, for example: "999999999"

**Query Parameters**:

|Query Parameter|Mandatory|Description|Format/Valid Values|    
|-|-|-|-|    
|`from`|**false**|Used to filter the response to only include items from this date|YYYY-MM-DD|    
|`to`|**false**|Used to filter the response to only include items before this date|YYYY-MM-DD|    
|`status`|**false**|Used to filter the response to only include items that have a specific status|`String`|


#### Success Response

##### Obligations object
|Data Item|Type|Mandatory|    
|-|-|-|    
|obligations|`Array[Obligation]`|**true**|

##### Obligation object
|Data Item|Type|Mandatory|  
|-|-|-|  
|start|`String`|**true**|  
|end|`String`|**true**|  
|due|`String`|**true**|  
|status|`String`|**true**|  
|periodKey|`String`|**true**|  
|received|`String`|**false**|

#### Example

Status: OK (200)

Response Body:
```
{    
    "obligations": [
        {
            "status" : "F",    
            "start" : "2017-05-02",    
            "end" : "2017-07-02",    
            "received" : "2017-05-01",
            "due": "2017-07-07",
            "periodKey": "17AB" 
        },
        {
            "status" : "F",    
            "start" : "2018-02-02",    
            "end" : "2018-04-02",    
            "received" : "2018-02-01",
            "due": "2018-04-07",
            "periodKey": "18AB" 
        }
    ]
}
```

## Running the application
To run this microservice, you must have SBT installed. You should then be able to start the application using:

```sbt run```

### Testing
```sbt test it:test```

### Requirements
This service is written in [Scala](http://www.scala-lang.org/) and [Play](http://playframework.com/), so needs at least a [JRE](https://www.java.com/en/download/) to run.

## License
This code is open source software licensed under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0.html)

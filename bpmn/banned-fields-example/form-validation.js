load('classpath:validationResult.js')
load('classpath:validate.min.js')

/*
Available variables:
execution -> returns: org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity
submissionValues -> returns: Returns org.camunda.bpm.engine.variable.impl.VariableMapImpl
*/

/*
Submission Values Access:

submissionValues variable can be accessed as a Map/HashMap and with extra helpers from nashron
submissionValues['myFieldName'] returns the value of the field myFieldName
submissionValues.toString() pretty prints a detailed breakdown of the Map
References:
1. https://github.com/camunda/camunda-commons/blob/master/typed-values/src/main/java/org/camunda/bpm/engine/variable/impl/VariableMapImpl.java
2. https://wiki.openjdk.java.net/display/Nashorn/Nashorn+extensions see: "Java Map keys as properties" section
3. https://github.com/camunda/camunda-commons/blob/master/typed-values/src/main/java/org/camunda/bpm/engine/variable/impl/VariableMapImpl.java

*/

// Used for converting Java objects (such as Maps) into JSON Strings
var JSONObject = Java.type('org.camunda.bpm.engine.impl.util.json.JSONObject')
// var JSONArray = Java.type('org.camunda.bpm.engine.impl.util.json.JSONArray')

var jsonSubmission = JSON.parse(new JSONObject(submissionValues).toString())


// Validate.js Constraints
function getConstraints() {
  var constraints = {
    age: {
      presence: true,
      numericality: {
        onlyInteger: true,
        greaterThan: 18,
        lessThanOrEqualTo: 125,
      }
    }
  };
  return constraints
}

var validation = validate(jsonSubmission, getConstraints())

if (validation === undefined) {
  validationResult(true)
} else {
  validationResult(false, {
                            "detail": "VALIDATE.JS", 
                            "message": JSON.stringify(validation)
                          }
                    )
}

// validationResult(true)
// validationResult(false, {
//                           "detail": "FIELD_REQUIRED", 
//                           "message": "Field ABC123 is Required!!!"
//                         }
//                   )


// -------------------------------------------------------------------------------
// Field Validation Examples
// Custom/Raw validation
// function validateData(values){
// // @TODO Add Validate.js usage example
//   if (values.containsKey('firstName') == false){
//     return validationResult(false, {
//                                      "detail": "FIELD_REQUIRED", 
//                                      "message": "firstName is required"
//                                     })
//   }

//   if (values.containsKey('age') && values['age'] < 18 ){
//     return validationResult(false, {
//                                      "detail": "AGE_LIMIT", 
//                                      "message": "age must be 18 or older"
//                                     })
//   }
  
//   // If no errors were found:
//   return validationResult(true)
// }
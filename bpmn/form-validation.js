//helper for printing to console
var system = java.lang.System
load('classpath:validationResult.js')
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



system.out.println("Submission Values:")
system.out.println(submissionValues.toString())

validateData(submissionValues)
// validationResult(true)
// validationResult(false, {
//                           "detail": "FIELD_REQUIRED", 
//                           "message": "Field ABC123 is Required!!!"
//                         }
//                   )


// -------------------------------------------------------------------------------
// Field Validation Examples

function validateData(values){
// @TODO Add Validate.js usage example
  if (values.containsKey('firstName') == false){
    return validationResult(false, {
                                     "detail": "FIELD_REQUIRED", 
                                     "message": "firstName is required"
                                    })
  }

  if (values.containsKey('age') && values['age'] < 18 ){
    return validationResult(false, {
                                     "detail": "AGE_LIMIT", 
                                     "message": "age must be 18 or older"
                                    })
  }
  
  // If no errors were found:
  return validationResult(true)
}


// -------------------------------------------------------------------------------
// @TODO move this section into a standalone js file that will be 
// loaded through: load('classpath:validationResult.js')

/*
  Generic Function to build final response for validation
*/
// function validationResult(result, validationError) {
//   if (result != true){
//     result = false
//   }
//   // @TODO ensure validationError is a object with detail and message keys
//   if (result == true){
//     return Java.asJSONCompatible({
//       "result": true
//     })
//   } else {
//     return Java.asJSONCompatible({
//       "result": false,
//       "validation_error": {
//         "detail": validationError.detail,
//         "message": validationError.message
//       }
//     })
//   }
// }

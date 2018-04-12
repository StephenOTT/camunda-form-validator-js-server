/*
  Generic Function to build final response for server side Form validation
  Contact: Stephen Russett (Github: StephenOTT)
*/
function validationResult(result, validationError) {
  if (result != true){
    result = false
  }
  // @TODO ensure validationError is a object with detail and message keys
  if (result == true){
    return Java.asJSONCompatible({
      "result": true
    })
  } else {
    return Java.asJSONCompatible({
      "result": false,
      "validation_error": {
        "detail": validationError.detail,
        "message": validationError.message
      }
    })
  }
}
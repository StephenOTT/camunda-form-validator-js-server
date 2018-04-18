load('classpath:validationResult.js')
load('classpath:validate.min.js')
var JSONObject = Java.type('org.camunda.bpm.engine.impl.util.json.JSONObject')

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

// List of fields that are not allowed to be changed
function bannedFields(){
  // could also be loaded from another location (like a yaml or json file)
  return [
    "risk",
    "owner",
    "master_field",
    "current_state",
    "flagged",
    "priority"
  ]
}

// Check if the submission has any of the banned fields
function checkForBannedFields(submission, bannedFields) {
  // Loop through list of banned fields (nashorn loop)
  for each (var field in bannedFields){
    var hasBannedField = validate.contains(submission, field)
    // if a banned field was found then return true:
    if (hasBannedField == true) {
      return true
    }
  }
  // If no banned fields were found:
  return false
}

// if there is a banned field:
if (checkForBannedFields(jsonSubmission, bannedFields())){
  validationResult(false, {
                          "detail": "VALIDATE.JS", 
                          "message": 'Submission contains a banned field: ' + JSON.stringify(bannedFields())
                        }
                  )

// If no banned fields were found:
} else {
  // If no banned fields then continue:
  // Run Validations against Validate.js
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
}


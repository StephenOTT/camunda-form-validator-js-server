package io.digitalstate.camunda;

import org.camunda.commons.utils.IoUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;

import java.io.InputStream;
import java.util.Map;

import org.camunda.bpm.engine.impl.form.validator.FormFieldValidationException;
import org.camunda.bpm.engine.impl.form.validator.FormFieldValidator;
import org.camunda.bpm.engine.impl.form.validator.FormFieldValidatorContext;

import org.camunda.bpm.engine.impl.scripting.engine.ScriptEngineResolver;

// import org.camunda.bpm.model.xml.ModelInstance;
// import org.camunda.bpm.model.xml.instance.ModelElementInstance;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class JsFormValidation implements FormFieldValidator {

  public boolean validate(Object submittedValue, FormFieldValidatorContext validatorContext){
    
    // @TODO add getVariableScope() usage as getExecution is deprecated
    // Must discussion with Camunda devs on building proper interface 
    // for getting the current execution
    DelegateExecution execution = validatorContext.getExecution();

      // ModelInstance modelInstance = execution.getBpmnModelInstance();
      // ModelElementInstance elementInstance = modelInstance.getModelElementById(execution.getCurrentActivityId());
      // var extensionElements = elementInstance.getExtensionElements().getElementsQuery().filterByType(Java.type('org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperties').class).singleResult().getCamundaProperties().toArray()



    // @TODO add dynamic file selection based on configuration
    String fileName = "form-validation.js";

    String processDefinitionId = execution.getProcessDefinitionId();
    String deploymentId = execution.getProcessEngineServices().getRepositoryService().getProcessDefinition(processDefinitionId).getDeploymentId();
    InputStream resource = execution.getProcessEngineServices().getRepositoryService().getResourceAsStream(deploymentId, fileName);

    ScriptEngineManager manager = new ScriptEngineManager();
    ScriptEngineResolver engine = new ScriptEngineResolver(manager);
    ScriptEngine nashornEngine = engine.getScriptEngine("nashorn", true);

    // get access to all form fields submitted in the form submit
    Map<String,Object> submissionValues = validatorContext.getSubmittedValues();
    Bindings bindings = nashornEngine.createBindings();
    bindings.put("submissionValues", submissionValues);
    bindings.put("execution", execution);

    // Holder of result
    // Defaults to False if not value is provided
    Boolean validationReturn;

    try {
      String jsScript = IoUtil.inputStreamAsString(resource);
      Object validationResult = nashornEngine.eval(jsScript, bindings);
      Map<String, Object> validationMap = (Map<String, Object>)validationResult;
      System.out.println("VALIDATION RESULT: " + validationMap.get("result"));

      if (validationMap.get("result").equals(true)) {
        System.out.println("Validation Passed!!!");
        validationReturn = true;

      } else {
        throw new FormFieldValidationException(((Map<String, String>)validationMap.get("validation_error")).get("detail"), 
                                               ((Map<String, String>)validationMap.get("validation_error")).get("message"));
      }
    } catch (ScriptException se) {
      throw new FormFieldValidationException("SCRIPT_ERROR", "SCRIPT ERROR Occured: " + se);
    }
    return validationReturn;
  }
}
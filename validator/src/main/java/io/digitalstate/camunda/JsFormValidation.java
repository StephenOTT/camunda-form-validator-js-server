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

    private final static JsFormValidationLogger LOG = JsFormValidationLogger.LOG;

    static ScriptEngineManager manager = new ScriptEngineManager();
    static ScriptEngineResolver engine = new ScriptEngineResolver(manager);
    static ScriptEngine nashornEngine = engine.getScriptEngine("nashorn", true);

  public boolean validate(Object submittedValue, FormFieldValidatorContext validatorContext){
    
    // @TODO add getVariableScope() usage as getExecution is deprecated
    // Must discussion with Camunda devs on building proper interface 
    // for getting the current execution
    DelegateExecution execution = validatorContext.getExecution();

    // Gets the value of the "validator_file" property or defaults to "[fieldId].js"
    String submissionFieldName = validatorContext.getFormFieldHandler().getId();
    LOG.debug("6", "Submission Field Name: " + submissionFieldName);

    final String fileNameDefault = submissionFieldName + ".js";
    LOG.debug("10", "File Name Default: " + fileNameDefault);

    String fileName = validatorContext.getFormFieldHandler().getProperties().getOrDefault("validator_file", fileNameDefault);
    LOG.debug("5", "Validation File Name: " + fileName);
    
    String processDefinitionId = execution.getProcessDefinitionId();
    String deploymentId = execution.getProcessEngineServices().getRepositoryService().getProcessDefinition(processDefinitionId).getDeploymentId();
    InputStream resource = execution.getProcessEngineServices().getRepositoryService().getResourceAsStream(deploymentId, fileName);

    // get access to all form fields submitted in the form submit
    Map<String,Object> submissionValues = validatorContext.getSubmittedValues();
    LOG.debug("9", "Submission Values: \n" + submissionValues.toString());

    Bindings bindings = nashornEngine.createBindings();
    bindings.put("submissionValues", submissionValues);
    bindings.put("execution", execution);

    // Holder of result
    // Defaults to False if no value was initialized
    Boolean validationReturn;

    try {
      String jsScript = IoUtil.inputStreamAsString(resource);
      Object validationResult = nashornEngine.eval(jsScript, bindings);
      Map<String, Object> validationMap = (Map<String, Object>)validationResult;
      LOG.debug("1", "Validation Result: " + validationMap.get("result"));

      if (validationMap.get("result").equals(true)) {
        LOG.debug("2", "Validation passed");
        validationReturn = true;

      } else {
        LOG.debug("3", "Validation failed");
        throw new FormFieldValidationException(((Map<String, String>)validationMap.get("validation_error")).get("detail"), 
                                               ((Map<String, String>)validationMap.get("validation_error")).get("message"));
      }
    } catch (ScriptException se) {
      LOG.debug("4", "Script exception occured");
      throw new FormFieldValidationException("SCRIPT_ERROR", "SCRIPT ERROR Occured: " + se);
    }

    LOG.debug("7", "Validation Return Result: " + validationReturn);
    return validationReturn;
  }
}
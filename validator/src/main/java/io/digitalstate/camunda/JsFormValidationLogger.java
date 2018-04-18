package io.digitalstate.camunda;

import org.camunda.commons.logging.BaseLogger;

public class JsFormValidationLogger extends BaseLogger {

  public static JsFormValidationLogger LOG = createLogger(
    JsFormValidationLogger.class, "JsFormValidationLogger", "io.digitalstate.camunda", "01"
  );

  public void debug(String debugId, String message) {
    logDebug(debugId, message);
  }

  public void info(String infoId, String message) {
    logInfo(infoId, message);
  }

  public void error(String errorId, String message) {
    logError(errorId, message);
  }
}
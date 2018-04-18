FROM camunda/camunda-bpm-platform:tomcat-7.8.0

# add custom configurations
COPY docker/camunda/conf/ /camunda/conf

# add JS script for RSA encryption
COPY docker/camunda/lib/digitalstate.camunda.js.server.form.validation-0.4.0-SNAPSHOT.jar /camunda/lib/digitalstate.camunda.js.server.form.validation-0.4.0-SNAPSHOT.jar
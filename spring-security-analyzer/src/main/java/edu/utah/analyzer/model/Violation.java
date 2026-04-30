package edu.utah.analyzer.model;

public class Violation {

    private String propertyName;
    private String endpoint;
    private String controllerClass;
    private String methodName;
    private String sourceFile;
    private int lineNumber;
    private String message;
    private String severity;

    public Violation(String propertyName,
                     String endpoint,
                     String controllerClass,
                     String methodName,
                     String sourceFile,
                     int lineNumber,
                     String message,
                     String severity) {
        this.propertyName = propertyName;
        this.endpoint = endpoint;
        this.controllerClass = controllerClass;
        this.methodName = methodName;
        this.sourceFile = sourceFile;
        this.lineNumber = lineNumber;
        this.message = message;
        this.severity = severity;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getControllerClass() {
        return controllerClass;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getSourceFile() {
        return sourceFile;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getMessage() {
        return message;
    }

    public String getSeverity() {
        return severity;
    }

    @Override
    public String toString() {
        return "[" + severity + "] " + endpoint + "\n" +
                "Property: " + propertyName + "\n" +
                "Controller: " + controllerClass + "\n" +
                "Method: " + methodName + "\n" +
                "Location: " + sourceFile + ":" + lineNumber + "\n" +
                "Reason: " + message;
    }
}
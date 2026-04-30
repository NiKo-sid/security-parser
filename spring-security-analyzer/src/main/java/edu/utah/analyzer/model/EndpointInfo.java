package edu.utah.analyzer.model;

import java.util.ArrayList;
import java.util.List;

public class EndpointInfo {

    private String controllerClass;
    private String methodName;
    private String httpMethod;
    private String path;
    private String sourceFile;
    private int lineNumber;

    private List<String> requiredRoles = new ArrayList<>();

    private boolean authenticatedOnly = false;
    private boolean permitAll = false;

    public EndpointInfo(String controllerClass,
                        String methodName,
                        String httpMethod,
                        String path,
                        String sourceFile,
                        int lineNumber) {
        this.controllerClass = controllerClass;
        this.methodName = methodName;
        this.httpMethod = httpMethod;
        this.path = path;
        this.sourceFile = sourceFile;
        this.lineNumber = lineNumber;
    }

    public String getControllerClass() {
        return controllerClass;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }

    public String getSourceFile() {
        return sourceFile;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public List<String> getRequiredRoles() {
        return requiredRoles;
    }

    public boolean isAuthenticatedOnly() {
        return authenticatedOnly;
    }

    public boolean isPermitAll() {
        return permitAll;
    }

    public void addRole(String role) {
        if (role != null && !role.isBlank() && !requiredRoles.contains(role)) {
            requiredRoles.add(role);
        }
    }

    public void setAuthenticatedOnly(boolean authenticatedOnly) {
        this.authenticatedOnly = authenticatedOnly;
    }

    public void setPermitAll(boolean permitAll) {
        this.permitAll = permitAll;
    }

    public String getDisplayName() {
        return httpMethod + " " + path;
    }

    @Override
    public String toString() {
        return httpMethod + " " + path +
                " roles=" + requiredRoles +
                " authenticated=" + authenticatedOnly +
                " permitAll=" + permitAll +
                " controller=" + controllerClass +
                " method=" + methodName +
                " file=" + sourceFile +
                " line=" + lineNumber;
    }
}
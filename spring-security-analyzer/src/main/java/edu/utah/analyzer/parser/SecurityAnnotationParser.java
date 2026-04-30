package edu.utah.analyzer.parser;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import edu.utah.analyzer.model.EndpointInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SecurityAnnotationParser {

    public void applyClassLevelSecurityAnnotations(ClassOrInterfaceDeclaration clazz, EndpointInfo endpoint) {
        parsePreAuthorize(clazz, endpoint);
        parseSecured(clazz, endpoint);
        parsePermitAll(clazz, endpoint);
    }

    public void applyMethodLevelSecurityAnnotations(MethodDeclaration method, EndpointInfo endpoint) {
        parsePreAuthorize(method, endpoint);
        parseSecured(method, endpoint);
        parsePermitAll(method, endpoint);
    }

    private void parsePreAuthorize(ClassOrInterfaceDeclaration clazz, EndpointInfo endpoint) {
        var annotationOpt = clazz.getAnnotationByName("PreAuthorize");

        if (annotationOpt.isEmpty()) {
            return;
        }

        String value = annotationOpt.get().toString();
        applyPreAuthorizeValue(value, endpoint);
    }

    private void parsePreAuthorize(MethodDeclaration method, EndpointInfo endpoint) {
        var annotationOpt = method.getAnnotationByName("PreAuthorize");

        if (annotationOpt.isEmpty()) {
            return;
        }

        String value = annotationOpt.get().toString();
        applyPreAuthorizeValue(value, endpoint);
    }

    private void applyPreAuthorizeValue(String value, EndpointInfo endpoint) {
        if (value.contains("isAuthenticated()")) {
            endpoint.setAuthenticatedOnly(true);
        }

        Pattern hasRolePattern = Pattern.compile("hasRole\\('([A-Za-z0-9_]+)'\\)");
        Matcher hasRoleMatcher = hasRolePattern.matcher(value);

        while (hasRoleMatcher.find()) {
            endpoint.addRole(hasRoleMatcher.group(1));
        }

        Pattern hasAnyRolePattern = Pattern.compile("hasAnyRole\\((.*?)\\)");
        Matcher hasAnyRoleMatcher = hasAnyRolePattern.matcher(value);

        if (hasAnyRoleMatcher.find()) {
            String inside = hasAnyRoleMatcher.group(1);
            String[] parts = inside.split(",");

            for (String part : parts) {
                String role = part.replace("'", "").replace("\"", "").trim();
                if (!role.isEmpty()) {
                    endpoint.addRole(role);
                }
            }
        }
    }

    private void parseSecured(ClassOrInterfaceDeclaration clazz, EndpointInfo endpoint) {
        var annotationOpt = clazz.getAnnotationByName("Secured");

        if (annotationOpt.isEmpty()) {
            return;
        }

        String value = annotationOpt.get().toString();
        applySecuredValue(value, endpoint);
    }

    private void parseSecured(MethodDeclaration method, EndpointInfo endpoint) {
        var annotationOpt = method.getAnnotationByName("Secured");

        if (annotationOpt.isEmpty()) {
            return;
        }

        String value = annotationOpt.get().toString();
        applySecuredValue(value, endpoint);
    }

    private void applySecuredValue(String value, EndpointInfo endpoint) {
        Pattern rolePattern = Pattern.compile("ROLE_([A-Za-z0-9_]+)");
        Matcher matcher = rolePattern.matcher(value);

        while (matcher.find()) {
            endpoint.addRole(matcher.group(1));
        }
    }

    private void parsePermitAll(ClassOrInterfaceDeclaration clazz, EndpointInfo endpoint) {
        if (clazz.isAnnotationPresent("PermitAll")) {
            endpoint.setPermitAll(true);
        }
    }

    private void parsePermitAll(MethodDeclaration method, EndpointInfo endpoint) {
        if (method.isAnnotationPresent("PermitAll")) {
            endpoint.setPermitAll(true);
        }
    }
}
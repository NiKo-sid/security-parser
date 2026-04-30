package edu.utah.analyzer.checks;

import edu.utah.analyzer.model.EndpointInfo;
import edu.utah.analyzer.model.Violation;

import java.util.ArrayList;
import java.util.List;

public class PropertyChecker {

    public List<Violation> checkProperties(List<EndpointInfo> endpoints) {
        List<Violation> violations = new ArrayList<>();

        for (EndpointInfo endpoint : endpoints) {
            checkAdminEndpoints(endpoint, violations);
            checkPublicEndpoints(endpoint, violations);
            checkProtectedEndpoints(endpoint, violations);
        }

        return violations;
    }

    private void checkAdminEndpoints(EndpointInfo endpoint, List<Violation> violations) {
        if (endpoint.getPath().startsWith("/admin")) {
            boolean hasAdminRole = endpoint.getRequiredRoles().contains("ADMIN");

            if (!hasAdminRole) {
                violations.add(new Violation(
                        "AdminEndpointsMustRequireAdminRole",
                        endpoint.getDisplayName(),
                        endpoint.getControllerClass(),
                        endpoint.getMethodName(),
                        endpoint.getSourceFile(),
                        endpoint.getLineNumber(),
                        "Endpoint under /admin does not require ADMIN role.",
                        "HIGH"
                ));
            }
        }
    }

    private void checkPublicEndpoints(EndpointInfo endpoint, List<Violation> violations) {
        if (endpoint.getPath().startsWith("/public")) {
            if (!endpoint.isPermitAll()) {
                violations.add(new Violation(
                        "PublicEndpointsShouldBePermitAll",
                        endpoint.getDisplayName(),
                        endpoint.getControllerClass(),
                        endpoint.getMethodName(),
                        endpoint.getSourceFile(),
                        endpoint.getLineNumber(),
                        "Endpoint under /public is not explicitly marked as permitAll.",
                        "MEDIUM"
                ));
            }
        }
    }

    private void checkProtectedEndpoints(EndpointInfo endpoint, List<Violation> violations) {
        boolean isPublic = endpoint.getPath().startsWith("/public");

        boolean hasNoProtection =
                endpoint.getRequiredRoles().isEmpty() &&
                        !endpoint.isAuthenticatedOnly() &&
                        !endpoint.isPermitAll();

        if (!isPublic && hasNoProtection) {
            violations.add(new Violation(
                    "NonPublicEndpointsShouldNotBeUnprotected",
                    endpoint.getDisplayName(),
                    endpoint.getControllerClass(),
                    endpoint.getMethodName(),
                    endpoint.getSourceFile(),
                    endpoint.getLineNumber(),
                    "Non-public endpoint has no authentication or role restriction.",
                    "HIGH"
            ));
        }
    }
}
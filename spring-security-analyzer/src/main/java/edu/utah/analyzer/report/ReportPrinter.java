package edu.utah.analyzer.report;

import edu.utah.analyzer.model.EndpointInfo;
import edu.utah.analyzer.model.Violation;

import java.util.List;

public class ReportPrinter {

    public void printEndpointReport(List<EndpointInfo> endpoints) {
        System.out.println("\n========== Endpoint Analysis Report ==========");
        System.out.println("Total Endpoints Discovered: " + endpoints.size());
        System.out.println();

        for (EndpointInfo endpoint : endpoints) {
            System.out.println(endpoint.getDisplayName());
            System.out.println("  Controller : " + endpoint.getControllerClass());
            System.out.println("  Method     : " + endpoint.getMethodName());
            System.out.println("  Roles      : " + endpoint.getRequiredRoles());
            System.out.println("  Auth Only  : " + endpoint.isAuthenticatedOnly());
            System.out.println("  Permit All : " + endpoint.isPermitAll());
            System.out.println("  Location   : " + endpoint.getSourceFile() + ":" + endpoint.getLineNumber());
            System.out.println();
        }
    }

    public void printViolationReport(List<Violation> violations) {
        System.out.println("\n========== Violation Report ==========");
        System.out.println("Total Violations: " + violations.size());

        int highCount = 0;
        int mediumCount = 0;
        int lowCount = 0;

        for (Violation violation : violations) {
            switch (violation.getSeverity()) {
                case "HIGH" -> highCount++;
                case "MEDIUM" -> mediumCount++;
                case "LOW" -> lowCount++;
            }
        }

        System.out.println("HIGH   : " + highCount);
        System.out.println("MEDIUM : " + mediumCount);
        System.out.println("LOW    : " + lowCount);
        System.out.println();

        if (violations.isEmpty()) {
            System.out.println("No violations found.");
            return;
        }

        for (Violation violation : violations) {
            System.out.println(violation);
            System.out.println();
        }
    }
}
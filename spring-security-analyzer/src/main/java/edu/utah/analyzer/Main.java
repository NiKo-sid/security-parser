package edu.utah.analyzer;

import edu.utah.analyzer.checks.PropertyChecker;
import edu.utah.analyzer.model.EndpointInfo;
import edu.utah.analyzer.model.Violation;
import edu.utah.analyzer.parser.ControllerParser;
import edu.utah.analyzer.report.ReportPrinter;

import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        System.out.println("Spring Security Analyzer Started");

        String projectPath = "test-spring-app/src/main/java";

        ControllerParser parser = new ControllerParser();
        List<EndpointInfo> endpoints = parser.parseControllers(projectPath);

        PropertyChecker checker = new PropertyChecker();
        List<Violation> violations = checker.checkProperties(endpoints);

        ReportPrinter printer = new ReportPrinter();
        printer.printEndpointReport(endpoints);
        printer.printViolationReport(violations);
    }
}
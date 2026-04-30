# Spring Security Analyzer – Demo README

## Overview

This project is a static analysis tool that detects RBAC (Role-Based Access Control) misconfigurations in Spring Boot applications.

It analyzes source code without running the application and reports:
- Missing role restrictions
- Unprotected endpoints
- Incorrect access levels

---

## Repository Structure

spring-security-analyzer/

src/main/java/edu/utah/analyzer/
- Main.java
- parser/
  - ControllerParser.java
  - SecurityAnnotationParser.java
- model/
  - EndpointInfo.java
  - Violation.java
- checker/
  - PropertyChecker.java
- report/
  - ReportPrinter.java

test-spring-app/
- src/main/java/
  - AdminController.java
  - RequestMappingController.java
  - ClassLevelSecurityController.java
  - ClassLevelAuthController.java

pom.xml

---

## Prerequisites

- Java 21+
- Maven

Check installation:

java -version
mvn -version

---

## Step-by-Step Demo Instructions

1. Build the project

mvn clean install

2. Verify demo input path

Open:

src/main/java/edu/utah/analyzer/Main.java

Ensure this line exists:

String projectPath = "test-spring-app/src/main/java";

This tells the analyzer where to find the demo controllers.

3. Run the analyzer

Using Maven:

mvn exec:java -Dexec.mainClass="edu.utah.analyzer.Main"

OR run Main.java directly from your IDE.

---

## Demo Scenario

The analyzer runs on the included sample Spring Boot project located at:

test-spring-app/

Example controller:

@RestController
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public String profile() {
        return "profile";
    }
}

---

## Expected Output

Endpoint Analysis:

GET /admin/profile
  Controller : AdminController
  Method     : profile
  Roles      : []
  Auth Only  : true
  Permit All : false
  Location   : AdminController.java:24

Violation Report:

[HIGH] GET /admin/profile
Property: AdminEndpointsMustRequireAdminRole
Location: AdminController.java:24
Reason: Endpoint under /admin does not require ADMIN role.

---

## What This Demonstrates

- The analyzer extracts endpoints from Spring controllers
- It parses security annotations
- It detects incorrect RBAC configurations

Specifically:
An /admin endpoint is protected only by authentication instead of requiring ADMIN role, which is flagged as a violation.

---

## Rules Enforced

- /admin/** must require ADMIN role
- /public/** should be explicitly marked as public
- Other endpoints must not be unprotected

---

## Key Features

- AST-based parsing using JavaParser
- Supports @GetMapping, @PostMapping, and @RequestMapping
- Handles class-level and method-level security annotations
- Supports RBAC patterns:
  - hasRole
  - hasAnyRole
  - isAuthenticated
  - @Secured
  - @PermitAll
- Reports include controller, method, file, and line number

---

## Notes for Graders

- No external setup required beyond Java and Maven
- The analyzer runs on the included sample project
- Output is printed directly to the console
- Focus on the /admin/profile violation

---

## Limitations

- Does not analyze SecurityFilterChain
- Does not perform runtime verification
- Designed as a research prototype

---

## Author

Siddiq Khan  
MS Computer Science  

---

## Quick Run (TLDR)

mvn clean install  
mvn exec:java -Dexec.mainClass="edu.utah.analyzer.Main"

Look for the /admin/profile violation in the output.
package edu.utah.analyzer.parser;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import edu.utah.analyzer.model.EndpointInfo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ControllerParser {

    private final SecurityAnnotationParser securityParser = new SecurityAnnotationParser();

    public List<EndpointInfo> parseControllers(String sourceDir) throws IOException {
        List<EndpointInfo> endpoints = new ArrayList<>();

        File folder = new File(sourceDir);
        scanFolder(folder, endpoints);

        return endpoints;
    }

    private void scanFolder(File folder, List<EndpointInfo> endpoints) throws IOException {
        File[] files = folder.listFiles();

        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                scanFolder(file, endpoints);
            } else if (file.getName().endsWith(".java")) {
                CompilationUnit cu = StaticJavaParser.parse(file);

                cu.findAll(ClassOrInterfaceDeclaration.class).forEach(clazz -> {
                    if (clazz.isAnnotationPresent("RestController")) {
                        String controllerName = clazz.getNameAsString();
                        String basePath = extractClassLevelPath(clazz);

                        clazz.getMethods().forEach(method -> {
                            List<EndpointInfo> methodEndpoints =
                                    extractEndpoints(controllerName, basePath, method, file.getName());

                            for (EndpointInfo endpoint : methodEndpoints) {
                                securityParser.applyClassLevelSecurityAnnotations(clazz, endpoint);
                                securityParser.applyMethodLevelSecurityAnnotations(method, endpoint);
                                endpoints.add(endpoint);
                            }
                        });
                    }
                });
            }
        }
    }

    private List<EndpointInfo> extractEndpoints(String controllerName,
                                                String basePath,
                                                MethodDeclaration method,
                                                String sourceFile) {
        List<EndpointInfo> endpoints = new ArrayList<>();
        int lineNumber = method.getBegin().map(position -> position.line).orElse(-1);

        if (method.isAnnotationPresent("GetMapping")) {
            String methodPath = extractPath(method, "GetMapping");
            endpoints.add(new EndpointInfo(
                    controllerName,
                    method.getNameAsString(),
                    "GET",
                    combinePaths(basePath, methodPath),
                    sourceFile,
                    lineNumber
            ));
        }

        if (method.isAnnotationPresent("PostMapping")) {
            String methodPath = extractPath(method, "PostMapping");
            endpoints.add(new EndpointInfo(
                    controllerName,
                    method.getNameAsString(),
                    "POST",
                    combinePaths(basePath, methodPath),
                    sourceFile,
                    lineNumber
            ));
        }

        if (method.isAnnotationPresent("PutMapping")) {
            String methodPath = extractPath(method, "PutMapping");
            endpoints.add(new EndpointInfo(
                    controllerName,
                    method.getNameAsString(),
                    "PUT",
                    combinePaths(basePath, methodPath),
                    sourceFile,
                    lineNumber
            ));
        }

        if (method.isAnnotationPresent("DeleteMapping")) {
            String methodPath = extractPath(method, "DeleteMapping");
            endpoints.add(new EndpointInfo(
                    controllerName,
                    method.getNameAsString(),
                    "DELETE",
                    combinePaths(basePath, methodPath),
                    sourceFile,
                    lineNumber
            ));
        }

        if (method.isAnnotationPresent("RequestMapping")) {
            String methodPath = extractPath(method, "RequestMapping");
            List<String> httpMethods = extractRequestMethods(method);

            if (httpMethods.isEmpty()) {
                endpoints.add(new EndpointInfo(
                        controllerName,
                        method.getNameAsString(),
                        "REQUEST",
                        combinePaths(basePath, methodPath),
                        sourceFile,
                        lineNumber
                ));
            } else {
                for (String httpMethod : httpMethods) {
                    endpoints.add(new EndpointInfo(
                            controllerName,
                            method.getNameAsString(),
                            httpMethod,
                            combinePaths(basePath, methodPath),
                            sourceFile,
                            lineNumber
                    ));
                }
            }
        }

        return endpoints;
    }

    private List<String> extractRequestMethods(MethodDeclaration method) {
        List<String> httpMethods = new ArrayList<>();

        var annotationOpt = method.getAnnotationByName("RequestMapping");
        if (annotationOpt.isEmpty()) {
            return httpMethods;
        }

        var annotation = annotationOpt.get();

        if (annotation.isNormalAnnotationExpr()) {
            var pairs = annotation.asNormalAnnotationExpr().getPairs();

            for (var pair : pairs) {
                if (pair.getNameAsString().equals("method")) {
                    String value = pair.getValue().toString();

                    if (value.startsWith("{") && value.endsWith("}")) {
                        value = value.substring(1, value.length() - 1);
                        String[] parts = value.split(",");

                        for (String part : parts) {
                            String httpMethod = extractRequestMethodName(part.trim());
                            if (!httpMethod.isBlank()) {
                                httpMethods.add(httpMethod);
                            }
                        }
                    } else {
                        String httpMethod = extractRequestMethodName(value.trim());
                        if (!httpMethod.isBlank()) {
                            httpMethods.add(httpMethod);
                        }
                    }
                }
            }
        }

        return httpMethods;
    }

    private String extractRequestMethodName(String raw) {
        if (raw.contains(".")) {
            return raw.substring(raw.lastIndexOf(".") + 1).trim();
        }
        return raw.trim();
    }

    private String extractClassLevelPath(ClassOrInterfaceDeclaration clazz) {
        if (!clazz.isAnnotationPresent("RequestMapping")) {
            return "";
        }

        var annotationOpt = clazz.getAnnotationByName("RequestMapping");

        if (annotationOpt.isEmpty()) {
            return "";
        }

        var annotation = annotationOpt.get();

        if (annotation.isSingleMemberAnnotationExpr()) {
            return cleanPath(
                    annotation.asSingleMemberAnnotationExpr()
                            .getMemberValue()
                            .toString()
                            .replace("\"", "")
            );
        }

        if (annotation.isNormalAnnotationExpr()) {
            var pairs = annotation.asNormalAnnotationExpr().getPairs();

            for (var pair : pairs) {
                if (pair.getNameAsString().equals("value") || pair.getNameAsString().equals("path")) {
                    return cleanPath(pair.getValue().toString().replace("\"", ""));
                }
            }
        }

        return "";
    }

    private String extractPath(MethodDeclaration method, String annotationName) {
        var annotationOpt = method.getAnnotationByName(annotationName);

        if (annotationOpt.isEmpty()) {
            return "/";
        }

        var annotation = annotationOpt.get();

        if (annotation.isSingleMemberAnnotationExpr()) {
            return cleanPath(
                    annotation.asSingleMemberAnnotationExpr()
                            .getMemberValue()
                            .toString()
                            .replace("\"", "")
            );
        }

        if (annotation.isNormalAnnotationExpr()) {
            var pairs = annotation.asNormalAnnotationExpr().getPairs();

            for (var pair : pairs) {
                if (pair.getNameAsString().equals("value") || pair.getNameAsString().equals("path")) {
                    return cleanPath(pair.getValue().toString().replace("\"", ""));
                }
            }
        }

        return "/";
    }

    private String combinePaths(String basePath, String methodPath) {
        if (basePath == null || basePath.isBlank()) {
            return cleanPath(methodPath);
        }

        if (methodPath == null || methodPath.isBlank() || methodPath.equals("/")) {
            return cleanPath(basePath);
        }

        String combined = basePath + "/" + methodPath;
        combined = combined.replaceAll("/+", "/");

        return cleanPath(combined);
    }

    private String cleanPath(String path) {
        if (path == null || path.isBlank()) {
            return "/";
        }

        path = path.replace("\"", "").trim();

        if (!path.startsWith("/")) {
            path = "/" + path;
        }

        path = path.replaceAll("/+", "/");

        if (path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        return path;
    }
}
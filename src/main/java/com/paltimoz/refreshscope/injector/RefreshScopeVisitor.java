package com.paltimoz.refreshscope.injector;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class RefreshScopeVisitor extends VoidVisitorAdapter<Void> {

    public static final String VALUE = "Value";
    public static final String REFRESH_SCOPE = "RefreshScope";
    public static final String REFRESH_SCOPE_PACKAGE = "org.springframework.cloud.context.config.annotation.RefreshScope";


    @Override
    public void visit(ClassOrInterfaceDeclaration node, Void arg) {
        super.visit(node, arg);

        if(containsRefreshScopeAnnotation(node)) return;

        if (containsValueAnnotation(node)) {
            ImportDeclaration importDeclaration = new ImportDeclaration(REFRESH_SCOPE_PACKAGE, false, false);
            node.getParentNode().ifPresent(parent -> {
                if (parent instanceof CompilationUnit) {
                    ((CompilationUnit) parent).addImport(importDeclaration);
                }
            });
            AnnotationExpr refreshScopeAnnotation = new MarkerAnnotationExpr(node.getTokenRange().orElse(null),
                    new JavaParser().parseName(REFRESH_SCOPE).getResult().orElse(null));
            node.addAnnotation(refreshScopeAnnotation);
        }
    }

    private boolean containsValueAnnotation(ClassOrInterfaceDeclaration node) {
        return node.getMembers().stream()
                .anyMatch(member ->
                        member.findFirst(AnnotationExpr.class,
                                expr -> expr.getNameAsString().equals(VALUE)).isPresent());
    }

    private boolean containsRefreshScopeAnnotation(ClassOrInterfaceDeclaration node) {
        return node.getAnnotations().stream()
                .anyMatch(annotation -> annotation.getNameAsString().equals(REFRESH_SCOPE));
    }
}
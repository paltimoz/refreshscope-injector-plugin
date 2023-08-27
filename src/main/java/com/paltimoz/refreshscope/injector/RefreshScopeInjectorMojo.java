package com.paltimoz.refreshscope.injector;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

@Mojo(name = "inject")
@Slf4j
public class RefreshScopeInjectorMojo extends AbstractMojo {

    public static final String JAVA_FILE = ".java";
    @Parameter(defaultValue = "${project.basedir}/src/main/java")
    private File sourceDirectory;

    @Override
    public void execute() {
        log.info("refresh-scope-injector started!");
        processDirectory(sourceDirectory);
        log.info("refresh-scope-injector ended!");
    }

    private void processDirectory(File directory) {
        File[] files = directory.listFiles();

        if (files == null || files.length == 0) return;

        Arrays.stream(files)
                .forEach(file -> {
                    if (file.isDirectory()) {
                        processDirectory(file);
                    } else if (file.isFile() && file.getName().endsWith(JAVA_FILE)) {
                        processJavaFile(file);
                    }
                });
    }

    private void processJavaFile(File file) {
        try {
            ParseResult<CompilationUnit> parseResult = new JavaParser().parse(file);
            if (parseResult.isSuccessful()) {
                parseResult.getResult()
                        .ifPresent(compilationUnit -> {
                            compilationUnit.accept(new RefreshScopeVisitor(), null);
                            writeUpdatedFile(file, compilationUnit.toString());
                        });

            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void writeUpdatedFile(File file, String content) {
        try {
            Files.write(file.toPath(), content.getBytes());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}

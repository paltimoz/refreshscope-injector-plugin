package com.paltimoz.refreshscope.injector;

import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.dynamic.DynamicType;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaClassSource;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.Set;

@Mojo(name = "inject", defaultPhase = LifecyclePhase.COMPILE)
@Slf4j
public class RefreshScopeInjectorMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true)
    protected MavenProject project;

    private final String VALUE_CLASS_NAME = "org.springframework.beans.factory.annotation.Value";
    private final String REFRESH_SCOPE_CLASS_NAME = "org.springframework.cloud.context.config.annotation.RefreshScope";

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        log.info("refreshscope-injector-plugin started !!!");
        String sourceDirectory = project.getBuild().getSourceDirectory();
        log.info("sourceDirectory -> " + sourceDirectory);
        String outputDirectory = project.getBuild().getOutputDirectory();
        log.info("outputDirectory -> " + outputDirectory);
        for (File javaFile : getJavaFiles(sourceDirectory)) {
            try {
                JavaClassSource source = Roaster.parse(JavaClassSource.class, javaFile);
                if(checkAnnotation(source)){
                    DynamicType.Builder<?> builder = new ByteBuddy().redefine(Class.forName(source.getQualifiedName()));
                    builder = annotate(builder);
                    builder.make().saveIn(new File(outputDirectory));
                }
            } catch (Throwable t) {
                log.error(t.getMessage(), t);
            }
        }
        log.info("refreshscope-injector-plugin finished !!!");
    }

    private boolean checkAnnotation(JavaClassSource source) {
        return source.getFields().stream().anyMatch(field -> field.hasAnnotation(VALUE_CLASS_NAME)) && !source.hasAnnotation(REFRESH_SCOPE_CLASS_NAME);
    }

    public Set<File> getJavaFiles(String rootPath) {
        Set<File> allJavaFiles = new LinkedHashSet<>();
        File rootDir = new File(rootPath);
        if (rootDir.isDirectory()) {
            File[] files = rootDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        allJavaFiles.addAll(getJavaFiles(file.getAbsolutePath()));
                    } else {
                        allJavaFiles.add(file);
                    }
                }
            }
        }
        return allJavaFiles;
    }

    public DynamicType.Builder<?> annotate(DynamicType.Builder<?> builder) throws ClassNotFoundException {
        Class<? extends Annotation> annotationType = (Class<? extends Annotation>) Class.forName(REFRESH_SCOPE_CLASS_NAME);
        builder = builder.annotateType(AnnotationDescription.Builder.ofType(annotationType).build());
        return builder;
    }
}

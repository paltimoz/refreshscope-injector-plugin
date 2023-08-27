# RefreshScope Injector Plugin

RefreshScope Injector Plugin is a Maven plugin that automatically adds the `@RefreshScope` annotation to classes that use the `@Value` annotation but do not have the `@RefreshScope` annotation. This ensures that the property values obtained using `@Value` are refreshed across all services in a Spring Cloud project using bus refresh.

## Usage

To use the RefreshScope Injector Plugin, you need to add the following configuration to your project's `pom.xml` file:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>com.paltimoz</groupId>
            <artifactId>refreshscope-injector-plugin</artifactId>
            <version>1.0.0</version>
            <dependencies>
                <dependency>
                    <groupId>${project.groupId}</groupId>
                    <artifactId>${project.artifactId}</artifactId>
                    <version>${project.version}</version>
                </dependency>
            </dependencies>
            <executions>
                <execution>
                    <phase>compile</phase>
                    <goals>
                        <goal>inject</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

Once the plugin is added, you can run `mvn clean install` to compile your project. During the compilation phase, the RefreshScope Injector Plugin will scan for classes that use the `@Value` annotation and automatically add the `@RefreshScope` annotation if it is not already present.

Please make sure that you have the dependency that includes the `@RefreshScope` annotation among your project dependencies. Otherwise, the compilation process will not be blocked, but you will encounter `ClassNotFoundException` errors.

With the RefreshScope Injector Plugin in place, you no longer need to manually add the `@RefreshScope` annotation to every class that uses `@Value`. The plugin will handle it for you during the build process.

`Note:` If you wish to add the `@RefreshScope` annotation to your source code, please use the source_code branch.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

Feel free to contribute and provide feedback.
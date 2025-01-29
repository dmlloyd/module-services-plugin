# Module services plugin

[![Maven Central](https://img.shields.io/maven-central/v/io.github.dmlloyd/module-services-plugin?color=green)](https://search.maven.org/search?q=g:io.github.dmlloyd%20AND%20a:module-services-plugin)

A Maven plugin to produce a classpath `META-INF/services` file from a compiled `module-info` descriptor.

## Usage

Add the following plugin configuration to your POM:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>io.github.dmlloyd.maven</groupId>
            <artifactId>module-services-plugin</artifactId>
            <version>1.0</version>
            <executions>
                <execution>
                    <id>generate-services</id>
                    <goals>
                        <goal>generate</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

By default, the plugin execution will run during the `process-classes` phase.

### Parameters

The following parameters are supported by this plugin.

| Name               | Type      | Required | Since | Description |
|--------------------|-----------|----------| ----- | -------------|
| `classesDirectory` | `File`    | Yes      | 1.0 | The input directory where the module info class should be found.<br>**Default:** `${project.build.outputDirectory}`<br>**User Property:** `module.services.classes`
| `outputDirectory`  | `File`    | Yes      | 1.0 | The output directory where the services file should be placed.<br>**Default:** `${project.build.outputDirectory}`<br>**User Property:** `module.services.classes`
| `skip`             | `boolean` | No       | 1.0 | Set to true to skip execution of this plugin.<br>**Default:** `false`<br>**User Property:** `module.services.skip`

## Requirements

You must use Java 17 or later to build your project in order for this plugin to function.
Your project can target any version of Java.

## Limitations

If you put your `module-info.class` into a nonstandard place (for example, `META-INF/versions/9/module-info.class`), you must configure the `classesDirectory` property accordingly, otherwise the file will not be found and nothing will be produced.

# plusminus-utils

Set of utility classes for all occasions

A small Java library of static utility classes (package `software.plusminus.util`)
focused on reflection, classpath, and object-graph helpers. All utilities are
Lombok `@UtilityClass`-style classes with static methods; checked failures are
wrapped in runtime exceptions from `software.plusminus.util.exception`.

## Utilities

| Class | What it does |
|---|---|
| `AnnotationUtils` | Finds annotations on objects, classes, fields, and methods (by type or name) and reads their attributes |
| `ClassUtils` | Finds and loads classes by simple name, package, or regex; creates instances and inspects class hierarchies |
| `EntityUtils` | Finds an entity's id field and reads its value |
| `FieldUtils` | Reads, writes, and finds fields reflectively by predicate, type, or annotation |
| `FileUtils` | Writes, reads, and checks existence of files by `Path` |
| `GenericsUtils` | Resolves the first generic type parameter of an object's class |
| `MapUtils` | Converts a list into a map keyed by element class |
| `MethodUtils` | Streams a class's methods, walks overridden-method hierarchies, and checks method annotations |
| `NumberUtils` | Checks whether a class is a primitive or wrapped number type |
| `ObjectUtils` | Converts beans to maps, lists null properties, and detects references and circular references in object graphs |
| `ResourceUtils` | Checks for and reads classpath resources as strings |
| `StreamUtils` | Provides merge functions for handling duplicates in `Collectors.toMap` |
| `StringUtils` | Converts enum constant names to camelCase |

Example:

```java
Long id = EntityUtils.findId(entity, Long.class);
boolean circular = ObjectUtils.containsCircularReferences(entity);
String name = StringUtils.enumNameToCamelCase(DayOfWeek.MONDAY); // "monday"
```

## Getting started

Add the dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>software.plusminus</groupId>
    <artifactId>plusminus-utils</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

## Building

Requires JDK 8. Build with the Maven wrapper:

```
./mvnw clean install
```

The build inherits from `plusminus-parent` and enforces Checkstyle, PMD,
SpotBugs, and JaCoCo checks.

## License

Licensed under the Apache License, Version 2.0 — see [LICENSE](LICENSE).

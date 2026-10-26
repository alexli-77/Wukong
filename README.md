# Wukong

Wukong is an early prototype that generates JUnit tests from what a Java program does at run time. It follows the design of [Pankti](https://github.com/ASSERT-KTH/pankti): pick methods worth testing, record their inputs and outputs while the program runs, then turn those recordings into tests.

## Modules

| Module | What it does |
| --- | --- |
| `Wukong-extract` | Builds a Spoon model of a Maven project, JAR or source directory and selects candidate methods: public, non-static, non-abstract, non-empty, not deprecated, and non-void unless you pass `-v`. It tags each method by its contents (branches, local variables, returns), writes the result to `extracted-methods-<project>.csv`, and counts the `java.*` / `jdk.*` APIs each method calls. |
| `Wukong-instrument` | A Javassist java agent (`premain` and `agentmain`) that rewrites method calls to log parameter values, return values and types, then stores them as JSON. |
| `Wukong-generation` | Reads a target Maven project with Spoon, takes a JSON file of recorded calls, and generates JUnit 5 test classes (`Test<Class>PanktiGen`) that rebuild the recorded objects with XStream. |

Stack: Java, Maven, Spoon, Javassist, XStream, Jackson, picocli.

## Status

This is a proof of concept from October 2024 and does not run end to end yet:

- The agent only instruments `ListTest.testList` and writes to absolute paths on my machine.
- The module POMs do not set a main class or an agent manifest, so the jars need manual setup to run.
- `Wukong-extract` reads its API whitelist from `./resources/whitelist`, which is not in the repository.

My later work on runtime-grounded test generation is in [Runtime2Test](https://github.com/alexli-77/Runtime2Test).

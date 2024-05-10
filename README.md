taskrecorder
===

[![Maven Central](https://img.shields.io/maven-central/v/com.io7m.taskrecorder/com.io7m.taskrecorder.svg?style=flat-square)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.io7m.taskrecorder%22)
[![Maven Central (snapshot)](https://img.shields.io/nexus/s/com.io7m.taskrecorder/com.io7m.taskrecorder?server=https%3A%2F%2Fs01.oss.sonatype.org&style=flat-square)](https://s01.oss.sonatype.org/content/repositories/snapshots/com/io7m/taskrecorder/)
[![Codecov](https://img.shields.io/codecov/c/github/io7m-com/taskrecorder.svg?style=flat-square)](https://codecov.io/gh/io7m-com/taskrecorder)
![Java Version](https://img.shields.io/badge/21-java?label=java&color=e6c35c)

![com.io7m.taskrecorder](./src/site/resources/taskrecorder.jpg?raw=true)

| JVM | Platform | Status |
|-----|----------|--------|
| OpenJDK (Temurin) Current | Linux | [![Build (OpenJDK (Temurin) Current, Linux)](https://img.shields.io/github/actions/workflow/status/io7m-com/taskrecorder/main.linux.temurin.current.yml)](https://www.github.com/io7m-com/taskrecorder/actions?query=workflow%3Amain.linux.temurin.current)|
| OpenJDK (Temurin) LTS | Linux | [![Build (OpenJDK (Temurin) LTS, Linux)](https://img.shields.io/github/actions/workflow/status/io7m-com/taskrecorder/main.linux.temurin.lts.yml)](https://www.github.com/io7m-com/taskrecorder/actions?query=workflow%3Amain.linux.temurin.lts)|
| OpenJDK (Temurin) Current | Windows | [![Build (OpenJDK (Temurin) Current, Windows)](https://img.shields.io/github/actions/workflow/status/io7m-com/taskrecorder/main.windows.temurin.current.yml)](https://www.github.com/io7m-com/taskrecorder/actions?query=workflow%3Amain.windows.temurin.current)|
| OpenJDK (Temurin) LTS | Windows | [![Build (OpenJDK (Temurin) LTS, Windows)](https://img.shields.io/github/actions/workflow/status/io7m-com/taskrecorder/main.windows.temurin.lts.yml)](https://www.github.com/io7m-com/taskrecorder/actions?query=workflow%3Amain.windows.temurin.lts)|

## taskrecorder

The `taskrecorder` package provides a simple abstraction to record the steps
(and subtasks) performed during the execution of tasks within an application.

## Features

* Record detailed task steps for informative error reports.
* High coverage test suite.
* [OSGi-ready](https://www.osgi.org/)
* [JPMS-ready](https://en.wikipedia.org/wiki/Java_Platform_Module_System)
* ISC license.

## Usage

Create a new task, and create steps and subtasks as the application performs
operations:

```
final Logger logger;
final TRTaskRecorderType<Integer> recorder =
  TRTaskRecorder.create(logger, "Book Flight");

recorder.beginStep("Picking best airline price...");
Airline airline;
try {
  airline = pickAirline();
  recorder.setStepSucceeded("Found airline.");
} catch (Exception e) {
  recorder.setTaskFailed("No price available.", e);
  return;
}

recorder.beginStep("Making reservation...");
try {
  int id = makeReservation(airline);
  recorder.setTaskSucceeded("Created reservation.", id);
} catch (Exception e) {
  recorder.setTaskFailed("No reservations available.", e);
  return;
}

var task = recorder.toTask();
assert task.resolution() instanceof TRSucceeded;
```


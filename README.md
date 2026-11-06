
# Advanced File System — Visitor Pattern

## Overview

This project implements an in-memory hierarchical file system using the Visitor behavioral design pattern.

The system supports files, directories, nested structures, and three independent operations.

## Technologies

- Java 17
- Maven
- JUnit 5

## Design

The Element interface defines the common contract for files and directories.

FileNode represents a file with a name and non-negative byte size.

DirectoryNode represents a directory containing files and other directories.

The Visitor interface defines overloaded visit methods for both element types.

FileSystemWalker handles traversal using polymorphism without runtime type checking.

## Visitors

### TotalSizeVisitor
Calculates the total size of all files in a subtree.

### SearchVisitor
Searches files using a configurable filename criterion and inclusive minimum-size threshold. Results are sorted lexicographically and use root-relative paths.

### StatisticsVisitor
Counts files, directories, and maximum tree depth. Root depth is zero.

## Tree Integrity

The file system prevents:
- Negative file sizes
- Duplicate sibling names
- Multiple parent ownership
- Cyclic directory structures

Directory children cannot be modified through the returned collection.

## Running the Application

From the project root:

```bash
mvn compile
mvn exec:java -Dexec.mainClass=Main
```

## Running Tests

```bash
mvn test
```

## Test Results

Test class: FileSystemTest

Tests run: 8
Failures: 0
Errors: 0
Skipped: 0

BUILD SUCCESS

## Visitor Reusability

Visitor instances can be reused through their public execution methods. Accumulated results are reset before each operation.

## Extensibility

New operations can be added by implementing the Visitor interface without modifying FileNode or DirectoryNode.

Introducing a new element type requires updating the Visitor interface and all existing visitor implementations.

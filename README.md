Commons Network
============
[![Travis CI](https://travis-ci.org/1and1/commons-network.svg?branch=master)](https://travis-ci.org/1and1/commons-network)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.oneandone/commons-network/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.oneandone/commons-network)
[![javadoc](https://javadoc.io/badge2/com.oneandone/commons-network/javadoc.svg)](https://javadoc.io/doc/com.oneandone/commons-network)
[![ReleaseDate](https://img.shields.io/github/release-date/1and1/commons-network)](https://github.com/1and1/commons-network/releases)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/1d90f8dfca404cedbc74cb9f11b702ea)](https://www.codacy.com/manual/Stephan-FuhrmannOrganization/commons-network?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=1and1/commons-network&amp;utm_campaign=Badge_Grade)


A library of IP network representation classes, like
* IP address (IPv4, IPv6),
* MAC address,
* IP network using the CIDR method ([Classless Inter-Domain Routing](https://en.wikipedia.org/wiki/Classless_Inter-Domain_Routing)).

## Prerequisites

* Java 8 or later
* Maven 3.6 or later
 
## Design Goals

The following are the key design goals:
* Test-coverage: Have well-tested classes. Test coverage is at the moment at 95% line-coverage.
* Immutability: Have immutable classes where possible to support usage in a multi-threaded environment.
* Performance: Have simple, well-performing implementations.
* Simplicity: Have simple implementations where possible.
* No dependencies: Do not require dependencies for the core cases.
* Fail-fast: If you enter illegal data, you'll get an exception. The invariant
  of all objects is to contain no illegal or null data.
* No I/O: In contrast to the [InetAddress](https://docs.oracle.com/javase/7/docs/api/java/net/InetAddress.html) class
in JDK, these classes are just model classes.

Usage
============
You can include commons-network as a Maven dependency from
[Maven Central](https://mvnrepository.com/artifact/com.oneandone/commons-network).

## Maven dependency

```xml
<dependency>
    <groupId>com.oneandone</groupId>
    <artifactId>commons-network</artifactId>
    <version>0.13.0</version>
</dependency>
```

## Documentation of provided classes and methods

The JavaDoc documentation can be found [here](https://javadoc.io/doc/com.oneandone/commons-network).

## Version schema

The version numbers are chosen according to the
[semantic versioning](https://semver.org/) schema.
Especially major version changes come with breaking API
changes.

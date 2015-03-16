![http://static-www.cloudbees.com/images/badges/BuiltOnDEV.png](http://static-www.cloudbees.com/images/badges/BuiltOnDEV.png)

# Features for Next Release #

5.3-SNAPSHOT (in-development)
  * expose camel contexts deployed with process to its invoke handler
  * replacement of Apache AXIS with Apache CXF
  * inclusion of Esper's CEP engine

# Release Notes #

5.2 (July 2011)
  * If you're using the default database (H2) then be sure to download the [h2 jar](http://repo2.maven.org/maven2/com/h2database/h2/1.2.122/) and place it in your Tomcat/lib or Jetty/lib
  * war packaging
  * ear packaging for JBoss and possibly other app servers (courtesy of super dave)
  * no more JBI/ServiceMix packaging
  * [Apache Camel Component](ApacheCamel.md)
  * SpringDeploymentHandler supports inclusion of spring application context file within deployment bundles
  * restoring some of the web-services for deployment, URN mappings, and other management functionality
  * ApplicationConfiguration addresses the replacement of custom engine config with Spring application context and Java Preferences
  * lots of assorted code cleanup (mostly generics)

5.1 (June 2010)
  * JBI/ServiceMix 3.3/4 packaging
  * remote management via web-app and JMX

# Intro #

This project is based on the final open source release of ActiveBPEL. The goal of this project is to provide a place for developers to enhance this release in order to provide an open source BPEL engine that is suitable for use in government projects or other cash constrained environments.

# Licensing #
A few words for the licensing requirements:

This product includes software used under license from ActiveBPEL, LLC, but it is not an ActiveBPEL product and has not been tested, endorsed, or approved by ActiveBPEL, LLC.

Powered by ActiveBPEL

# Unit Tests #
The lack of unit tests in the source code is due to the unit test projects not having been released with the open source code. This is unfortunate since in many cases there was good coverage for the code base. The net result is that changes can't be made with impunity so any changes within the engine really should require creation of tests.

# Highlights #
  * [BPELComparison](BPELComparison.md) page for how bpel-g compares with some other open source engines.
# Deployment Handlers #

The 5.2 release introduced a [simplification](DeploymentClasses.md) of the deployment process where each archive (either a BPR or ODE style) passes through a chain of handlers that each handle a different part of the deployment process. This chain includes handlers for deploying WSDL/XSD resources, BPEL processes, web services for BPEL endpoints, as well as an optional Spring Application Context for each BPR.

# Spring Deployment Handler #

The spring handler looks for the existence of a META-INF/applicationContext.xml file within the deployment bundle. This is supported within the Apache ODE style zip file and the traditional Business Process Archive (BPR) format from ActiveBPEL.

The application context file is loaded by a GenericApplicationContext with access to the webapp classloader as well as a URL classloader with access to the resources within the deployment unit. The lifecycle of the context is tied to the lifecycle of the application. The context starts running when the unit is deployed and stops when the unit is undeployed.
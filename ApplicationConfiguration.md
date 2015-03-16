# Original ActiveBPEL packaging #

The original packaging for ActiveBPEL included a custom XML configuration file that was used to both store user preferences and configure the application. This packaging included information that was intended to be configured at build time, installation time, and also variable runtime properties.

# Java Preferences #
The Java Preferences framework is now used to store all of the variable runtime parameters for the application. Reasonable defaults are provided that the user can modify directly through the system storage or via the web console application.

One drawback to the Preferences framework is that it appears to behave slightly differently on different platforms. In the case of Windows, it looks as though the user must run the application as an Administrator since it stores the preferences within the registry. On some Linux installations, the default directory for the preference storage is not writable by default. Everything seems to work great on OS X.

The bpel-g release includes an optional file based preferences framework that the user can configure through the standard system property. This property is set as follows:

-Djava.util.prefs.PreferencesFactory=org.activebpel.rt.bpel.prefs.FilePreferencesFactory

The file defaults to [user.home]/.fileprefs, but may be overridden with a system property like so:

-Dorg.activebpel.rt.bpel.prefs.FilePreferencesFactory.file=path/to/yourFile.txt

The implementation for this file based preferences factory is taken from here:

http://www.davidc.net/programming/java/java-preferences-using-file-backing-store

# Application Configuration #
The remaining configuration information that is intended to be set during the build or installation time is configured within a spring application context. The various managers and other components that were previously created or configured through reflection are now managed by the spring framework.
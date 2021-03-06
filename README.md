![screenshot1](mddf-tools/docs/users/md/manifest/validator/v1.1/images/MLabs_header.jpg)
# MovieLabs Digital Distribution Framework—MDDF


1 [What's New](#h_News)

2 [Overview](#h_Overview)

3 [Status](#h_Status)

4 [Software Organization & Roadmap](#h_Roadmap)
  1. [mddf-lib](#h_mddf-lib)
  2. [mddf-tools](#h_mddf-tools)
  3. [Versioning](#h_versionMgmt)
  
5) [Using mddf-lib](#h_building)

6 [Installing and Running mddf-tools](#h_Install)
  1. [Java](#h_Install_Java)
  1. [Native Executables ](#h_Install_native)
      1.[Downloading](#h_download)
      2.[OS-X](#h_Install_OSX)
      3. [Windows](#h_Install_MS)
      4. [Linux](#h_Install_LINUX)

7 [ Release History](#h_History)

---
## <a name="h_News">1) What's New</a>

* 2019-Jan-21: v1.5.1 Release Candidates available for evaluation. See ChangeLog for details
* 2018-Nov-15: mddf-tool v1.5 has been released as both an executable jar and as native executables.

__NOTE:__ Beginning with Release 1.5, pre-built executables for Windows, OS-X, and Linux will no longer
be supported. The executable Java jar will still be supported. See ["__Installing and Running__"](#h_Install)
for details.

## <a name="h_Overview">2) Overview:</a>

This repository contains Java software supporting the creation and usage of MDDF files including

* Avails
* Common Media Manifest (CMM)
* Media Manifest Core (MMC)
* Media Entertainment Core (MEC)

Information on the MovieLabs Digital Distribution Framework and the various MDDF standards is available at <http://www.movielabs.com/md/>

## <a name="h_Status">3) Status and Relationship to Other MovieLabs Github Repositories:</a>

The software in this repository is intended to replace that contained in the following MovieLabs repositories:

* availslib
* availstool
* cpe-preprocessing

## <a name="h_Roadmap">4) Software Organization & Roadmap:</a>

There are two projects within this repository: __mddf-lib__ and __mddf-tools__.

### <a name="h_mddf-lib">4.1) mddf-lib</a>
mddf-lib implements all core (i.e., *non-UI*) functionality that can be used to generate, validate, or transform MDDF files. 
### <a name="h_mddf-tools">4.2) mddf-tools</a>
This project implements standalone applications for performing MDDF-related tasks.

The applications in mddf-tools are implemented on top of mddf-lib and any developers intending to develop their 
own mddf support applications are encouraged to do the same.

The mddf-tools software currently implements two applications:

   - **Avails Validator Tool**: may be used to
      - validate an Avails file specified as either XML or XLSX
      - translate an XLSX-formatted Avails to the equivalent XML
      - translate an XML-formatted Avails to the equivalent XLSX
   
   - **Manifest Validator**: validates that a CMM, MMC, or MEC file conforms with:
     - the CMM schema
     - recommended 'Best Practices'
     - a specific *profile*


Releases of the mddf-tools are available as an executable Java jar which supports the processing of either Avails or Manifest files. In addition to an interactive mode, the jar provides a command
line interface (CLI) that may be used either from a terminal window or in conjunction with scheduled jobs (e.g., via crontab).
[NOTE: Releases prior to v1.1.3 did not include pre-built executable jars.]

Prior to v1.5, releases included Native Executables for Windows, OS-X, and Linux systems. Each executable is specific to the processing of either Avails or Manifest files.
 
Refer to ChangeLog.md for a list of specific enhancements and bug fixes for any given release.

### <a name="h_versionMgmt">4.3) Versioning:</a>
#### 4.3.1) mddf-lib and Java jars:
Stable releases of the mddf-lib will have a two-part version or a three-part version ID. 
Interim development and evaluation (i.e., BETA) releases will include an "rcN" suffix. 

Examples:
* v1.3 or v2.0 identify stable releases
* v2.0.1.rc2 is a Beta release of v2.0.1

Executable jars are created for both stable and BETA releases and track the version number of the underlying mddf-lib.

Example: mddf-tool-v3.1.jar would based on, and compatible with, v3.1 of mddf-lib.

#### 4.3.2) Native Executables:
As of v1.5, support for pre-built native executables has been dropped. 

Beginning with the release of v1.3 of mddf-lib, native executables were assigned versions that append a letter to the 
the underlying mddf-lib version.

Examples: 
* an Avails Validator with a version of v1.3.a is based on v1.3 of the mddf-lib.
* a Manifest Validator with a version of v1.3.2.c is based on v1.3.2 of the mddf-lib.

Prior to July 2017 and the release of v1.3 of the mddf-lib, the native executables were assigned a version ID
independent of the mddf-lib version. 

## <a name="h_building">5) Using mddf-lib:</a>
The mddf-tools described in the next section are implemented on top of mddf-lib. Starting with v1.5.1, releases of mddf-lib are available via the Maven Central Repository. 
Developers wishing to use mddf-lib in their own software therefore have two options: building from the source or adding a dependency to their pom.xml:

		<dependency>
			<groupId>com.movielabs</groupId>
			<artifactId>mddf-lib</artifactId>
			<version>${mddf.lib.version}</version>
		</dependency>


## <a name="h_Install">6) Installing and Running mddf-tools:</a>

### <a name="h_Install_Java">6.1) Java Executable Jar:</a>

The mddf-tool jar may be used on any machine that supports Java 1.8 or a more recent jvm. A single jar file provides support
for both the Avails and Manifest Validators. This is in contrast to the OS-specific executables in which there is a separate 
executable for each validation tool.

Once the jar file has been downloaded, it may be run using the standard Java command:

`java -jar ./{path-to-directory}/mddf-tool-v{version}.jar -i`

The `-i` argument indicates the toolkit should be started in interactive mode. A 
`Tool Launcher`dialog will appear that allows the user to select the MDDF tool they 
wish to use (e.g., the Avails Validator). To see all supported arguments, use `-h`

### <a name="h_Install_native">6.2)  Native Executables (DEPRECATED):</a>

Prior to v1.5 native executables for OS-X, Windows, and Linux were also provided. While these are still available for download, usage
is not recommended as they will lack support for the latest MDDF formats and standards. Also note that the executable  Java jar provides support
for both the Avails and Manifest Validators, in contrast to the OS-specific executables in which there is a separate 
executable for each validation tool

#### <a name="h_download">Downloading:</a>

The native executables may be downloaded from one of two sources:

   * Manifest Validator: <http://movielabs.com/md/manifest/validator/>
   * Avails Validator: <http://movielabs.com/md/avails/validator/>

#### <a name="h_Install_OSX">OS-X:</a>

Due to issues with Apple's Gatekeeper security mechanism and JavaFX applications, OS-X executables are provided 
in the form of zip files. Unlike with the Java jars, a separate file must be
downloaded and installed for each MDDF tool.

1. unzip the downloaded file(s) to the desired location (e.g. `/Applications`)
1. make the applications executable. Open a terminal and enter the following:
```
  cd {install-directory}
  chmod -R a+x ./AvailsValidator.app
  chmod -R a+x ./ManifestValidator.app
```

The application may now be launched via the Finder. An error message that “the application can’t be opened” 
indicates Step #2 was not performed correctly.

#### <a name="h_Install_MS">Windows:</a>

Executables for Windows environments are provided in the form of standard `.exe` files and require no special
steps to install or run. The `.msi` formatted distro is not supported.

#### <a name="h_Install_LINUX">Linux:</a>

Linux executables are provided in both`rpm` and `deb` distro formats.

## <a name="h_History">7) Release History:</a>

* **2018-Nov-15**: mddf-tool v1.5 released.

* **2018-Aug-08**: mddf-tool v1.4 released.

* **2018-Jul-27**: mddf-tool v1.4_rc2 released as an executable jar for testing and evaluation.

* **2018-Jul-05**: mddf-tool v1.4_rc1 released as an executable jar for testing and evaluation.

* **2018-Mar-22**: mddf-tool v1.3.2 released.

* **2017-Dec-18**: mddf-tool v1.3.1 released.

* **2017-Nov-13**: mddf-tool v1.3 released.

* **2017-Oct-13**: mddf-tool v1.3.rc4 released for testing and evaluation.

* **2017-Sep-21**: mddf-tool v1.3.rc3 released for testing and evaluation.

* **2017-May-09**: mddf-tool v1.2 released.

* **2017-May-01**: mddf-tool v1.1.3.rc8 released as an executable jar for testing and evaluation.

* **2017-Apr-21**: mddf-tool v1.1.3.rc6 released as an executable jar for testing and evaluation.

* **2017-Apr-18**: mddf-tool v1.1.3.rc4 released as an executable jar for testing and evaluation.

* **2017-Mar-29**: mddf-tool released as an executable jar. Processing of either Avails or Manifest files is supported.

* **2017-Mar-02**: mddf-lib v1.1.2 released.

* **2017-Feb-13**: mddf-lib v1.1.1 released. This provides several fixes relating to the translation of Avails
from XLSX to XML. 

* **2017-Jan-30**: mddf-lib v1.1 released. 
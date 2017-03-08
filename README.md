![screenshot1](mddf-tools/docs/users/md/manifest/validator/v1.1/images/MLabs_header.jpg)
# MovieLabs Digital Distribution Framework—MDDF

1 [What's New](#h_News)

2 [Overview](#h_Overview)

3 [Status](#h_Status)

4 [Versions](#h_Versions)

5 [Software Organization & Roadmap](#h_Roadmap)

---
## <a name="h_News">What's New</a>

* **2017-Mar-02**: mddf-lib v1.1.2 is now available. See Change Log for list of fixed bugs.

* **2017-Feb-13**: mddf-lib v1.1.1 is now available. This provides several fixes relating to the translation of Avails
from XLSX to XML. 

* **2017-Jan-30**: mddf-lib v1.1 is now stable. The validators based on this release are Manifest Validator v1.2.2 and 
Avails Validator v1.1. The Avails Validator now supports conversion of an XML Avails file to XLSX. The currently
supported conversions are:
  * from XML (v2.1 or v2.2) to XLSX v1.7
  * from XLSX (v1.6 or v1.7) to XML v2.2
  * from XLSX v1.6 to XLSX v1.7mddf-lib v1.

## <a name="h_Overview">Overview:</a>

This repository contains Java software supporting the creation and usage of MDDF files including

* Avails
* Common Media Manifest (CMM)
* Media Manifest Core (MMC)
* Media Entertainment Core (MEC)

Information on the various MDDF standards is available at <http://www.movielabs.com/md/>

## <a name="h_Status">Status and Relationship to Other MovieLabs Github Repositories:</a>

The software in this repository is intended to replace that contained in the following MovieLabs repositories:

* availslib
* availstool
* cpe-preprocessing

## <a name="h_Versions">Versions</a>

The latest releases are:

* mddf-lib: v1.1.1
* Manifest Validator: v1.2.3
* Avails Validator: v1.1.1

Refer to ChangeLog.md for a list of specific enhancements and bug fixes.

## <a name="h_Roadmap">Software Organization & Roadmap:</a>

There are two projects within this repository:

* __mddf-lib__: this implements all core (i.e., *non-UI*) functionality that can be used to generate, validate, or transform MDDF files.
* __mddf-tools__: implements standalone applications for validation.

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
      

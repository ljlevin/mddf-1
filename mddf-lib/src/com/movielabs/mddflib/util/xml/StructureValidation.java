/**
 * Copyright (c) 2017 MovieLabs

 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.movielabs.mddflib.util.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import com.movielabs.mddflib.logging.IssueLogger;
import com.movielabs.mddflib.logging.LogMgmt;
import com.movielabs.mddflib.logging.LogReference;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * A 'helper' class that supports the validation of an XML file against a set of
 * structural requirements not specified via an XSD.
 * <p>
 * The MDDF specifications define many requirements for specific use cases as
 * well as recommended 'best practices'. These requirements and recommendations
 * specify relationships between XML elements that are not defined via the XSD.
 * In order to support validation they are instead formally specified via a
 * JSON-formatted <i>structure definition</i> file. The
 * <tt>StructureValidation</tt> class provides the functions that can interpret
 * the requirements and test an XML file for compliance.
 * </p>
 * <h3>Semantics and Syntax:</h3>
 * <p>
 * The semantics of JSON a structure definition is as follows:
 * 
 * <pre>
 * {
 * 	  <i>USAGE</i>:
 * 		 {
 * 			"targetPath" : <i>XPATH</i> (optional)
 * 			"constraint" :
 * 			[
 * 				{
				  "min": <i>INTEGER</i>,
				  "max": <i>INTEGER</i>,
				  "xpath": <i>(XPATH | ARRAY[XPATH])</i>,
				  "severity": <i>("Fatal" | "Error" | "Warning" | "Notice")</i>,
				  "msg" : <i>STRING</i>,  (optional)
				  "docRef": <i>STRING</i> (optional)
 * 				}
 * 			]
 * 		 }
 * }
 * </pre>
 * 
 * where:
 * <ul>
 * <li><i>USAGE</i> is a string defining the key used by a validator to retrieve
 * the requirements appropriate to a use case.</li>
 * <li><tt>targetPath</tt>: indicates the element(s) that provide the evaluation
 * context for the constraint xpath when invoking the
 * <tt>validateDocStructure()</tt> method. If not specified, a target element
 * must be provided when invoking the <tt>validateConstraint()</tt> method on a
 * target element that has been identified by other means.</li>
 * <li><tt>constraint</tt>: one or more structural requirements associated with
 * the targeted element.
 * <ul>
 * <li><tt>xpath</tt>: defines one or more xpaths relative to the target element
 * that, when evaluated, the number of matching elements satisfy the min/max
 * cardinality constraints. If multiple xpaths are listed, the total number of
 * elements (or attributes) returned when each is separately evaluated must
 * satisfy the constraint.</li>
 * <li><tt>min</tt>: minimum number of matching objects that should be found
 * when evaluating the xpath(s). [OPTIONAL, default is 0]</li>
 * <li><tt>max</tt>: maximum number of matching objects that should be found
 * when evaluating the xpath(s). [OPTIONAL, default is unlimited]</li>
 * <li><tt>severity</tt>: must match one of the <tt>LogMgmt.level</tt> values
 * </li>
 * <li><tt>msg</tt>: text to use for a log entry if the constraint is not met.
 * If not provided, a generic message is used.</li>
 * <li><tt>docRef</tt>: is a value usable by <tt>LogReference</tt> that will
 * indicate any reference material documenting the requirement.</li>
 * </ul>
 * </li>
 * </ul>
 * </p>
 * 
 * <p>
 * An <tt>XPATH</tt> is defined using the standard XPath syntax with one
 * modification. Namespaces are indicated using a variable indicating the name
 * of an MDDF schema. The appropriate namespace prefixes will be inserted by the
 * software. Supported namespaces are:
 * </p>
 * <ul>
 * <li>{avail}</li>
 * <li>{mdmec}</li>
 * <li>{manifest}</li>
 * <li>{md}</li>
 * </ul>
 * For example:
 * 
 * <pre>
 * <tt>
		"POEST": 
		{
			"targetPath": ".//{avail}LicenseType[.='POEST']",
			"constraint": 
			[
				{ 
					"min": "1",
					"max": "1",
					"xpath": "../{avail}Term[@termName='SuppressionLiftDate']",
					"severity": "Error",
					"msg": "One SuppressionLiftDate is required for LicenseType 'POEST'"
				}
			]
		},
		
		"WorkType-Episode": 
		{
			"constraint": 
			[
				{
					"docRef": "AVAIL:avail00n",
					"min": "1",
					"max": "2",
					"xpath": 
					[
						"{avail}EpisodeMetadata/{avail}AltIdentifier",
						"{avail}EpisodeMetadata/{avail}EditEIDR-URN"
					]
				}
			]
		},
 *</tt>
 * </pre>
 * 
 * <h3>Filters:</h3>
 * <p>
 * A 'Filter' may be used to supplement the matching criteria specified by the
 * XPaths. This is used when XPath criteria are insufficient, or too unwieldy,
 * to fully implement a constraint. Filters are (for now) defined as a set of
 * values and an optional 'negated' flag set to <tt>true</tt> or <tt>false</tt>.
 * </p>
 * <p>
 * For example, the following filter would identify <tt>Audio</tt> assets that
 * have a <tt>ChannelMapping</tt> that is inconsistent with the number of actual
 * channels:
 * </p>
 * 
 * <pre>
		"MultiChannel": 
		{
			"targetPath": ".//{md}Channels[. &lt; 1]",
			"constraint": 
			[
				{
					"xpath": 
					[
						"../{md}Encoding/{md}ChannelMapping"
					],

					"filter": 
					{
						"values": 
						[
							"Mono",
							"Left",
							"Center",
							"Right"
						],

						"negated": "false"
					},

					"max": "0",
					"severity": "Error",
					"msg": "ChannelMapping is not valid for multi-channel Audio"
				}
			]
		}
 * </pre>
 * 
 * <h3>Usage:</h3>
 * <p>
 * Validation modules should determine the appropriate JSON resource file based
 * on the type and version of the MDDF file. Requirements may then be retrieved
 * and individually checked using the USAGE key or the entire collection may be
 * iterated thru.
 * 
 * @author L. Levin, Critical Architectures LLC
 *
 */
public class StructureValidation {

	protected IssueLogger logger;
	protected String logMsgSrcId;

	public StructureValidation(IssueLogger logger, String logMsgSrcId) {
		this.logger = logger;
	}

	public boolean validateDocStructure(Element rootEl, JSONObject rqmt) {
		String rootPath = rqmt.getString("targetPath");
		XPathExpression<?> xpExp = resolveXPath(rootPath);
		List<Element> targetElList = (List<Element>) xpExp.evaluate(rootEl);
		JSONArray constraintSet = rqmt.getJSONArray("constraint");
		boolean isOk = true;
		for (Element nextTargetEl : targetElList) {
			for (int i = 0; i < constraintSet.size(); i++) {
				JSONObject constraint = constraintSet.getJSONObject(i);
				isOk = evaluateConstraint(nextTargetEl, constraint) && isOk;
			}
		}
		return isOk;
	}

	public boolean evaluateConstraint(Element target, JSONObject constraint) {
		boolean passes = true;
		Map<String, String> varMap = resolveVariables(target, constraint);
		int min = constraint.optInt("min", 0);
		int max = constraint.optInt("max", -1);
		String severity = constraint.optString("severity", "Error");
		int logLevel = LogMgmt.text2Level(severity);

		String docRef = constraint.optString("docRef");

		Object xpaths = constraint.opt("xpath");
		List<XPathExpression<?>> xpeList = new ArrayList<XPathExpression<?>>();
		String targetList = ""; // for use if error msg is required
		String[] xpParts = null;
		if (xpaths instanceof String) {
			String xpathDef = (String) xpaths;
			xpeList.add(resolveXPath(xpathDef, varMap));
			xpParts = xpathDef.split("\\[");
			targetList = xpParts[0];
		} else if (xpaths instanceof JSONArray) {
			JSONArray xpArray = (JSONArray) xpaths;
			for (int i = 0; i < xpArray.size(); i++) {
				String xpathDef = xpArray.getString(i);
				xpeList.add(resolveXPath(xpathDef, varMap));
				xpParts = xpathDef.split("\\[");
				if (i < 1) {
					targetList = xpParts[0];
				} else if (i == (xpArray.size() - 1)) {
					targetList = targetList + ", or " + xpParts[0];
				} else {
					targetList = targetList + ", " + xpParts[0];
				}
			}
		}
		// reformat targetList for use in log msgs....
		targetList = targetList.replaceAll("\\{\\w+\\}", "");

		/* Has an OPTIONAL filter been included with the constraint? */
		JSONObject filterDef = constraint.optJSONObject("filter");

		List<Element> matchedElList = new ArrayList<Element>();
		for (int i = 0; i < xpeList.size(); i++) {
			XPathExpression<Element> xpExp = (XPathExpression<Element>) xpeList.get(i);
			List<Element> nextElList = xpExp.evaluate(target);
			if (filterDef != null) {
				nextElList = applyFilter(nextElList, filterDef);
			}
			matchedElList.addAll(nextElList);
		}
		String logMsg = constraint.optString("msg", "");
		String details = constraint.optString("details", "");

		// check cardinality
		int count = matchedElList.size();
		if (min > 0 && (count < min)) {
			String elName = target.getName();
			String msg;
			if (logMsg.isEmpty()) {
				msg = "Invalid " + elName + " structure: missing elements";
			} else {
				msg = logMsg;
			}
			if (details.isEmpty()) {
				details = elName + " requires minimum of " + min + " " + targetList + " elements";
				if (xpParts.length > 1) {
					details = details + " matching the criteria [" + xpParts[1];
				}
			}
			LogReference srcRef = resolveDocRef(docRef);
			logger.logIssue(LogMgmt.TAG_MD, logLevel, target, msg, details, srcRef, logMsgSrcId);
			if (logLevel > LogMgmt.LEV_WARN) {
				passes = false;
			}
		}
		if (max > -1 && (count > max)) {
			String elName = target.getName();
			String msg;
			if (logMsg.isEmpty()) {
				msg = "Invalid " + elName + " structure: too many child elements";
			} else {
				msg = logMsg;
			}
			if (details.isEmpty()) {
				details = elName + " permits maximum of " + max + "  " + targetList + " elements";
			}
			LogReference srcRef = resolveDocRef(docRef);
			logger.logIssue(LogMgmt.TAG_MD, logLevel, target, msg, details, srcRef, logMsgSrcId);

			if (logLevel > LogMgmt.LEV_WARN) {
				passes = false;
			}
		}

		return passes;
	}

	/**
	 * Assign values to any <i>variables</i> used by a <tt>constraint</tt>.
	 * Variables are denoted by a "$" followed by an ID (e.g., <tt>$FOO</tt>) and
	 * are assigned a value via a XPath. For example: <tt><pre>
	 *   "$CID": "./@ContentID"
	 * </pre></tt> They may be included in the constraint's XPath by using enclosing
	 * the variable name in curly brackets. For example: <tt><pre>
	 *   "xpath": 
	 *      [
	 *         "../..//{manifest}Experience[@ExperienceID={$CID}]/{manifest}PictureGroupID"
	 *      ],
	 * </pre></tt>
	 * <p>
	 * It is possible that the XPath used to determine a variable's value will
	 * evaluate to a <tt>null</tt>. Any constraint XPath that references a null
	 * variable will be skipped when evaluating the constraint criteria.
	 * </p>
	 * 
	 * @param target
	 * @param constraint
	 * @return variable assignments as key/value entries in a Map
	 */
	private Map<String, String> resolveVariables(Element target, JSONObject constraint) {
		Map<String, String> varMap = new HashMap<String, String>();
		Set<String> keySet = constraint.keySet();
		for (String key : keySet) {
			if (key.startsWith("$")) {
				String xpath = constraint.getString(key);
				XPathExpression<?> xpe = resolveXPath(xpath);
				String value = null;
				if (resolvesToAttribute(xpath)) {
					Attribute varSrc = (Attribute) xpe.evaluateFirst(target);
					if (varSrc != null) {
						value = varSrc.getValue();
					}
				} else {
					Element varSrc = (Element) xpe.evaluateFirst(target);
					if (varSrc != null) {
						value = varSrc.getTextNormalize();
					}
				}
				varMap.put(key, value);
				String msg = "resolveVariables(): Var " + key + "=" + value;
				logger.logIssue(LogMgmt.TAG_MD, LogMgmt.LEV_DEBUG, target, msg, xpath, null, logMsgSrcId);

			}
		}
		return varMap;
	}

	/**
	 * Apply a filter to a list of Elements. This is used when XPath criteria are
	 * insufficient, or too unwieldy, to fully implement a constraint.
	 * <p>
	 * Filters are (for now) defined as a set of values and an optional 'negated'
	 * flag set to <tt>true</tt> or <tt>false</tt>
	 * </p>
	 * <b>NOTE:</b> future implementations may include support for REGEX pattern
	 * matching.
	 * 
	 * @param elList
	 * @param filterDef
	 * @return
	 */
	private List<Element> applyFilter(List<Element> inList, JSONObject filterDef) {
		List<Element> outList = new ArrayList();
		JSONArray valueSet = filterDef.optJSONArray("values");
		String negated = filterDef.optString("negated", "false");
		boolean isNegated = negated.equals("true");
		for (Element nextEl : inList) {
			String value = nextEl.getValue();
			if (valueSet.contains(value)) {
				if (!isNegated) {
					outList.add(nextEl);
				}
			} else {
				if (isNegated) {
					outList.add(nextEl);
				}
			}
		}
		return outList;
	}

	public static XPathExpression<?> resolveXPath(String xpathDef) {
		return resolveXPath(xpathDef, null);
	}

	/**
	 * Create a <tt>XPathExpression</tt> from a string representation. An
	 * <tt>xpathDef</tt> is defined using the standard XPath syntax with one
	 * modification. Namespaces are indicated using a variable indicating the name
	 * of an MDDF schema. The appropriate namespace prefixes will be inserted by the
	 * software. Supported namespaces are:
	 * <ul>
	 * <li>{avail}</li>
	 * <li>{mdmec}</li>
	 * <li>{manifest}</li>
	 * <li>{md}</li>
	 * </ul>
	 * 
	 * @param xpathDef
	 * @param varMap   (optional)
	 * @return
	 */
	public static XPathExpression<?> resolveXPath(String xpathDef, Map<String, String> varMap) {
		Set<Namespace> nspaceSet = new HashSet<Namespace>();

		/*
		 * replace namespace placeholders with actual prefix being used
		 */
		if (xpathDef.contains("{md}")) {
			xpathDef = xpathDef.replaceAll("\\{md\\}", XmlIngester.mdNSpace.getPrefix() + ":");
			nspaceSet.add(XmlIngester.mdNSpace);
		}

		if (xpathDef.contains("{avail}")) {
			xpathDef = xpathDef.replaceAll("\\{avail\\}", XmlIngester.availsNSpace.getPrefix() + ":");
			nspaceSet.add(XmlIngester.availsNSpace);
		}

		if (xpathDef.contains("{manifest}")) {
			xpathDef = xpathDef.replaceAll("\\{manifest\\}", XmlIngester.manifestNSpace.getPrefix() + ":");
			nspaceSet.add(XmlIngester.manifestNSpace);
		}

		if (xpathDef.contains("{mdmec}")) {
			xpathDef = xpathDef.replaceAll("\\{mdmec\\}", XmlIngester.mdmecNSpace.getPrefix() + ":");
			nspaceSet.add(XmlIngester.mdmecNSpace);
		}
		if (varMap != null) {
			Set<String> keySet = varMap.keySet();
			for (String varID : keySet) {
				String varValue = varMap.get(varID);
				String regExp = "\\{\\" + varID + "\\}";
				xpathDef = xpathDef.replaceAll(regExp, "'" + varValue + "'");
			}
		}

		// Now compile the XPath
		XPathExpression<?> xpExpression;
		/**
		 * The following are examples of xpaths that return an attribute value and that
		 * we therefore need to identity:
		 * <ul>
		 * <li>avail:Term/@termName</li>
		 * <li>avail:Term[@termName[.='Tier' or .='WSP' or .='DMRP']</li>
		 * <li>@contentID</li>
		 * </ul>
		 * whereas the following should NOT match:
		 * <ul>
		 * <li>avail:Term/avail:Event[../@termName='AnnounceDate']</li>
		 * </ul>
		 */
		XPathFactory xpfac = XPathFactory.instance();
		if (resolvesToAttribute(xpathDef)) {
			// must be an attribute value we're after..
			xpExpression = xpfac.compile(xpathDef, Filters.attribute(), null, nspaceSet);
		} else {
			xpExpression = xpfac.compile(xpathDef, Filters.element(), null, nspaceSet);
		}
		return xpExpression;
	}

	private static boolean resolvesToAttribute(String xpathDef) {
		return xpathDef.matches(".*/@[\\w]++(\\[.+\\])?");
	}

	private LogReference resolveDocRef(String docRef) {
		String docStandard = null;
		String docSection = null;
		LogReference srcRef = null;
		if (docRef != null && !docRef.isEmpty()) {
			String[] parts = docRef.split(":");
			if (parts.length >= 2) {
				docStandard = parts[0];
				docSection = parts[1];
				srcRef = LogReference.getRef(docStandard, docSection);
			}
		}
		return srcRef;
	}
}

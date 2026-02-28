/*
 * Copyright (c) 2024 Herve Girod. All rights reserved.
 DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA

 If you have any questions about this project, you can visit
 the project website at the project page on https://sourceforge.net/projects/xmldiff/
 */
package org.xmldiff.core.config;

import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.PatternSyntaxException;
import org.mdiutil.xml.ResolverSAXHandler;
import org.mdiutil.xml.XMLSAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * A parser which parses the list of node rules.
 *
 * @version 0.5
 */
public class NodeRulesParser {
   private NodeRules nodeRules = null;
   private URL nodeRulesURL = null;
   private ParserErrorListener errorListener = null;

   /**
    * Constructor.
    */
   public NodeRulesParser() {
   }

   public void setErrorListener(ParserErrorListener errorListener) {
      this.errorListener = errorListener;
   }

   /**
    * Parse the nodeRules.
    *
    * @return the nodeRules
    */
   public NodeRules parseNodeRules() {
      XMLDiffConfiguration conf = XMLDiffConfiguration.getInstance();
      nodeRulesURL = conf.getNodeRulesURL();

      XMLSAXParser parser = new XMLSAXParser("nodeRules");
      parser.setValidating(true);
      parser.showExceptions(false);
      parser.showWarnings(false);
      parser.setSchema(conf.getNodeRulesSchema());
      NodeRulesHandler handler = new NodeRulesHandler();
      parser.setHandler(handler);
      nodeRules = new NodeRules();
      conf.setNodeRules(nodeRules);
      parser.parse(nodeRulesURL);
      List<ResolverSAXHandler.ExceptionResult> exceptions = handler.getExceptionResults();
      if (errorListener != null && !exceptions.isEmpty()) {
         Iterator<ResolverSAXHandler.ExceptionResult> it = exceptions.iterator();
         while (it.hasNext()) {
            ResolverSAXHandler.ExceptionResult result = it.next();
            SAXParseException e = result.getSAXParseException();
            switch (result.getExceptionType()) {
               case ResolverSAXHandler.FATAL:
                  errorListener.error(e);
                  break;
               case ResolverSAXHandler.ERRORS:
                  errorListener.error(e);
                  break;
               case ResolverSAXHandler.WARNINGS:
                  errorListener.error(e);
                  break;
            }
         }
      }
      return nodeRules;
   }

   /**
    * The nodeRules handler.
    */
   private class NodeRulesHandler extends ResolverSAXHandler {
      private static final String REMOVE_DESCRIPTION_NEWLINES = "keepDescriptionNewLines";
      private static final String DESCRIPTION_TRIM = "descriptionTrim";
      private static final String CDATA_TRIM = "CDATATrim";
      private static final String KEEP_CDATA_NEWLINES = "keepCDATANewLines";
      private static final String COMPARISON_MODE = "comparisonMode";
      private static final String ORDER_IS_SIGNIFICANT = "orderIsSignificant";
      private static final String COMPARE_CDATA = "compareCDATA";
      private boolean isUniqueNodeName = true;
      private Rule rule = null;
      private String ruleName = null;
      private Set<String> nodenames = new HashSet<>();
      private boolean removeCDATANewLines = true;
      private String identifierName = null;
      private char defaultComparisonMode = NodeComparisonMode.ANY_DIFF_OTHER;
      private boolean isExtendingAttrs = false;
      private boolean isExcludingAttrs = false;
      private boolean isExtendingDesc = false;
      private boolean inIdentification = false;
      private boolean hasDescriptions = false;
      private boolean inCDATA = false;
      private final Set<String> ruleProperties = new HashSet<>();

      private NodeRulesHandler() {
      }

      /**
       * Receive notification of the beginning of an element.
       *
       * @param uri the Namespace URI
       * @param localname the local name (without prefix), or the empty string if Namespace processing is not being performed
       * @param qname The qualified name (with prefix), or the empty string if qualified names are not available
       * @param attr the specified or defaulted attributes
       */
      @Override
      public void startElement(String uri, String localname, String qname, Attributes attr) throws SAXException {
         switch (qname) {
            case "nodeRules":
               parseRoot(attr);
               break;
            case "defaultRule":
               createDefaultRule(attr);
               break;
            case "rule":
               parseNodeRule(attr);
               break;
            case "extendsDefaultRule":
               parseExtendsDefaultRule();
               break;
            case "extendsRule":
               parseExtendsRule(attr);
               break;
            case "appliesOn":
               if (!isUniqueNodeName) {
                  nodenames = new HashSet<>();
               }
               break;
            case "identification":
               this.inIdentification = true;
               break;
            case "descriptions":
               parseDescriptions(attr);
               break;
            case "CDATA":
               parseCDATA(attr);
               break;
            case "applyRegex":
               if (hasDescriptions) {
                  parseDescriptionsRegexReplace(attr);
               } else if (inCDATA) {
                  parseCDATARegexReplace(attr);
               }
               break;
            case "nodeName":
               if (!isUniqueNodeName) {
                  parseNodeName(attr);
               }
               break;
            case "attribute":
               if (rule != null) {
                  parseAttribute(attr);
               }
               break;
            case "description":
               if (rule != null) {
                  parseDescriptionAttribute(attr);
               }
               break;
            default:
               break;
         }
      }

      @Override
      public void endElement(String uri, String localname, String qname) {
         switch (qname) {
            case "defaultRule":
               rule = null;
               break;
            case "rule":
               rule = null;
               break;
            case "appliesOn":
               if (!isUniqueNodeName && !nodenames.isEmpty()) {
                  nodeRules.setNodeNames(rule, nodenames);
               }
               break;
            case "CDATA":
               inCDATA = false;
               break;
            default:
               break;
         }
      }

      private void createDefaultRule(Attributes attr) {
         rule = nodeRules.getDefaultRule();
         boolean orderIsSignificant = true;
         boolean compareDATA = true;

         for (int i = 0; i < attr.getLength(); i++) {
            String attrname = attr.getQName(i);
            String attrvalue = attr.getValue(i);

            switch (attrname) {
               case "orderIsSignificant":
                  orderIsSignificant = attrvalue.equals("true");
                  rule.setOrderIsSignificant(orderIsSignificant);
                  break;
               case "compareCDATA":
                  compareDATA = attrvalue.equals("true");
                  rule.setCompareCDATA(compareDATA);
                  break;
               case "removeCDATANewLines":
                  removeCDATANewLines = attrvalue.equals("true");
                  rule.removeCDATANewLines(removeCDATANewLines);
                  break;
               case "comparisonMode":
                  defaultComparisonMode = parseDefaultComparisonMode(attrvalue);
                  rule.setComparisonMode(defaultComparisonMode);
                  break;
               default:
                  break;
            }
         }
      }

      private void parseRoot(Attributes attr) {
         boolean orderIsSignificant = true;
         inIdentification = false;
         boolean compareCDATA = true;

         for (int i = 0; i < attr.getLength(); i++) {
            String attrname = attr.getQName(i);
            String attrvalue = attr.getValue(i);

            switch (attrname) {
               case "defaultComparisonMode":
                  defaultComparisonMode = parseDefaultComparisonMode(attrvalue);
                  break;
               case "compareCDATA":
                  compareCDATA = attrvalue.equals("true");
                  break;
               case "removeCDATANewLines":
                  removeCDATANewLines = attrvalue.equals("true");
                  break;
               case "orderIsSignificant":
                  orderIsSignificant = attrvalue.equals("true");
                  break;
               default:
                  break;
            }
         }
         nodeRules.setDefaultComparisonMode(defaultComparisonMode);
         nodeRules.removeCDATANewLines(removeCDATANewLines);
         nodeRules.setCompareCDATA(compareCDATA);
         nodeRules.setOrderIsSignificant(orderIsSignificant);
      }

      private char parseDefaultComparisonMode(String attrvalue) {
         switch (attrvalue) {
            case "SameNodeName":
               return NodeComparisonMode.SAME_NODENAME_SAME;
            default:
               return NodeComparisonMode.ANY_DIFF_OTHER;
         }
      }

      private char parseComparisonMode(String attrvalue) {
         switch (attrvalue) {
            case "SameNodeName":
               return NodeComparisonMode.SAME_NODENAME_SAME;
            case "OnAttributes":
               return NodeComparisonMode.ON_ATTRIBUTES;
            default:
               return defaultComparisonMode;
         }
      }

      private void parseNodeRule(Attributes attr) {
         identifierName = null;
         isUniqueNodeName = false;
         ruleName = null;
         isExtendingAttrs = false;
         isExtendingDesc = false;
         inIdentification = false;
         hasDescriptions = false;
         inCDATA = false;
         ruleProperties.clear();
         boolean orderIsSignificant = true;
         boolean _keepCDATANewLines = false;
         boolean _compareDATA = true;
         char _comparisonMode = NodeComparisonMode.ANY_DIFF_OTHER;

         for (int i = 0; i < attr.getLength(); i++) {
            String attrname = attr.getQName(i);
            String attrvalue = attr.getValue(i);

            switch (attrname) {
               case "ruleName":
                  ruleName = attrvalue.trim();
                  break;
               case "nodeName":
                  identifierName = attrvalue.trim();
                  break;
               case "keepCDATANewLines":
                  _keepCDATANewLines = attrvalue.equals("true");
                  ruleProperties.add(KEEP_CDATA_NEWLINES);
                  break;
               case "compareCDATA":
                  _compareDATA = attrvalue.equals("true");
                  ruleProperties.add(COMPARE_CDATA);
                  break;
               case "orderIsSignificant":
                  orderIsSignificant = attrvalue.equals("true");
                  ruleProperties.add(ORDER_IS_SIGNIFICANT);
                  break;
               case "comparisonMode":
                  _comparisonMode = parseComparisonMode(attrvalue);
                  ruleProperties.add(COMPARISON_MODE);
                  break;
            }
         }
         if (identifierName != null) {
            isUniqueNodeName = true;
            rule = nodeRules.createNodeRule(ruleName, identifierName);
         } else {
            isUniqueNodeName = false;
            rule = nodeRules.createNodeRule(ruleName);
         }
         rule.removeCDATANewLines(_keepCDATANewLines);
         rule.setOrderIsSignificant(orderIsSignificant);
         rule.setCompareCDATA(_compareDATA);
         rule.setComparisonMode(_comparisonMode);
      }

      private char parseTrimType(String attrvalue) {
         switch (attrvalue) {
            case "Trim":
               return TrimType.TRIM;
            case "TrimRight":
               return TrimType.TRIM_RIGHT;
            case "TrimLeft":
               return TrimType.TRIM_LEFT;               
            default:
               return TrimType.NO;
         }
      }

      private void parseDescriptions(Attributes attr) {
         boolean removeDescriptionNewLines = false;
         char trimType = TrimType.NO;

         for (int i = 0; i < attr.getLength(); i++) {
            String attrname = attr.getQName(i);
            String attrvalue = attr.getValue(i);

            switch (attrname) {
               case "removeNewLines":
                  removeDescriptionNewLines = attrvalue.equals("true");
                  ruleProperties.add(REMOVE_DESCRIPTION_NEWLINES);
                  break;
               case "trimType":
                  trimType = parseTrimType(attrvalue);
                  ruleProperties.add(DESCRIPTION_TRIM);
                  break;
            }
         }
         hasDescriptions = true;
         rule.removeDescriptionNewLines(removeDescriptionNewLines);
         rule.setDescriptionTrimType(trimType);
      }

      private void parseCDATA(Attributes attr) {
         boolean removeCATANewLines = false;
         char trimType = TrimType.NO;

         for (int i = 0; i < attr.getLength(); i++) {
            String attrname = attr.getQName(i);
            String attrvalue = attr.getValue(i);

            switch (attrname) {
               case "removeNewLines":
                  removeCATANewLines = attrvalue.equals("true");
                  ruleProperties.add(KEEP_CDATA_NEWLINES);
                  break;
               case "trimType":
                  trimType = parseTrimType(attrvalue);
                  ruleProperties.add(CDATA_TRIM);
                  break;
               case "excludeCDATA":
                  boolean compareCDATA = !attrvalue.equals("true");
                  rule.setCompareCDATA(compareCDATA);
                  ruleProperties.add(COMPARE_CDATA);
                  break;                  
            }
         }
         inCDATA = true;
         rule.removeCDATANewLines(!removeCATANewLines);
         rule.setCDATATrimType(trimType);
      }

      private void parseDescriptionsRegexReplace(Attributes attr) {
         String from = null;
         String to = null;

         for (int i = 0; i < attr.getLength(); i++) {
            String attrname = attr.getQName(i);
            String attrvalue = attr.getValue(i);

            switch (attrname) {
               case "replaceFrom":
                  from = attrvalue;
                  break;
               case "replaceTo":
                  to = attrvalue;
                  break;
            }
         }
         if (from != null && to != null) {
            try {
               RuleRegexReplace regexReplace = rule.createRuleDescriptionRegexReplace();
               regexReplace.addReplacement(from, to);
            } catch (PatternSyntaxException e) {
               this.error(new SAXParseException(e.getMessage(), locator));
            }
         }
      }

      private void parseCDATARegexReplace(Attributes attr) {
         String from = null;
         String to = null;

         for (int i = 0; i < attr.getLength(); i++) {
            String attrname = attr.getQName(i);
            String attrvalue = attr.getValue(i);

            switch (attrname) {
               case "replaceFrom":
                  from = attrvalue;
                  break;
               case "replaceTo":
                  to = attrvalue;
                  break;
            }
         }
         if (from != null && to != null) {
            try {
               RuleRegexReplace regexReplace = rule.createRuleCDATARegexReplace();
               regexReplace.addReplacement(from, to);
            } catch (PatternSyntaxException e) {
               this.error(new SAXParseException(e.getMessage(), locator));
            }
         }
      }

      private void doExtendRule(Rule extendedRule) {
         isExtendingAttrs = true;
         isExtendingDesc = true;
         if (!ruleProperties.contains(ORDER_IS_SIGNIFICANT)) {
            rule.setOrderIsSignificant(extendedRule.isOrderSignificant());
         }
         if (!ruleProperties.contains(COMPARE_CDATA)) {
            rule.setOrderIsSignificant(extendedRule.isComparingCDATA());
         }
         if (!ruleProperties.contains(COMPARISON_MODE)) {
            rule.setComparisonMode(extendedRule.getComparisonMode());
         }
         if (!ruleProperties.contains(REMOVE_DESCRIPTION_NEWLINES)) {
            rule.removeDescriptionNewLines(extendedRule.isRemovingDescriptionNewLines());
         }
         if (!ruleProperties.contains(DESCRIPTION_TRIM)) {
            rule.setDescriptionTrimType(extendedRule.getDescriptionTrimType());
         }
         if (!ruleProperties.contains(CDATA_TRIM)) {
            rule.setCDATATrimType(extendedRule.getCDATATrimType());
         }         
         if (!ruleProperties.contains(KEEP_CDATA_NEWLINES)) {
            rule.removeCDATANewLines(extendedRule.isKeepingCDATANewLines());
         }

         Iterator<String> it = extendedRule.getIdentificationAttributes().iterator();
         while (it.hasNext()) {
            String attrName = it.next();
            rule.addIdentificationAttribute(attrName);
         }
         it = extendedRule.getDescriptionAttributes().iterator();
         while (it.hasNext()) {
            String attrName = it.next();
            rule.addDescriptionAttribute(attrName);
         }
      }

      private void parseExtendsDefaultRule() {
         if (rule == null) {
            return;
         }
         Rule defaultRule = nodeRules.getDefaultRule();
         doExtendRule(defaultRule);
      }

      private void parseExtendsRule(Attributes attr) {
         if (rule == null) {
            return;
         }
         String extendsRuleName = null;
         for (int i = 0; i < attr.getLength(); i++) {
            String attrname = attr.getQName(i);
            String attrvalue = attr.getValue(i);
            if (attrname.equals("")) {
               extendsRuleName = attrvalue;
            }
         }
         if (extendsRuleName != null) {
            if (nodeRules.hasRuleByID(extendsRuleName)) {
               Rule extendedRule = nodeRules.getRuleByID(extendsRuleName);
               doExtendRule(extendedRule);
            }
         }
      }

      private void parseNodeName(Attributes attr) {
         String nodeName = null;

         for (int i = 0; i < attr.getLength(); i++) {
            String attrname = attr.getQName(i);
            String attrvalue = attr.getValue(i);

            if (attrname.equals("name")) {
               nodeName = attrvalue.trim();
            }
         }
         if (nodeName != null) {
            nodenames.add(nodeName);
         }
      }

      private void parseAttribute(Attributes attr) {
         String name = null;

         for (int i = 0; i < attr.getLength(); i++) {
            String attrname = attr.getQName(i);
            String attrvalue = attr.getValue(i);

            if (attrname.equals("name")) {
               name = attrvalue.trim();
            }
         }
         if (name != null && rule != null) {
            if (inIdentification) {
               if (isExtendingAttrs) {
                  rule.getIdentificationAttributes().clear();
                  isExtendingAttrs = false;
               }
               rule.addIdentificationAttribute(name);
            } else {
               if (isExtendingAttrs) {
                  rule.getExcludedAttributes().clear();
                  isExcludingAttrs = false;
               }
               rule.addExcludedAttribute(name);
            }
         }
      }

      private void parseDescriptionAttribute(Attributes attr) {
         String name = null;

         for (int i = 0; i < attr.getLength(); i++) {
            String attrname = attr.getQName(i);
            String attrvalue = attr.getValue(i);

            if (attrname.equals("name")) {
               name = attrvalue.trim();
            }
         }
         if (name != null && rule != null) {
            if (isExtendingDesc) {
               rule.getDescriptionAttributes().clear();
               isExtendingDesc = false;
            }
            rule.addDescriptionAttribute(name);
         }
      }
   }
}

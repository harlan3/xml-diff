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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.mdiutil.xml.tree.SortableQName;
import org.mdiutil.xml.tree.XMLNode;

/**
 * Represents one node rule.
 *
 * @version 0.6
 */
public class Rule {
   /**
    * Identifies the fact that the left and right nodes are identical.
    */
   public static final char STATUS_IDENTICAL = 0;
   /**
    * Identifies the fact that the left and right nodes are different.
    */
   public static final char STATUS_DIFFERENT = 1;
   /**
    * Identifies the fact that the left and right nodes are updated.
    */
   public static final char STATUS_UPDATED = 2;
   private static final Pattern TRIM_RIGHT = Pattern.compile("(.*\\S)\\s*");
   private static final Pattern TRIM_LEFT = Pattern.compile("\\s*(\\S.*)");
   private final Set<String> identAttributes = new HashSet<>();
   private final Set<String> excludedAttributes = new HashSet<>();
   private final Set<String> descriptionsAttrs = new HashSet<>();
   private char comparisonMode = NodeComparisonMode.ON_ATTRIBUTES;
   private boolean removeDescriptionNewLines = true;
   private char descriptionTrim = TrimType.NO;
   private char cdataTrim = TrimType.NO;
   private boolean keepCDATANewLines = true;
   private boolean orderIsSignificant = true;
   private boolean compareCDATA = true;
   private RuleRegexReplace descRegexReplace = null;
   private RuleRegexReplace cdataRegexReplace = null;
   private String id;

   public Rule() {
   }

   /**
    * Set the rule id.
    *
    * @param id the id
    */
   public void setID(String id) {
      this.id = id;
   }

   /**
    * Return the rule id.
    *
    * @return the id
    */
   public String getID() {
      return id;
   }

   @Override
   public String toString() {
      if (id != null) {
         return id;
      } else {
         return "";
      }
   }

   /**
    * Set if CDATA content is compared.
    *
    * @param compareCDATA true if CDATA content is compared
    */
   public void setCompareCDATA(boolean compareCDATA) {
      this.compareCDATA = compareCDATA;
   }

   /**
    * Return true if CDATA content is compared.
    *
    * @return true if CDATA content is compared
    */
   public boolean isComparingCDATA() {
      return compareCDATA;
   }

   /**
    * Set if the ordering of nodes is significant.
    *
    * @param orderIsSignificant true if the ordering of nodes is significant
    */
   public void setOrderIsSignificant(boolean orderIsSignificant) {
      this.orderIsSignificant = orderIsSignificant;
   }

   /**
    * Return true if the ordering of nodes is significant.
    *
    * @return true if the ordering of nodes is significant
    */
   public boolean isOrderSignificant() {
      return orderIsSignificant;
   }

   /**
    * Set the way CDATA new lines are considered. If true, new lines at the end of CDATA will be kept.
    *
    * @param keepCDATANewLines true if new lines at the end of CDATA will be kept
    */
   public void removeCDATANewLines(boolean keepCDATANewLines) {
      this.keepCDATANewLines = keepCDATANewLines;
   }

   /**
    * Return true if new lines at the end of CDATA will be kept.
    *
    * @return true if new lines at the end of CDATA will be kept
    */
   public boolean isKeepingCDATANewLines() {
      return keepCDATANewLines;
   }

   /**
    * Set the way CDATA new lines are considered. If true, new lines at the end of description attributes will be removed.
    *
    * @param removeDescriptionNewLines true if new lines at the end of description attributes will be removed
    */
   public void removeDescriptionNewLines(boolean removeDescriptionNewLines) {
      this.removeDescriptionNewLines = removeDescriptionNewLines;
   }

   /**
    * Return true if new lines at the end of description attributes will be removed.
    *
    * @return true if new lines at the end of description attributes will be removed
    */
   public boolean isRemovingDescriptionNewLines() {
      return removeDescriptionNewLines;
   }

   /**
    * Set the type of trim on description attributes.
    *
    * @param trimType the type of trim
    */
   public void setDescriptionTrimType(char trimType) {
      this.descriptionTrim = trimType;
   }

   /**
    * Return the type of trim on description attributes.
    *
    * @return the type of trim
    */
   public char getDescriptionTrimType() {
      return descriptionTrim;
   }

   /**
    * Set the type of trim on CDATA content.
    *
    * @param trimType the type of trim
    */
   public void setCDATATrimType(char trimType) {
      this.cdataTrim = trimType;
   }

   /**
    * Return the type of trim on on CDATA content.
    *
    * @return the type of trim
    */
   public char getCDATATrimType() {
      return cdataTrim;
   }

   /**
    * Set the comparison mode.
    *
    * @param comparisonMode the comparison mode
    * @see NodeComparisonMode
    */
   public void setComparisonMode(char comparisonMode) {
      this.comparisonMode = comparisonMode;
   }

   /**
    * Return the comparison mode.
    *
    * @return the comparison mode
    * @see NodeComparisonMode
    */
   public char getComparisonMode() {
      return comparisonMode;
   }

   /**
    * Return the attributes which are managed by this rule.
    *
    * @return the attributes names
    */
   public Set<String> getIdentificationAttributes() {
      return identAttributes;
   }

   /**
    * Add an attribute which is managed by this rule.
    *
    * @param attrName the attribute name
    */
   public void addIdentificationAttribute(String attrName) {
      identAttributes.add(attrName);
      this.comparisonMode = NodeComparisonMode.ON_ATTRIBUTES;
   }

   /**
    * Return the attributes which are excluded by this rule.
    *
    * @return the attributes names
    */
   public Set<String> getExcludedAttributes() {
      return excludedAttributes;
   }

   /**
    * Add an attribute which is excluded by this rule.
    *
    * @param attrName the attribute name
    */
   public void addExcludedAttribute(String attrName) {
      excludedAttributes.add(attrName);
   }

   /**
    * Add an attribute which is considered as a description attribute for this rule.
    *
    * @param attrName the attribute name
    */
   public void addDescriptionAttribute(String attrName) {
      descriptionsAttrs.add(attrName);
      this.comparisonMode = NodeComparisonMode.ON_ATTRIBUTES;
   }

   /**
    * Return the attributes which are considered as description attributes by this rule.
    *
    * @return the attributes names
    */
   public Set<String> getDescriptionAttributes() {
      return descriptionsAttrs;
   }

   /**
    * Return true if an attribute is considered as a description attribute for this rule.
    *
    * @param attrName the attribute name
    * @return rue if an attribute is considered as a description attribute
    */
   public boolean isDescriptionAttribute(String attrName) {
      return descriptionsAttrs.contains(attrName);
   }

   /**
    * Return the status of attributes comparisons for this rule. The status can be:
    * <ul>
    * <li>{@link Rule#STATUS_IDENTICAL} if the nodes are considered identical</li>
    * <li>{@link Rule#STATUS_DIFFERENT} if the nodes are considered two different nodes</li>
    * <li>{@link Rule#STATUS_UPDATED} if the nodes are considered to be the same node but their content is updated</li>
    * </ul>
    *
    * @param leftNode the left XML node
    * @param rightNode the right XML node
    * @return the status
    */
   public char getStatus(XMLNode leftNode, XMLNode rightNode) {
      Map<String, String> leftAttrs = getAttributes(leftNode);
      Map<String, String> rightAttrs = getAttributes(rightNode);
      String leftCDATA = leftNode.getCDATA();
      String rightCDATA = rightNode.getCDATA();
      switch (comparisonMode) {
         case NodeComparisonMode.ON_ATTRIBUTES:
            return getStatusOnAttrs(leftAttrs, rightAttrs, leftCDATA, rightCDATA);
         case NodeComparisonMode.SAME_NODENAME_SAME:
            return compareAllAttributes(leftAttrs, rightAttrs, false);
         default:
            char status = compareAllAttributes(leftAttrs, rightAttrs, true);
            if (status == STATUS_IDENTICAL && !compareCDATA(leftCDATA, rightCDATA)) {
               return STATUS_UPDATED;
            }
            return status;
      }
   }

   private Map<String, String> getAttributes(XMLNode node) {
      Map<String, String> attrs = new HashMap<>();
      Iterator<Map.Entry<SortableQName, String>> it = node.getAttributes().entrySet().iterator();
      while (it.hasNext()) {
         Map.Entry<SortableQName, String> entry = it.next();
         SortableQName qName = entry.getKey();
         String prefixedName = qName.getCompleteName();
         if (!excludedAttributes.contains(prefixedName)) {
            attrs.put(prefixedName, entry.getValue());
         }
      }
      return attrs;
   }

   private char compareAllAttributes(Map<String, String> leftAttrs, Map<String, String> rightAttrs, boolean anyDiff) {
      if (leftAttrs.size() != rightAttrs.size()) {
         return anyDiff ? STATUS_DIFFERENT : STATUS_UPDATED;
      } else {
         Iterator<Map.Entry<String, String>> it = leftAttrs.entrySet().iterator();
         while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            String attrname = entry.getKey();
            String leftvalue = entry.getValue();
            if (!rightAttrs.containsKey(attrname)) {
               return anyDiff ? STATUS_DIFFERENT : STATUS_UPDATED;
            } else if (isDescriptionAttribute(attrname)) {
               String leftValue = leftAttrs.get(attrname);
               String rightValue = rightAttrs.get(attrname);
               switch (descriptionTrim) {
                  case TrimType.TRIM_RIGHT:
                     leftValue = trimRight(leftValue);
                     rightValue = trimRight(rightValue);
                     break;
                  case TrimType.TRIM_LEFT:
                     leftValue = trimLeft(leftValue);
                     rightValue = trimLeft(rightValue);
                     break;
                  case TrimType.TRIM:
                     leftValue = leftValue.trim();
                     rightValue = rightValue.trim();
                     break;
                  default:
                     break;
               }
               if (removeDescriptionNewLines) {
                  leftValue = leftValue.replaceAll("\n", " ");
                  rightValue = rightValue.replaceAll("\n", " ");
                  leftValue = leftValue.replaceAll("\\s+", " ");
                  rightValue = rightValue.replaceAll("\\s+", " ");
               }
               if (!leftValue.equals(rightValue)) {
                  return anyDiff ? STATUS_DIFFERENT : STATUS_UPDATED;
               }
            } else if (!rightAttrs.get(attrname).equals(leftvalue)) {
               return anyDiff ? STATUS_DIFFERENT : STATUS_UPDATED;
            }
         }
         return STATUS_IDENTICAL;
      }
   }

   private char compareNodesExceptIDs(Map<String, String> leftAttrs, Map<String, String> rightAttrs, String leftCDATA, String rightCDATA) {
      if (leftAttrs.size() != rightAttrs.size()) {
         return STATUS_UPDATED;
      } else {
         Iterator<Map.Entry<String, String>> it = leftAttrs.entrySet().iterator();
         while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            String attrname = entry.getKey();
            if (identAttributes.contains(attrname)) {
               continue;
            }
            String leftvalue = entry.getValue();
            if (!rightAttrs.containsKey(attrname)) {
               return STATUS_UPDATED;
            } else if (isDescriptionAttribute(attrname)) {
               String leftValue = leftAttrs.get(attrname);
               String rightValue = rightAttrs.get(attrname);
               switch (descriptionTrim) {
                  case TrimType.TRIM_RIGHT:
                     leftValue = trimRight(leftValue);
                     rightValue = trimRight(rightValue);
                     break;
                  case TrimType.TRIM_LEFT:
                     leftValue = trimLeft(leftValue);
                     rightValue = trimLeft(rightValue);
                     break;
                  case TrimType.TRIM:
                     leftValue = leftValue.trim();
                     rightValue = rightValue.trim();
                     break;
                  default:
                     break;
               }
               if (removeDescriptionNewLines) {
                  leftValue = leftValue.replaceAll("\n", " ");
                  rightValue = rightValue.replaceAll("\n", " ");
                  leftValue = leftValue.replaceAll("\\s+", " ");
                  rightValue = rightValue.replaceAll("\\s+", " ");
               }
               if (descRegexReplace != null) {
                  leftValue = descRegexReplace.apply(leftValue);
                  rightValue = descRegexReplace.apply(rightValue);
               }
               if (!leftValue.equals(rightValue)) {
                  return STATUS_UPDATED;
               }
            } else if (!rightAttrs.get(attrname).equals(leftvalue)) {
               return STATUS_UPDATED;
            }
         }
         if (compareCDATA && !compareCDATA(leftCDATA, rightCDATA)) {
            return STATUS_UPDATED;
         }
         return STATUS_IDENTICAL;
      }
   }

   private char getStatusOnAttrs(Map<String, String> leftAttrs, Map<String, String> rightAttrs, String leftCDATA, String rightCDATA) {
      Iterator<String> it = identAttributes.iterator();
      while (it.hasNext()) {
         String attrname = it.next();
         if (!leftAttrs.containsKey(attrname) && rightAttrs.containsKey(attrname)) {
            return STATUS_DIFFERENT;
         } else if (leftAttrs.containsKey(attrname) && !rightAttrs.containsKey(attrname)) {
            return STATUS_DIFFERENT;
         } else if (isDescriptionAttribute(attrname)) {
            String leftValue = leftAttrs.get(attrname);
            String rightValue = rightAttrs.get(attrname);
            switch (descriptionTrim) {
               case TrimType.TRIM_RIGHT:
                  leftValue = trimRight(leftValue);
                  rightValue = trimRight(rightValue);
                  break;
               case TrimType.TRIM_LEFT:
                  leftValue = trimLeft(leftValue);
                  rightValue = trimLeft(rightValue);
                  break;
               case TrimType.TRIM:
                  leftValue = leftValue.trim();
                  rightValue = rightValue.trim();
                  break;
               default:
                  break;
            }
            if (removeDescriptionNewLines) {
               leftValue = leftValue.replaceAll("\n", " ");
               rightValue = rightValue.replaceAll("\n", " ");
               leftValue = leftValue.replaceAll("\\s+", " ");
               rightValue = rightValue.replaceAll("\\s+", " ");
            }
            if (descRegexReplace != null) {
               leftValue = descRegexReplace.apply(leftValue);
               rightValue = descRegexReplace.apply(rightValue);
            }
            if (!leftValue.equals(rightValue)) {
               return STATUS_DIFFERENT;
            }
         } else if (leftAttrs.containsKey(attrname) && rightAttrs.containsKey(attrname)) {
            String leftValue = leftAttrs.get(attrname);
            String rightValue = rightAttrs.get(attrname);
            if (!leftValue.equals(rightValue)) {
               return STATUS_DIFFERENT;
            }
         }
      }
      return compareNodesExceptIDs(leftAttrs, rightAttrs, leftCDATA, rightCDATA);
   }

   /**
    * Return true if two nodes are considered equal.
    *
    * @param node1 the first node
    * @param node2 the second node
    * @return true if the two nodes are considered equal
    */
   public boolean equals(XMLNode node1, XMLNode node2) {
      if (node1 != null && node2 == null) {
         return false;
      } else if (node1 == null && node2 != null) {
         return false;
      } else if (!node1.getPrefixedName().equals(node2.getPrefixedName())) {
         return false;
      } else {
         String cdata1 = node1.getCDATA();
         String cdata2 = node2.getCDATA();
         if (cdata1 == null && cdata2 != null) {
            return false;
         } else if (cdata1 != null && cdata2 == null) {
            return false;
         } else if (cdata1 != null && cdata2 != null) {
            cdata1 = cdata1.trim();
            cdata2 = cdata2.trim();
            if (!cdata1.equals(cdata2)) {
               return false;
            }
         }
         if (compareCDATA) {
            return compareCDATA(node1.getCDATA(), node2.getCDATA()) && compareAttributes(node1, node2);
         } else {
            return compareAttributes(node1, node2);
         }
      }
   }

   private boolean compareCDATA(String cdata1, String cdata2) {
      if (cdata1 == null && cdata2 != null) {
         return false;
      } else if (cdata1 != null && cdata2 == null) {
         return false;
      } else if (cdata1 != null && cdata2 != null) {
         switch (cdataTrim) {
            case TrimType.TRIM_RIGHT:
               cdata1 = trimRight(cdata1);
               cdata2 = trimRight(cdata2);
               break;
            case TrimType.TRIM_LEFT:
               cdata1 = trimLeft(cdata1);
               cdata2 = trimLeft(cdata2);
               break;
            case TrimType.TRIM:
               cdata1 = cdata1.trim();
               cdata2 = cdata2.trim();
               break;
            default:
               break;
         }
         if (cdataRegexReplace != null) {
            cdata1 = cdataRegexReplace.apply(cdata1);
            cdata2 = cdataRegexReplace.apply(cdata2);
         }
         if (!cdata1.equals(cdata2)) {
            if (keepCDATANewLines) {
               return false;
            } else {
               cdata1 = cdata1.trim().replaceAll("\n", " ");
               cdata2 = cdata2.trim().replaceAll("\n", " ");
               cdata1 = cdata1.replaceAll("\\s+", " ");
               cdata2 = cdata2.replaceAll("\\s+", " ");
               if (!cdata1.equals(cdata2)) {
                  return false;
               }
            }
         }
      }
      return true;
   }

   private Map<String, String> createAttrsMap(Map<SortableQName, String> map) {
      Map<String, String> output = new HashMap<>();
      Iterator<Map.Entry<SortableQName, String>> it = map.entrySet().iterator();
      while (it.hasNext()) {
         Map.Entry<SortableQName, String> entry = it.next();
         String key = entry.getKey().getCompleteName();
         String value = entry.getValue();
         if (!excludedAttributes.contains(key)) {
            output.put(key, value);
         }
      }
      return output;
   }

   private boolean compareAttributes(XMLNode node1, XMLNode node2) {
      Map<String, String> map1 = createAttrsMap(node1.getAttributes());
      Map<String, String> map2 = createAttrsMap(node2.getAttributes());
      if (map1.size() != map2.size()) {
         return false;
      }
      if (!map1.keySet().equals(map2.keySet())) {
         return false;
      }
      Iterator<Map.Entry<String, String>> it = map1.entrySet().iterator();
      while (it.hasNext()) {
         Map.Entry<String, String> entry = it.next();
         String value1 = entry.getValue();
         String value2 = map2.get(entry.getKey());
         if (value1 == null && value2 != null) {
            return false;
         } else if (value1 != null && value2 == null) {
            return false;
         } else if (value1 != null && value2 != null) {
            if (!value1.equals(value2)) {
               if (isDescriptionAttribute(entry.getKey())) {
                  switch (descriptionTrim) {
                     case TrimType.TRIM_RIGHT:
                        value1 = trimRight(value1);
                        value2 = trimRight(value2);
                        break;
                     case TrimType.TRIM_LEFT:
                        value1 = trimLeft(value1);
                        value2 = trimLeft(value2);
                        break;
                     case TrimType.TRIM:
                        value1 = value1.trim();
                        value2 = value2.trim();
                        break;
                     default:
                        break;
                  }
                  if (removeDescriptionNewLines) {
                     value1 = value1.replaceAll("\n", " ");
                     value2 = value2.trim().replaceAll("\n", " ");
                     value1 = value1.replaceAll("\\s+", " ");
                     value2 = value2.replaceAll("\\s+", " ");
                  }
               }
               return value1.equals(value2);
            }
         }
      }
      return true;
   }

   /**
    * Trim a String at the right.
    *
    * @param str the String
    * @return the trimmed String
    */
   private static String trimRight(String str) {
      if (str.trim().isEmpty()) {
         return "";
      } else {
         Matcher m = TRIM_RIGHT.matcher(str);
         if (m.matches()) {
            return m.group(1);
         } else {
            return str;
         }
      }
   }
   
   /**
    * Trim a String at the left.
    *
    * @param str the String
    * @return the trimmed String
    */
   private static String trimLeft(String str) {
      if (str.trim().isEmpty()) {
         return "";
      } else {
         Matcher m = TRIM_LEFT.matcher(str);
         if (m.matches()) {
            return m.group(1);
         } else {
            return str;
         }
      }
   }   

   /**
    * Create the regex replacement rule for the description attributes. It will only create it if it does not exist.
    *
    * @return the regex replacement rule
    */
   public RuleRegexReplace createRuleDescriptionRegexReplace() {
      if (descRegexReplace == null) {
         this.descRegexReplace = new RuleRegexReplace();
      }
      return descRegexReplace;
   }

   /**
    * Return the regex replacement rule for the description attributes.
    *
    * @return the regex replacement rule
    */
   public RuleRegexReplace getRuleDescriptionRegexReplace() {
      return descRegexReplace;
   }

   /**
    * Return true if there is the regex replacement rule for the description attributes.
    *
    * @return true if there is the regex replacement
    */
   public boolean hasRuleDescriptionRegexReplace() {
      return descRegexReplace != null && !descRegexReplace.isEmpty();
   }

   /**
    * Create the regex replacement rule for the CDATA content. It will only create it if it does not exist.
    *
    * @return the regex replacement rule
    */
   public RuleRegexReplace createRuleCDATARegexReplace() {
      if (cdataRegexReplace == null) {
         this.cdataRegexReplace = new RuleRegexReplace();
      }
      return cdataRegexReplace;
   }

   /**
    * Return the regex replacement rule for the CDATA content.
    *
    * @return the regex replacement rule
    */
   public RuleRegexReplace getRuleCDATARegexReplace() {
      return cdataRegexReplace;
   }

   /**
    * Return true if there is the regex replacement rule for the CDATA content.
    *
    * @return true if there is the regex replacement
    */
   public boolean hasRuleCDATARegexReplace() {
      return cdataRegexReplace != null && !cdataRegexReplace.isEmpty();
   }
}

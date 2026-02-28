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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.mdiutil.xml.tree.XMLNode;
import static org.xmldiff.core.config.Rule.STATUS_DIFFERENT;

/**
 * Represents node rules.
 *
 * @since 0.1
 */
public class NodeRules {
   private final Rule defaultRule;
   private final Map<String, Rule> rulesMapByNode = new HashMap<>();
   private final Map<String, Rule> rulesMapByID = new HashMap<>();

   public NodeRules() {
      defaultRule = new Rule();
   }

   /**
    * Return the default comparison mode.
    *
    * @return the default comparison mode
    * @see NodeComparisonMode
    */
   public char getDefaultComparisonMode() {
      return defaultRule.getComparisonMode();
   }

   /**
    * Return the default rule. Note that it always exist, even if no defauklt rule has been defined in the configuration XML file.
    *
    * @return the default rule
    */
   public Rule getDefaultRule() {
      return defaultRule;
   }

   /**
    * Return the rule associated with an XML node.
    *
    * @param node the XML node
    * @return the rule
    */
   public Rule getRule(XMLNode node) {
      String nodeName = node.getPrefixedName();
      if (rulesMapByNode.containsKey(nodeName)) {
         return rulesMapByNode.get(nodeName);
      } else {
         return defaultRule;
      }
   }

   /**
    * Return the comparison status between two XML nodes. The status can be:
    * <ul>
    * <li>{@link Rule#STATUS_IDENTICAL} if the nodes are considered identical</li>
    * <li>{@link Rule#STATUS_DIFFERENT} if the nodes are considered two different nodes</li>
    * <li>{@link Rule#STATUS_UPDATED} if the nodes are considered to be the same node but their content is updated</li>
    * </ul>
    *
    * @param leftNode the left XML node
    * @param rightNode the right XML node
    * @return the comparison status
    */
   public char getStatus(XMLNode leftNode, XMLNode rightNode) {
      String leftNodeName = leftNode.getPrefixedName();
      String rightNodeName = rightNode.getPrefixedName();
      if (!leftNodeName.equals(rightNodeName)) {
         return STATUS_DIFFERENT;
      }
      if (rulesMapByNode.containsKey(leftNodeName)) {
         return rulesMapByNode.get(leftNodeName).getStatus(leftNode, rightNode);
      } else {
         return defaultRule.getStatus(leftNode, rightNode);
      }
   }

   /**
    * Set the default comparison mode.
    *
    * @param defaultComparisonMode the default comparison mode
    * @see NodeComparisonMode
    */
   public void setDefaultComparisonMode(char defaultComparisonMode) {
      this.defaultRule.setComparisonMode(defaultComparisonMode);
   }

   /**
    * Set the default way CDATA new lines are considered. If true, new lines at the end of CDATA will be removed.
    *
    * @param removeCDATANewLines true if new lines at the end of CDATA will be removed
    */
   public void removeCDATANewLines(boolean removeCDATANewLines) {
      this.defaultRule.removeCDATANewLines(removeCDATANewLines);
   }
   
   /**
    * Set if by default CDATA content is compared.
    *
    * @param compareCDATA true if CDATA content is compared
    */
   public void setCompareCDATA(boolean compareCDATA) {
      this.defaultRule.setCompareCDATA(compareCDATA);
   }   
   
   /**
    * Set if by default the ordering of nodes is significant.
    *
    * @param orderIsSignificant true if the ordering of nodes is significant
    */
   public void setOrderIsSignificant(boolean orderIsSignificant) {
      this.defaultRule.setOrderIsSignificant(orderIsSignificant);
   }   

   /**
    * Return the map of rules by node names.
    *
    * @return the map of rules by node names
    */
   public Map<String, Rule> getRulesByNodeName() {
      return rulesMapByNode;
   }

   /**
    * Return the map of rules by their rule id.
    *
    * @return the map of rules by their rule id
    */
   public Map<String, Rule> getRulesByID() {
      return rulesMapByID;
   }

   /**
    * Return true if there is a rule for a specified id.
    *
    * @param id the rule id
    * @return true if there is a rule for the specified id
    */
   public boolean hasRuleByID(String id) {
      return rulesMapByID.containsKey(id);
   }

   /**
    * Return the rule for a specified id.
    *
    * @param id the rule id
    * @return the rule
    */
   public Rule getRuleByID(String id) {
      return rulesMapByID.get(id);
   }

   /**
    * Return true if an attribute for a node is considered a description attribute.
    *
    * @param nodeName the node name
    * @param attrName the ttribute name
    * @return true if the attribute is considered a description attribute
    */
   public boolean isDescriptionAttribute(String nodeName, String attrName) {
      Rule rule = null;
      if (rulesMapByNode.containsKey(nodeName)) {
         rule = rulesMapByNode.get(nodeName);
      } else if (defaultRule != null) {
         rule = defaultRule;
      }
      if (rule != null) {
         return rule.isDescriptionAttribute(attrName);
      } else {
         return false;
      }
   }

   /** 
    * Sets the node names for a rule.
    * 
    * @param rule the rule
    * @param nodeNames the node names
    */
   public void setNodeNames(Rule rule, Set<String> nodeNames) {
      Iterator<String> it = nodeNames.iterator();
      while (it.hasNext()) {
         rulesMapByNode.put(it.next(), rule);
      }
   }

   /**
    * Create a node rule for a rule id and a node name.
    * @param ruleID the rule id
    * @param nodeName the node name
    * @return the rule
    */
   public Rule createNodeRule(String ruleID, String nodeName) {
      Rule rule = new Rule();
      if (ruleID != null) {
         rule.setID(ruleID);
         rulesMapByID.put(ruleID, rule);
      }
      rulesMapByNode.put(nodeName, rule);
      return rule;
   }

   /**
    * Create a node rule for a rule id.
    * @param ruleID the rule id
    * @return the rule
    */   
   public Rule createNodeRule(String ruleID) {
      Rule rule = new Rule();
      if (ruleID != null) {
         rule.setID(ruleID);
         rulesMapByID.put(ruleID, rule);
      }
      return rule;
   }
}

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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.PropertyResourceBundle;
import org.mdiutil.lang.swing.ResourceUILoader;
import org.xmldiff.core.gui.ErrorWindow;

/**
 * The configuration for the xmldiff tool.
 *
 * @version 0.2
 */
public class XMLDiffConfiguration {
   private static XMLDiffConfiguration conf = null;
   /**
    * The tool version.
    */
   public String version;
   /**
    * The tool date.
    */
   public String date;
   private URL nodeRulesSchema = null;
   private URL nodeRulesURL = null;
   private NodeRules nodeRules = null;

   private XMLDiffConfiguration() {
      // load ressources
      ResourceUILoader loader = new ResourceUILoader("org/xmldiff/core/resources");
      PropertyResourceBundle prb = loader.getPropertyResourceBundle("xmldiff.properties");
      version = prb.getString("version");
      date = prb.getString("date");
      nodeRulesSchema = loader.getURL("nodeRules.xsd");
   }

   /**
    * Return the unique instance.
    *
    * @return the unique instance
    */
   public static XMLDiffConfiguration getInstance() {
      if (conf == null) {
         conf = new XMLDiffConfiguration();
      }
      return conf;
   }

   /**
    * Return the node rules Schema.
    *
    * @return the node rules Schema
    */
   public URL getNodeRulesSchema() {
      return nodeRulesSchema;
   }

   /**
    * Set the node rules URL.
    *
    * @param nodeRulesURL the node rules URL
    * @return the node rules
    */
   public NodeRules setNodeRulesURL(URL nodeRulesURL) {
      this.nodeRulesURL = nodeRulesURL;
      if (nodeRulesURL == null) {
         nodeRules = new NodeRules();
         return nodeRules;
      } else {
         NodeRulesParser parser = new NodeRulesParser();
         ErrorWindow errorWindow = new ErrorWindow();
         parser.setErrorListener(errorWindow);
         NodeRules rules = parser.parseNodeRules();
         if (errorWindow.hasErrors()) {
            errorWindow.showExceptions("Rules Parsing Errors");
         }
         return rules;
      }
   }

   /**
    * Set the node rules File.
    *
    * @param nodeRulesFile the node rules File
    * @return the node rules
    */
   public NodeRules setNodeRulesFile(File nodeRulesFile) {
      if (nodeRulesFile == null) {
         return setNodeRulesURL(null);
      }
      try {
         this.nodeRulesURL = nodeRulesFile.toURI().toURL();
         nodeRules = setNodeRulesURL(nodeRulesURL);
      } catch (MalformedURLException ex) {
      }
      return nodeRules;
   }

   /**
    * Return the node rules File.
    *
    * @return the node rules File
    */
   public File getNodeRulesFile() {
      if (nodeRulesURL == null) {
         return null;
      }
      return new File(nodeRulesURL.getFile());
   }

   /**
    * Return the node rules URL.
    *
    * @return the node rules URL
    */
   public URL getNodeRulesURL() {
      return nodeRulesURL;
   }

   /**
    * Set the node rules.
    *
    * @param nodeRules the node rules
    */
   public void setNodeRules(NodeRules nodeRules) {
      this.nodeRules = nodeRules;
   }

   /**
    * Return the node rules.
    *
    * @return the node rules
    */
   public NodeRules getNodeRules() {
      return nodeRules;
   }
}

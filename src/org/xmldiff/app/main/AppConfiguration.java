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
package org.xmldiff.app.main;

import java.io.File;
import java.util.PropertyResourceBundle;
import java.util.prefs.Preferences;
import org.mdi.bootstrap.Configuration;
import org.mdiutil.lang.swing.ResourceUILoader;
import org.mdiutil.prefs.PreferencesHelper;
import org.mdiutil.swing.ExtensionFileFilter;
import org.xmldiff.core.config.XMLDiffConfiguration;

/**
 * The configuration for the xmldiff tool.
 *
 * @version 0.3
 */
public class AppConfiguration implements Configuration {
   private static AppConfiguration conf = null;
   private final XMLDiffConfiguration xmldiffConf;
   /**
    * The tool version.
    */
   public String version;
   /**
    * The tool date.
    */
   public String date;
   /**
    * The window width.
    */
   public int width = 0;
   /**
    * The window height.
    */
   public int height = 0;
   public final ExtensionFileFilter xmlfilter;
   public ExtensionFileFilter xsdfilter;
   private File lastDirectory = null;
   private boolean keepSelectedDirectory = true;

   private AppConfiguration() {
      xmldiffConf = XMLDiffConfiguration.getInstance();
      // load ressources
      ResourceUILoader loader = new ResourceUILoader("org/xmldiff/app/resources");
      PropertyResourceBundle prb = loader.getPropertyResourceBundle("app.properties");
      width = Integer.parseInt(prb.getString("width"));
      height = Integer.parseInt(prb.getString("height"));

      String[] ext = {"xml"};
      xmlfilter = new ExtensionFileFilter(ext, "XML Files");
      String[] extXSD = {"xsd"};
      xsdfilter = new ExtensionFileFilter(extXSD, "XML Schemas");
   }

   /**
    * Return the unique instance.
    *
    * @return the unique instance
    */
   public static AppConfiguration getInstance() {
      if (conf == null) {
         conf = new AppConfiguration();
      }
      return conf;
   }

   public void keepSelectedDirectory(boolean keepDirectory) {
      this.keepSelectedDirectory = keepDirectory;
   }

   public boolean isKeepingSelectedDirectory() {
      return keepSelectedDirectory;
   }

   public void setLastDirectory(File dir) {
      this.lastDirectory = dir;
   }

   public File getLastDirectory() {
      return lastDirectory;
   }

   @Override
   public void putConfiguration(Preferences p, File file) {
      PreferencesHelper.putFile(p, "nodeRules", xmldiffConf.getNodeRulesFile());
      p.putBoolean("keepDirectory", keepSelectedDirectory);
      PreferencesHelper.putFile(p, "lastDirectory", lastDirectory);
   }

   @Override
   public void getConfiguration(Preferences p, File file) {
      File nodeRulesFile = PreferencesHelper.getFile(p, "nodeRules", xmldiffConf.getNodeRulesFile());
      xmldiffConf.setNodeRulesFile(nodeRulesFile);
      keepSelectedDirectory = p.getBoolean("keepDirectory", keepSelectedDirectory);
      File dir = PreferencesHelper.getFile(p, "lastDirectory", lastDirectory);
      if (dir != null && dir.isDirectory() && dir.exists()) {
         lastDirectory = dir;
      } else {
         lastDirectory = new File(System.getProperty("user.dir"));
      }
   }
}

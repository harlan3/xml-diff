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

import java.awt.Frame;
import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.JFileChooser;
import org.mdi.app.LauncherConf;
import org.mdi.app.swing.AbstractMDIApplication;
import org.mdi.bootstrap.FileProperties;
import org.mdi.bootstrap.launcher.Argument;
import org.mdi.bootstrap.launcher.ArgumentGroup;
import org.mdi.bootstrap.swing.MDIApplicationListener;
import org.mdiutil.io.FileUtilities;
import org.mdiutil.prefs.NetworkPreferencesFactory;
import org.xmldiff.app.gui.CompareXMLDialog;
import org.xmldiff.core.config.XMLDiffConfiguration;
import org.xmldiff.core.gui.XMLDiffWindow;
import org.xmldiff.core.model.ComparisonModel;

/**
 * The GUI launcher class for the xmldiff tool.
 *
 * @version 0.2
 */
public class XMLDiffGUI extends AbstractMDIApplication {
   private Preferences pref = null;
   private final XMLDiffGUIListener appListener = new XMLDiffGUIListener();

   public XMLDiffGUI(String[] args) {
      super("XMLDiff");
      this.hasClosableTab(true);
      // set the command-line arguments
      this.setCommandLineArguments(args);
      constructWithUserConfig();
      // apply the arguments for the application (it will indirectly call the handleCommandLineArguments method)
      super.applyCommandLineArguments();      
   }

   @Override
   public void handleCommandLineArguments(Map<String, ArgumentGroup> argumentGroups, Map<String, Argument> arguments) {
      // handle the arguments
      File leftFile = null;
      File rightFile = null;
      File rules = null;
      if (arguments.containsKey("leftFile")) {
         Argument arg = arguments.get("leftFile");
         leftFile = arg.getValueAsFile();
      }
      if (arguments.containsKey("rightFile")) {
         Argument arg = arguments.get("rightFile");
         rightFile = arg.getValueAsFile();
      }
      if (arguments.containsKey("rules")) {
         Argument arg = arguments.get("rules");
         rules = arg.getValueAsFile();
      }
      if (rules != null) {
         XMLDiffConfiguration.getInstance().setNodeRulesFile(rules);
      }
      if (leftFile != null && rightFile != null) {
         runComparison(leftFile, rightFile);
      }
   }

   @Override
   public URL getCommandLineConfiguration() {
      return this.getClass().getResource("commandline.xml");
   }

   public static void main(String[] args) {
      XMLDiffGUI gui = new XMLDiffGUI(args);
      gui.setVisible(true);
   }

   private void constructWithUserConfig() {
      initConfiguration("");
      constructApp();
   }

   private void constructApp() {
      AppConfiguration appconf = AppConfiguration.getInstance();
      conf = appconf;
      this.setSize(appconf.width, appconf.height);
      mfactory = new MenuFactory(this);
      super.preparePanels(true, true, mfactory);
      this.addApplicationListener(appListener);
   }

   /**
    * Creates the Configuration.
    *
    * @param dir the directory
    */
   protected void initConfiguration(String dir) {
      conf = AppConfiguration.getInstance();
      // finds the serializarion directory
      NetworkPreferencesFactory fac;

      try {
         LauncherConf lconf = LauncherConf.getInstance();

         fac = NetworkPreferencesFactory.newFactory(lconf.getUserHome(), null, null, "xmlj");
         pref = fac.userRoot();
      } catch (BackingStoreException e) {
         e.printStackTrace();
      }
      super.initConfiguration(pref);
   }

   /**
    * Run a comparison.
    */
   public void runComparison() {
      CompareXMLDialog dialog = new CompareXMLDialog(this.getApplicationWindow());
      int ret = dialog.showDialog();
      if (ret == JFileChooser.APPROVE_OPTION) {
         File leftFile = dialog.getLeftFile();
         File rightFile = dialog.getRightFile();
         runComparison(leftFile, rightFile);
      }
   }

   /**
    * Run a comparison between two files.
    *
    * @param leftFile the left file
    * @param rightFile the righ file
    */
   public void runComparison(File leftFile, File rightFile) {
      XMLDiffWindow diffWindow = new XMLDiffWindow();
      diffWindow.setFiles(leftFile, rightFile);
      Frame frame = this.getApplicationWindow();
      diffWindow.setDividerLocation(frame.getWidth(), frame.getHeight());
      ComparisonModel compModel = diffWindow.runCompare();
      this.addTab(diffWindow, compModel, getName(leftFile, rightFile));
      setCurrentComparisonModel(compModel);
   }

   private void setCurrentComparisonModel(ComparisonModel compModel) {
      ((MenuFactory) mfactory).setCurrentComparisonModel(compModel);
   }

   private String getName(File leftFile, File rightFile) {
      return FileUtilities.getFileNameBody(leftFile) + " - " + FileUtilities.getFileNameBody(rightFile);
   }

   private class XMLDiffGUIListener implements MDIApplicationListener {
      public void fireTabChanged(FileProperties prop) {
         ComparisonModel compModel = (ComparisonModel) prop.getObject();
         ((MenuFactory) mfactory).setCurrentComparisonModel(compModel);
      }

      @Override
      public void fireTabRemoved(FileProperties prop) {
         ((MenuFactory) mfactory).setCurrentComparisonModel(null);
      }
   }
}

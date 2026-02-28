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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import org.mdiutil.swing.JFileSelector;
import org.mdiutil.swing.PropertyEditor;
import org.xmldiff.core.config.XMLDiffConfiguration;

/**
 * This class manage the settings of the application.
 *
 * @version 0.3
 */
public class XMLDiffSettings {
   private final AppConfiguration appConf = AppConfiguration.getInstance();
   private final XMLDiffConfiguration xmldiffConf = XMLDiffConfiguration.getInstance();
   private static XMLDiffSettings settings = null;
   private XMLDiffGUI app = null;
   private final PropertyEditor generalSettings = new PropertyEditor();
   private final PropertyEditor browserSettings = new PropertyEditor();
   private JFileSelector nodeRulesSelector;
   private JCheckBox keepSelectedDirCb;

   private XMLDiffSettings() {
      super();
   }

   public static XMLDiffSettings getInstance() {
      if (settings == null) {
         settings = new XMLDiffSettings();
      }
      return settings;
   }

   public void setApplication(XMLDiffGUI app) {
      this.app = app;
      initialize();
   }

   public XMLDiffGUI getApplication() {
      return app;
   }

   public PropertyEditor getGeneralSettings() {
      return generalSettings;
   }
   
   public PropertyEditor getBrowserSettings() {
      return browserSettings;
   }   

   public void resetSettings() {
      nodeRulesSelector.setSelectedFile(xmldiffConf.getNodeRulesFile());
      keepSelectedDirCb.setSelected(appConf.isKeepingSelectedDirectory());
   }

   /**
    * Initialize the general Settings.
    */
   private void initializeGeneralSettings() {
      File userdir = new File(System.getProperty("user.dir"));

      nodeRulesSelector = new JFileSelector();
      nodeRulesSelector.setDialogTitle("Nodes Rules");
      nodeRulesSelector.setDialogType(JFileChooser.OPEN_DIALOG);
      nodeRulesSelector.setFileSelectionMode(JFileChooser.FILES_ONLY);
      nodeRulesSelector.setCurrentDirectory(userdir);
      nodeRulesSelector.setHasOptionalFiles(true);

      nodeRulesSelector.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            xmldiffConf.setNodeRulesFile(nodeRulesSelector.getSelectedFile());
         }
      });
   }
   
   /**
    * Initialize the browser Settings.
    */
   private void initializeBrowserSettings() {
      keepSelectedDirCb = new JCheckBox();
      keepSelectedDirCb.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            appConf.keepSelectedDirectory(keepSelectedDirCb.isSelected());
         }
      });
   }   

   private void initialize() {
      initializeGeneralSettings();
      initializeBrowserSettings();
      configureSettings();
   }

   /**
    * Configure the Settings.
    */
   private void configureSettings() {
      resetSettings();

      generalSettings.addProperty(nodeRulesSelector, "", "Nodes Rules");
      generalSettings.setVisible(true);

      browserSettings.addProperty(keepSelectedDirCb, "", "Keep Selected Directory");
      browserSettings.setVisible(true);      
   }

}

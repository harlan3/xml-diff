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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import org.mdi.app.swing.AbstractMDIApplication;
import org.mdi.app.swing.AbstractMDIMenuFactory;
import org.mdi.bootstrap.swing.SwingFileProperties;
import org.mdi.gui.swing.AbstractSettingsAction;
import org.mdi.gui.swing.DefaultSettingsAction;
import org.xmldiff.core.config.XMLDiffConfiguration;
import org.xmldiff.core.gui.ComparatorPanel;
import org.xmldiff.core.gui.XMLDiffWindow;
import org.xmldiff.core.model.ComparisonModel;

/**
 * This class creates the Menus for the application.
 *
 * @version 0.4
 */
public class MenuFactory extends AbstractMDIMenuFactory {
   private final JMenu fileMenu = new JMenu("File");
   private final JMenu optionsmenu = new JMenu("Options");
   private JMenu helpMenu = new JMenu("Help");
   private final XMLDiffConfiguration conf;
   private final AppConfiguration appconf;
   private final XMLDiffGUI win;
   private XMLDiffSettings settings;
   private AbstractAction aboutAction;
   private AbstractAction runAction;
   private ComparatorPanel diffPanel = null;

   /**
    * Constructor.
    *
    * @param win the XML Editor
    */
   public MenuFactory(XMLDiffGUI win) {
      appconf = AppConfiguration.getInstance();
      conf = XMLDiffConfiguration.getInstance();
      this.win = win;
   }

   public void setHelpMenu(JMenu helpMenu) {
      this.helpMenu = helpMenu;
   }
   
   public void setCurrentComparisonModel(ComparisonModel compModel) {
      diffPanel.setComparisonModel(compModel);
   }

   /**
    * construct the application internal menus.
    */
   @Override
   protected void initMenus() {
      exitAction = new AbstractAction("Exit") {
         @Override
         public void actionPerformed(ActionEvent ae) {
            win.dispose();
         }
      };

      runAction = new AbstractAction("Run Comparison") {
         @Override
         public void actionPerformed(ActionEvent e) {
            win.runComparison();
         }
      };

      settingsAction = new DefaultSettingsAction(appli, "Settings");
      settingsAction.getSettingsComponent().setPreferredSize(new Dimension(700, 500));
      settings = XMLDiffSettings.getInstance();
      settings.setApplication(win);
      settingsAction.addNode(null, "General", settings.getGeneralSettings(), null);
      settingsAction.addNode(null, "Browser", settings.getBrowserSettings(), null);
      optionsmenu.add(new JMenuItem((AbstractSettingsAction) settingsAction));

      aboutAction = new AbstractAction("About") {
         @Override
         public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(null, "xmldiff version " + conf.version + "\n" + "building date: " + conf.date, "About",
               JOptionPane.INFORMATION_MESSAGE);
         }
      };

      JMenuItem runItem = new JMenuItem(runAction);
      JMenuItem exitItem = new JMenuItem(exitAction);
      JMenuItem aboutItem = new JMenuItem(aboutAction);

      // create file menu
      fileMenu.add(runItem);
      fileMenu.addSeparator();
      fileMenu.add(exitItem);
      
      registerMenus();

      // create help menu
      helpMenu.add(aboutItem);

      // create Menu bar
      Mbar.add(fileMenu);
      Mbar.add(optionsmenu);
      Mbar.add(helpMenu);
      
      JToolBar tbar = new JToolBar("Options");
      getToolBarPanel().add(tbar);
      diffPanel = new ComparatorPanel();
      tbar.add(diffPanel);
   } 
   
   @Override
   public void createPopupMenu(JPopupMenu menu) {
      JMenuItem reload = new JMenuItem("Reload");
      menu.add(reload);
      reload.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            reload();
         }
      });    
   }     
   
   private void reload() {
      XMLDiffWindow diffWindow =  getSelectedWindow();
      if (diffWindow != null) {
         ComparisonModel compModel = diffWindow.reload();
         this.setCurrentComparisonModel(compModel);
      }
   }
   
   /**
    * Return the current diff window.
    *
    * @return the current diff window
    */
   public XMLDiffWindow getSelectedWindow() {
      SwingFileProperties prop = ((AbstractMDIApplication) appli).getSelectedProperties();
      if (prop != null) {
         Object o = prop.getComponent();
         if (o instanceof XMLDiffWindow) {
            return (XMLDiffWindow) o;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }    
   
   /**
    * Return the current model.
    *
    * @return the current model
    */
   public ComparisonModel getSelectedElement() {
      SwingFileProperties prop = ((AbstractMDIApplication) appli).getSelectedProperties();
      if (prop != null) {
         Object o = prop.getObject();
         if (o instanceof ComparisonModel) {
            return (ComparisonModel) o;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }     
}

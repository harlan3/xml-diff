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
package org.xmldiff.app.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.mdiutil.swing.GenericDialog;
import org.mdiutil.swing.JFileSelector;
import org.xmldiff.app.main.AppConfiguration;

/**
 * This Dialog allows to set XML files to compare.
 *
 * @since 0.1
 */
public class CompareXMLDialog extends GenericDialog {
   private JFileSelector xmlLeftSelector = null;
   private JFileSelector xmlRightSelector = null;

   public CompareXMLDialog(Component parent) {
      super("Set XML Files to Compare");
      this.createDialog(parent, true);
   }

   /**
    * Return the left file.
    *
    * @return the left file
    */
   public File getLeftFile() {
      return xmlLeftSelector.getSelectedFile();
   }
   
   /**
    * Return the right file.
    *
    * @return the right file
    */
   public File getRightFile() {
      return xmlRightSelector.getSelectedFile();
   }   

   @Override
   protected void createPanel() {
      AppConfiguration appConf = AppConfiguration.getInstance();
      
      AppConfiguration config = AppConfiguration.getInstance();
      Container pane = dialog.getContentPane();
      pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
      pane.add(Box.createVerticalStrut(5));
      File dir = appConf.getLastDirectory();
      if (dir == null) {
         dir = new File(System.getProperty("user.dir"));
      }

      // left selector
      JPanel leftpanel = new JPanel();
      leftpanel.setLayout(new BoxLayout(leftpanel, BoxLayout.X_AXIS));
      leftpanel.add(Box.createHorizontalStrut(5));
      leftpanel.add(new JLabel("Left XML File"));
      xmlLeftSelector = new JFileSelector("Left XML File");
      xmlLeftSelector.setDialogType(JFileChooser.OPEN_DIALOG);
      xmlLeftSelector.addChoosableFileFilter(config.xmlfilter);
      xmlLeftSelector.addChoosableFileFilter(config.xsdfilter);      
      xmlLeftSelector.setCurrentDirectory(dir);
      xmlLeftSelector.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            File theFile = xmlLeftSelector.getSelectedFile();
            if (appConf.isKeepingSelectedDirectory() && theFile != null) {
               File dir = theFile.getParentFile();
               appConf.setLastDirectory(dir);
               xmlRightSelector.setCurrentDirectory(dir);
            }
         }
      });
      
      leftpanel.add(Box.createHorizontalStrut(5));
      leftpanel.add(xmlLeftSelector);
      leftpanel.add(Box.createHorizontalGlue());
      pane.add(leftpanel);
      pane.add(Box.createVerticalStrut(5));
      pane.add(Box.createRigidArea(new Dimension(50, 20)));

      // right selector
      JPanel rightpanel = new JPanel();
      rightpanel.setLayout(new BoxLayout(rightpanel, BoxLayout.X_AXIS));
      rightpanel.add(Box.createHorizontalStrut(5));
      rightpanel.add(new JLabel("Right XML File"));
      xmlRightSelector = new JFileSelector("Right XML File");
      xmlRightSelector.setDialogType(JFileChooser.OPEN_DIALOG);
      xmlRightSelector.addChoosableFileFilter(config.xmlfilter);
      xmlRightSelector.addChoosableFileFilter(config.xsdfilter);
      xmlRightSelector.setCurrentDirectory(dir);
      xmlRightSelector.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            File theFile = xmlRightSelector.getSelectedFile();
            if (appConf.isKeepingSelectedDirectory() && theFile != null) {
               File dir = theFile.getParentFile();
               appConf.setLastDirectory(dir);
               xmlLeftSelector.setCurrentDirectory(dir);
            }
         }
      });      
      rightpanel.add(Box.createHorizontalStrut(5));
      rightpanel.add(xmlRightSelector);
      rightpanel.add(Box.createHorizontalGlue());
      pane.add(rightpanel);
      pane.add(Box.createVerticalStrut(5));
      pane.add(Box.createRigidArea(new Dimension(50, 20)));      

      JPanel yesnopanel = this.createYesNoPanel();
      pane.add(yesnopanel);
   }

}

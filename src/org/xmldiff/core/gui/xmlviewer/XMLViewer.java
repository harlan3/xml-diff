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
package org.xmldiff.core.gui.xmlviewer;

import java.io.File;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import org.xmldiff.core.model.ComparisonModel;

/**
 * The XML viewer.
 *
 * @since 0.6
 */
public class XMLViewer extends JFrame {
   private static XMLViewer xmlViewer = null;
   private File leftFile = null;
   private File rightFile = null;
   private FilePane leftEditor = null;
   private FilePane rightEditor = null;
   private final JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

   /**
    * Constructor.
    */
   private XMLViewer() {
      // Initialise the window
      super("XML Viewer");
      this.setSize(1200, 700);
      setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      setContentPane(split);
      setup();
   }

   /**
    * Return the left file.
    *
    * @return the left file
    */
   public File getLeftFile() {
      return leftFile;
   }

   /**
    * Return the right file.
    *
    * @return the right file
    */
   public File getRightFile() {
      return rightFile;
   }

   /**
    * Return the unique instance.
    *
    * @return the instance
    */
   public static XMLViewer getInstance() {
      if (xmlViewer == null) {
         xmlViewer = new XMLViewer();
      }
      return xmlViewer;
   }

   private void setup() {
      leftEditor = new FilePane(this);
      rightEditor = new FilePane(this);
      split.setLeftComponent(leftEditor);
      split.setRightComponent(rightEditor);
      split.setDividerLocation(leftEditor.getPreferredSize().width);
   }

   /**
    * Open a file in the editor and scroll to specific lines at the left and at the right.
    *
    * @param model the comparison model
    * @param leftLine the left line
    * @param rightLine the right line
    */
   public void load(ComparisonModel model, int leftLine, int rightLine) {
      this.leftFile = model.getLeftFile();
      this.rightFile = model.getRightFile();
      openImpl();
      leftEditor.getEditor().recalculateVisibleLines();
      rightEditor.getEditor().recalculateVisibleLines();
      scrollTo(leftLine, rightLine);
   }

   private void openImpl() {
      leftEditor.setFile(leftFile);
      rightEditor.setFile(rightFile);
   }

   /**
    * Scroll to specific lines at the left and at the right.
    *
    * @param leftLine the left line
    * @param rightLine the right line
    */
   public void scrollTo(int leftLine, int rightLine) {
      if (leftLine != -1) {
         leftEditor.scrollTo(leftLine);
      } else {
         leftEditor.scrollTo(-1);
      }
      if (rightLine != -1) {
         rightEditor.scrollTo(rightLine);
      } else {
         rightEditor.scrollTo(-1);
      }
   }
}

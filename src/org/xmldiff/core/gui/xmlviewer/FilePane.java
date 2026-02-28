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

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import org.jeditor.core.CodeEditorDefaults;
import org.jeditor.gui.DefaultEditorPopup;
import org.jeditor.gui.JEditor;
import org.jeditor.scripts.base.Token;
import org.jeditor.scripts.base.TokenMarker;
import org.jeditor.scripts.tokenmarkers.XMLTokenMarker;

/**
 * A Panel showing the content of an XML file.
 *
 * @since 0.6
 */
public class FilePane extends JPanel {
   private TextFile textFile = null;
   private final XMLViewer parent;
   private JEditor ed = null;
   private TokenMarker marker = new XMLTokenMarker();
   private static CodeEditorDefaults defaults = new CodeEditorDefaults();

   static {
      defaults.eolMarkers = false;
      defaults.paintInvalid = false;
      defaults.gutterCollapsed = false;
      defaults.setStyle(Token.KEYWORD1, Color.BLUE, false, true);
      defaults.setStyle(Token.KEYWORD2, new Color(13, 130, 0), false, true);
      defaults.setStyle(Token.KEYWORD3, Color.BLUE, false, true);
      defaults.setStyle(Token.COMMENT1, Color.DARK_GRAY, true, false);
      defaults.setStyle(Token.COMMENT2, Color.DARK_GRAY, true, false);
      defaults.setPopup(new DefaultEditorPopup());
   }

   /**
    * Constructor for files comparison.
    *
    * @param parent the parent editor
    */
   public FilePane(XMLViewer parent) {
      super();
      this.parent = parent;
      this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
      setPanel();
   }

   /**
    * Set the file to dhow in the editor.
    *
    * @param file the file
    */
   public void setFile(File file) {
      this.textFile = new TextFile(file);
      ed.setText(textFile.getText());
   }

   /**
    * Return the associated file.
    *
    * @return the textFile
    */
   public File getFile() {
      return textFile.getFile();
   }

   private void setPanel() {
      ed = new JEditor(defaults);
      ed.setEditable(false);
      ed.setTokenMarker(marker);
      this.add(ed);
   }

   /**
    * Highlight a line number.
    *
    * @param lineNumber the line number
    */
   public void scrollTo(int lineNumber) {
      ed.scrollTo(lineNumber - 1, 0);
      ed.setCaretPosition(ed.getLineStartOffset(lineNumber - 1));
   }

   /**
    * Return the associated editor.
    *
    * @return the editor
    */
   public JEditor getEditor() {
      return ed;
   }

   /**
    * Represents the editor associated text file.
    *
    * @since 0.6
    */
   private class TextFile {
      public File file;
      public List<String> vfile = new ArrayList<>();
      public String ext = "";

      private TextFile() {
      }

      private TextFile(File file) {
         this.file = file;
         ext = getExtension(file);
         load();
      }

      private void load() {
         String s;
         try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            while ((s = reader.readLine()) != null) {
               vfile.add(s);
            }
         } catch (IOException e) {
         }
      }

      public void setFile(File file) {
         this.file = file;
      }

      public File getFile() {
         return file;
      }

      public String getExtension() {
         return ext;
      }

      public String getName() {
         return file.getName();
      }

      public List<String> getText() {
         return vfile;
      }

      private String getExtension(File file) {
         String s = file.getName();
         int idx = s.lastIndexOf('.');
         if (idx != -1) {
            return s.substring(idx + 1, s.length());
         } else {
            return "";
         }
      }
   }
}

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
package org.xmldiff.core.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import org.mdiutil.xml.swing.BasicSAXHandler;
import org.xml.sax.SAXParseException;
import org.xmldiff.core.config.ParserErrorListener;

/**
 * The ErrorWindow is used to show errors detected during the rules parsing.
 *
 * @version 0.4
 */
public class ErrorWindow implements ParserErrorListener {
   private final List<String> errorsList = new ArrayList<>();
   private JButton OKButton = null;
   private JDialog dialog = null;

   public ErrorWindow() {
   }

   /**
    * Fired for a warning.
    *
    * @param e the SAXParseException
    */
   @Override
   public void warning(SAXParseException e) {
      errorsList.add("WARNING: " + e.getMessage());
   }

   /**
    * Fired for an error.
    *
    * @param e the SAXParseException
    */
   @Override
   public void error(SAXParseException e) {
      errorsList.add("ERROR: " + e.getMessage());
   }

   /**
    * Fired for a fatal error.
    *
    * @param e the SAXParseException
    */
   @Override
   public void fatal(SAXParseException e) {
      errorsList.add("FATAL: " + e.getMessage());
   }

   /**
    * Return true if there were errors during the parsing.
    *
    * @return true if there were errors during the parsing
    */
   @Override
   public boolean hasErrors() {
      return !errorsList.isEmpty();
   }

   /**
    * Show the parsing exceptions in a popup window.
    *
    * @param title the window title
    */
   public void showExceptions(String title) {
      if (errorsList.isEmpty()) {
         return;
      }
      JTextPane area = constructExceptionResultPane();

      OKButton = new JButton("OK");

      OKButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            dialog.dispose();
         }
      });

      JScrollPane scroll = new JScrollPane(area);
      JOptionPane pane = new JOptionPane(scroll, JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{OKButton});
      Frame frame = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, null);
      dialog = pane.createDialog(frame, title);
      dialog.setResizable(true);
      dialog.setModal(false);
      dialog.setVisible(true);
   }

   /**
    * Construct the JTextPane holding the exception encountered during the parsing.
    */
   private JTextPane constructExceptionResultPane() {
      SizedTextPane area = new SizedTextPane(10, 40);

      Iterator<String> it = errorsList.iterator();
      while (it.hasNext()) {
         String errorText = it.next();
         area.append(errorText, "red");
      }

      return area;
   }

   /**
    * A JTextPane with a specified preferred scrollable size.
    */
   private class SizedTextPane extends JTextPane {
      private Dimension d;
      private int rows = 1;
      private int columns = 1;
      private HTMLDocument doc;
      private final HTMLEditorKit kit = new HTMLEditorKit();
      private String fontFace = null;
      private static final int FONT_SIZE = BasicSAXHandler.DEFAULT_SIZE;

      /**
       * Create a new SizedTextPane.
       *
       * @param rows the number of rows of the area
       * @param columns the number of columns of the area
       */
      public SizedTextPane(int rows, int columns) {
         super();
         this.rows = rows;
         this.columns = columns;

         setSize();
         createDocument();
      }

      private void setSize() {
         JTextArea textArea = new JTextArea(rows, columns);
         textArea.setEditable(false);
         Font font = textArea.getFont();
         fontFace = font.getFamily();
         d = textArea.getPreferredSize();
      }

      private void createDocument() {
         this.setEditorKit(kit);
         doc = (HTMLDocument) kit.createDefaultDocument();
         this.setDocument(doc);
         this.setEditable(false);
      }

      /**
       * Append a message on an html color.
       *
       * @param text the text message
       * @param htmlColor the html color
       */
      public void append(String text, String htmlColor) {
         appendImpl("<html><font face=\"" + fontFace + "\" size=\"" + FONT_SIZE + "\" color=" + htmlColor + "\">" + text + "</font></html>\n");
      }

      private void appendImpl(String text) {
         try {
            Reader r = new StringReader(text);
            kit.read(r, doc, doc.getLength());
            this.setCaretPosition(doc.getLength());
         } catch (BadLocationException | IOException e) {
         }
      }

      @Override
      public Dimension getPreferredScrollableViewportSize() {
         return d;
      }
   }
}

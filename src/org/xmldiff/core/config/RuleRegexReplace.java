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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * A regex replace used in a Rule for description attributes or CDATA content.
 *
 * @version 0.6
 */
public class RuleRegexReplace {
   private final List<Replace> replacements = new ArrayList<>();

   public RuleRegexReplace() {
   }

   public void addReplacement(String from, String to) throws PatternSyntaxException {
      Replace replace = new Replace(from, to);
      replacements.add(replace);
   }

   /**
    * Apply all the regex on the attribute value or CDATA content.
    *
    * @param value the attribute value or CDATA content
    * @return the replaced value after applying all the regex
    */
   public String apply(String value) {
      Iterator<Replace> it = replacements.iterator();
      while (it.hasNext()) {
         Replace replace = it.next();
         value = replace.apply(value);
      }
      return value;
   }

   /**
    * Return the list of replacements.
    *
    * @return the list of replacements
    */
   public List<Replace> getReplacements() {
      return replacements;
   }

   /**
    * Return true if there are no replacements.
    *
    * @return true if there are no replacements
    */
   public boolean isEmpty() {
      return replacements.isEmpty();
   }

   /**
    * Represents one replacement regex.
    */
   public static class Replace {
      private final String from;
      private final Pattern pat;
      private final String to;

      private Replace(String from, String to) throws PatternSyntaxException {
         this.from = from;
         this.to = to;
         this.pat = Pattern.compile(from);
      }

      public String getFrom() {
         return from;
      }

      public String getTo() {
         return to;
      }

      /**
       * Apply the regex on the attribute value or CDATA content..
       *
       * @param value the attribute value or CDATA content.
       * @return the replaced value after applying the regex
       */
      private String apply(String value) {
         return pat.matcher(value).replaceAll(to);
      }
   }
}

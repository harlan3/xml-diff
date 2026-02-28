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
package org.xmldiff.core.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The key for node in the comparison.
 *
 * @since 0.1
 */
public class XMLNodeKey {
   private List<Integer> path = new ArrayList<>();
   private final String nodeName;

   public XMLNodeKey(List<Integer> path, String nodeName) {
      this.path = path;
      this.nodeName = nodeName;
   }

   public List<Integer> getPath() {
      return path;
   }

   public String getNodeName() {
      return nodeName;
   }

   @Override
   public String toString() {
      return nodeName;
   }

   @Override
   public int hashCode() {
      int hash = 7;
      hash = 97 * hash + Objects.hashCode(this.path);
      return hash;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final XMLNodeKey other = (XMLNodeKey) obj;
      if (!Objects.equals(this.path, other.path)) {
         return false;
      }
      return true;
   }

}

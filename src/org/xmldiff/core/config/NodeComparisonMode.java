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

/**
 * The comparison mode for nodes. This basically detects when a left and right nodes are considered to represent the same node (even if they are different).
 *
 * @since 0.1
 */
public interface NodeComparisonMode {
   /**
    * Consider that if there is any difference between two nodes (including their names and attributes), they are consideed different nodes.
    */
   public static char ANY_DIFF_OTHER = 0;
   /**
    * Consider that if the names of the nodes are the same, the are considered to be th same node.
    */   
   public static char SAME_NODENAME_SAME = 1;
   /**
    * Use the attributes defined on the rules to deect f two nodes are the same.
    */
   public static char ON_ATTRIBUTES = 2;
}

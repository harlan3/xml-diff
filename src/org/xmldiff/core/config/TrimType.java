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
 * The types of tim for descriptions.
 *
 * @version 0.6
 */
public interface TrimType {
   /**
    * No Trim.
    */
   public static char NO = 0;
   /**
    * Trim at the right.
    */   
   public static char TRIM_RIGHT = 1;
   /**
    * Trim at the left.
    */   
   public static char TRIM_LEFT = 2;   
   /**
    * Trim at the right and left.
    */
   public static char TRIM = 3;
}

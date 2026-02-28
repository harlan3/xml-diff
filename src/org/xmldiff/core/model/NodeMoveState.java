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

/**
 * The move states of a node in the comparison.
 *
 * @version 0.6
 */
public interface NodeMoveState extends NodeState {
   /**
    * Tags the case where the node is not moved between left and right.
    */   
   public static final char MOVED_UNCHANGED = STATE_UNCHANGED;
   /**
    * Tags the case where the node is moved up between left and right.
    */      
   public static final char MOVED_UP = 4;
   /**
    * Tags the case where the node is moved down between left and right.
    */         
   public static final char MOVED_DOWN = 5;
   /**
    * Tags the case where the node is moved up between left and right, and updated independently from the move.
    */  
   public static final char MOVED_UP_AND_UPDATED = 6;
   /**
    * Tags the case where the node is moved down between left and right, and updated independently from the move.
    */     
   public static final char MOVED_DOWN_AND_UPDATED = 7;  
   /**
    * Tags the case where the node is moved up between left and right, and the the move updates it.
    */     
   public static final char MOVED_UP_THEN_UPDATED = 8;
   /**
    * Tags the case where the node is moved down between left and right, and the the move updates it.
    */        
   public static final char MOVED_DOWN_THEN_UPDATED = 9;  
}

// 
// MobeelizerError.java
// 
// Copyright (C) 2012 Mobeelizer Ltd. All Rights Reserved.
//
// Mobeelizer SDK is free software; you can redistribute it and/or modify it 
// under the terms of the GNU Affero General Public License as published by 
// the Free Software Foundation; either version 3 of the License, or (at your
// option) any later version.
//
// This program is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
// FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License
// for more details.
//
// You should have received a copy of the GNU Affero General Public License 
// along with this program; if not, write to the Free Software Foundation, Inc., 
// 51 Franklin St, Fifth Floor, Boston, MA  02110-1301 USA
// 

package com.mobeelizer.mobile.android.api;

import java.util.List;

/**
 * Representation of the validation error.
 * 
 * @since 1.0
 */
public interface MobeelizerError {

    /**
     * Return the code of the error.
     * 
     * @return code
     * @since 1.0
     */
    MobeelizerErrorCode getCode();

    /**
     * Return the readable message for the error.
     * 
     * @return message
     * @since 1.0
     */
    String getMessage();

    /**
     * Return the arguments for message.
     * 
     * @return arguments
     * @see MobeelizerErrorCode
     * @since 1.0
     */
    List<Object> getArgs();

}

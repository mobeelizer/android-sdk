// 
// MobeelizerLoginStatus.java
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

import com.mobeelizer.mobile.android.Mobeelizer;

/**
 * The result of creation a user session.
 * 
 * @see Mobeelizer#login(String, String)
 * @see Mobeelizer#login(String, String, String)
 * @since 1.0
 */
public enum MobeelizerLoginStatus {

    /**
     * The user session has been successfully created.
     * 
     * @since 1.0
     */
    OK,

    /**
     * Login, password and instance do not match to any existing users.
     * 
     * @since 1.0
     */
    AUTHENTICATION_FAILURE,

    /**
     * Unknown error. Look for the explanation in the instance logs and the application logs.
     * 
     * @since 1.0
     */
    OTHER_FAILURE,

    /**
     * Connection error. Look for the explanation in the application logs.
     * 
     * @since 1.0
     */
    CONNECTION_FAILURE,

    /**
     * Missing connection. First login requires active Internet connection.
     * 
     * @since 1.0
     */
    MISSING_CONNECTION_FAILURE

}
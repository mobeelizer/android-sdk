// 
// MobeelizerModelDefinition.java
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

import java.util.Set;

/**
 * Definition of the database model.
 * 
 * @since 1.0
 */
public interface MobeelizerModelDefinition {

    /**
     * Return a name of the given model.
     * 
     * @return name
     * @since 1.0
     */
    String getName();

    /**
     * Return a class that maps the given model.
     * 
     * @return class
     * @since 1.0
     */
    Class<?> getMappingClass();

    /**
     * Return a list of fields.
     * 
     * @return fields
     * @since 1.0
     */
    Set<MobeelizerFieldDefinition> getFields();

    /**
     * The credentials for current user.
     * 
     * @return credentials
     * @since 1.0
     */
    MobeelizerModelCredentials getCredentials();

}

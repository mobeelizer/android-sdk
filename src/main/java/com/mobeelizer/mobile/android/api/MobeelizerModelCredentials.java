// 
// MobeelizerModelCredentials.java
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

/**
 * Credentials for the database model and the user role.
 * 
 * @since 1.0
 */
public interface MobeelizerModelCredentials {

    /**
     * Credential for read operation.
     * 
     * @return credential
     * @since 1.0
     */
    MobeelizerCredential getReadAllowed();

    /**
     * Credential for update operation.
     * 
     * @return credential
     * @since 1.0
     */
    MobeelizerCredential getUpdateAllowed();

    /**
     * Credential for create operation.
     * 
     * @return credential
     * @since 1.0
     */
    MobeelizerCredential getCreateAllowed();

    /**
     * Credential for delete operation.
     * 
     * @return credential
     * @since 1.0
     */
    MobeelizerCredential getDeleteAllowed();

    /**
     * The flag if role has permission to the resolve conflicts.
     * 
     * @return true if role has permission to the resolve conflicts
     * @since 1.0
     */
    boolean isResolveConflictAllowed();

}

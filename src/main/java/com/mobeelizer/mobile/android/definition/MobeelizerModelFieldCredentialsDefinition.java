// 
// MobeelizerModelFieldCredentialsDefinition.java
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

package com.mobeelizer.mobile.android.definition;

import java.io.Serializable;

import com.mobeelizer.mobile.android.api.MobeelizerCredential;

public class MobeelizerModelFieldCredentialsDefinition implements Serializable {

    private static final long serialVersionUID = -7383951029997941308L;

    private String role;

    private MobeelizerCredential readAllowed;

    private MobeelizerCredential createAllowed;

    private MobeelizerCredential updateAllowed;

    String getDigestString() {
        return role
                + "="
                + String.format("%d%d%d%d%d", readAllowed.ordinal(), createAllowed.ordinal(), updateAllowed.ordinal(),
                        MobeelizerCredential.NONE.ordinal(), 0);
    }

    public String getRole() {
        return role;
    }

    public void setRole(final String role) {
        this.role = role;
    }

    public MobeelizerCredential getReadAllowed() {
        return readAllowed;
    }

    public void setReadAllowed(final MobeelizerCredential readAllowed) {
        this.readAllowed = readAllowed;
    }

    public MobeelizerCredential getCreateAllowed() {
        return createAllowed;
    }

    public MobeelizerCredential getUpdateAllowed() {
        return updateAllowed;
    }

    public void setCreateAllowed(final MobeelizerCredential createAllowed) {
        this.createAllowed = createAllowed;
    }

    public void setUpdateAllowed(final MobeelizerCredential updateAllowed) {
        this.updateAllowed = updateAllowed;
    }

}

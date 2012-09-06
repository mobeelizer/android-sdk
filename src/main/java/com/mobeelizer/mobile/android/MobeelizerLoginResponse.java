// 
// MobeelizerLoginResponse.java
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

package com.mobeelizer.mobile.android;

import com.mobeelizer.java.api.MobeelizerOperationError;

class MobeelizerLoginResponse {

    private final String role;

    private final String instanceGuid;

    private final MobeelizerOperationError error;

    private final boolean initialSyncRequired;

    public MobeelizerLoginResponse(final MobeelizerOperationError error, final String instanceGuid, final String role,
            final boolean initialSyncRequired) {
        this.error = error;
        this.role = role;
        this.instanceGuid = instanceGuid;
        this.initialSyncRequired = initialSyncRequired;
    }

    public MobeelizerLoginResponse(final MobeelizerOperationError error) {
        this.error = error;
        this.role = null;
        this.instanceGuid = null;
        this.initialSyncRequired = false;
    }

    public String getRole() {
        return role;
    }

    public String getInstanceGuid() {
        return instanceGuid;
    }

    public MobeelizerOperationError getError() {
        return error;
    }

    public boolean isInitialSyncRequired() {
        return initialSyncRequired;
    };

}

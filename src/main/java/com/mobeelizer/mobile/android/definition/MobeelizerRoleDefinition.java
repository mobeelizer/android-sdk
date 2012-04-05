// 
// MobeelizerRoleDefinition.java
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

public class MobeelizerRoleDefinition implements Serializable {

    private static final long serialVersionUID = -4498194438077142973L;

    private String group;

    private String device;

    String getDigestString() {
        return "{" + group + "$" + device + "}";
    }

    public String getGroup() {
        return group;
    }

    public String getDevice() {
        return device;
    }

    public void setGroup(final String group) {
        this.group = group;
    }

    public void setDevice(final String device) {
        this.device = device;
    }

    public Object resolveName() {
        return group + "-" + device;
    }

}

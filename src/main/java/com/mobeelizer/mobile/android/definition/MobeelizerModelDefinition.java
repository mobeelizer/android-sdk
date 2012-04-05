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

package com.mobeelizer.mobile.android.definition;import java.io.Serializable;
import java.util.Set;
;

public class MobeelizerModelDefinition implements Serializable {

    private static final long serialVersionUID = 2023304700190612820L;

    private String name;

    private Set<MobeelizerModelFieldDefinition> fields;

    private Set<MobeelizerModelCredentialsDefinition> credentials;

    String getDigestString() {
        StringBuilder sb = new StringBuilder().append(name).append("{");
        MobeelizerApplicationDefinition.digestSortJoinAndAdd(sb, fields);
        sb.append("$");
        MobeelizerApplicationDefinition.digestSortJoinAndAdd(sb, credentials);
        sb.append("}");
        return sb.toString();
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Set<MobeelizerModelFieldDefinition> getFields() {
        return fields;
    }

    public void setFields(final Set<MobeelizerModelFieldDefinition> fields) {
        this.fields = fields;
    }

    public Set<MobeelizerModelCredentialsDefinition> getCredentials() {
        return credentials;
    }

    public void setCredentials(final Set<MobeelizerModelCredentialsDefinition> credentials) {
        this.credentials = credentials;
    }

}

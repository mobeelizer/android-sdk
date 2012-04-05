// 
// MobeelizerModelFieldDefinition.java
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mobeelizer.mobile.android.types.FieldType;

public class MobeelizerModelFieldDefinition implements Serializable {

    private static final long serialVersionUID = 3003860769298775158L;

    private String name;

    private FieldType type;

    private Set<MobeelizerModelFieldCredentialsDefinition> credentials;

    private boolean required;

    private String defaultValue;

    private Map<String, String> options;

    String getDigestString() {
        StringBuilder sb = new StringBuilder().append(name).append("{");
        sb.append(type.name()).append("$");
        sb.append(required).append("$");
        sb.append(defaultValue).append("$");
        MobeelizerApplicationDefinition.digestSortJoinAndAdd(sb, credentials);
        sb.append("$");
        if (options != null) {
            List<String> optionsList = new ArrayList<String>();
            for (Map.Entry<String, String> option : options.entrySet()) {
                optionsList.add(option.getKey() + "=" + option.getValue());
            }
            Collections.sort(optionsList);

            boolean first = true;

            for (String optionString : optionsList) {
                if (first) {
                    first = false;
                } else {
                    sb.append("&");
                }

                sb.append(optionString);
            }
        }
        sb.append("}");
        return sb.toString();
    }

    public String getName() {
        return name;
    }

    public Set<MobeelizerModelFieldCredentialsDefinition> getCredentials() {
        return credentials;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public FieldType getType() {
        return type;
    }

    public void setCredentials(final Set<MobeelizerModelFieldCredentialsDefinition> credentials) {
        this.credentials = credentials;
    }

    public void setDefaultValue(final String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setOptions(final Map<String, String> options) {
        this.options = options;
    }

    public void setRequired(final boolean required) {
        this.required = required;
    }

    public void setType(final FieldType type) {
        this.type = type;
    }

    public boolean isRequired() {
        return required;
    }

}

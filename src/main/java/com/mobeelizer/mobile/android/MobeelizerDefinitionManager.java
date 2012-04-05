// 
// MobeelizerDefinitionManager.java
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

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import com.mobeelizer.mobile.android.api.MobeelizerCredential;
import com.mobeelizer.mobile.android.definition.MobeelizerApplicationDefinition;
import com.mobeelizer.mobile.android.definition.MobeelizerModelCredentialsDefinition;
import com.mobeelizer.mobile.android.definition.MobeelizerModelDefinition;
import com.mobeelizer.mobile.android.definition.MobeelizerModelFieldCredentialsDefinition;
import com.mobeelizer.mobile.android.definition.MobeelizerModelFieldDefinition;
import com.mobeelizer.mobile.android.definition.MobeelizerRoleDefinition;
import com.mobeelizer.mobile.android.model.MobeelizerFieldDefinitionImpl;
import com.mobeelizer.mobile.android.model.MobeelizerModelDefinitionImpl;

class MobeelizerDefinitionManager {

    public Set<MobeelizerModelDefinitionImpl> convert(final MobeelizerApplicationDefinition definition,
            final String entityPackage, final String role) {
        checkRole(definition, role);

        Set<MobeelizerModelDefinitionImpl> models = new HashSet<MobeelizerModelDefinitionImpl>();

        for (MobeelizerModelDefinition radModel : definition.getModels()) {
            MobeelizerModelCredentialsDefinition modelCredentials = hasAccess(radModel, role);

            if (modelCredentials == null) {
                continue;
            }

            Class<?> clazz = findClazz(radModel, entityPackage);

            Set<MobeelizerFieldDefinitionImpl> fields = new HashSet<MobeelizerFieldDefinitionImpl>();

            for (MobeelizerModelFieldDefinition radField : radModel.getFields()) {
                MobeelizerModelFieldCredentialsDefinition fieldCredentials = hasAccess(radField, role);

                if (fieldCredentials == null) {
                    continue;
                }

                fields.add(new MobeelizerFieldDefinitionImpl(clazz, radField, fieldCredentials));
            }

            MobeelizerModelDefinitionImpl model = new MobeelizerModelDefinitionImpl(clazz, radModel.getName(), modelCredentials,
                    fields);

            models.add(model);
        }

        return models;
    }

    private Class<?> findClazz(final MobeelizerModelDefinition radModel, final String entityPackage) {
        Class<?> clazz;
        try {
            clazz = Class.forName(entityPackage + "." + radModel.getName().substring(0, 1).toUpperCase(Locale.ENGLISH)
                    + radModel.getName().substring(1));
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        return clazz;
    }

    private void checkRole(final MobeelizerApplicationDefinition definition, final String role) {
        for (MobeelizerRoleDefinition radRole : definition.getRoles()) {
            if (radRole.resolveName().equals(role)) {
                return;
            }
        }
        throw new IllegalStateException("Role " + role + " doesn't exist in definition.");
    }

    private MobeelizerModelFieldCredentialsDefinition hasAccess(final MobeelizerModelFieldDefinition field, final String role) {
        for (MobeelizerModelFieldCredentialsDefinition credentials : field.getCredentials()) {
            if (credentials.getRole().equals(role)) {
                if (credentials.getCreateAllowed() != MobeelizerCredential.NONE
                        || credentials.getUpdateAllowed() != MobeelizerCredential.NONE
                        || credentials.getReadAllowed() != MobeelizerCredential.NONE) {
                    return credentials;
                }
                break;
            }
        }

        return null;
    }

    private MobeelizerModelCredentialsDefinition hasAccess(final MobeelizerModelDefinition model, final String role) {
        for (MobeelizerModelCredentialsDefinition credentials : model.getCredentials()) {
            if (credentials.getRole().equals(role)) {
                if (credentials.getCreateAllowed() != MobeelizerCredential.NONE
                        || credentials.getUpdateAllowed() != MobeelizerCredential.NONE
                        || credentials.getReadAllowed() != MobeelizerCredential.NONE
                        || credentials.getDeleteAllowed() != MobeelizerCredential.NONE) {
                    return credentials;
                }
                break;
            }
        }

        return null;
    }

}

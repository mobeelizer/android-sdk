// 
// MobeelizerFieldDefinitionImpl.java
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

package com.mobeelizer.mobile.android.model;

import static com.mobeelizer.mobile.android.model.MobeelizerReflectionUtil.getField;

import java.lang.reflect.Field;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;

import com.mobeelizer.mobile.android.MobeelizerErrorsImpl;
import com.mobeelizer.mobile.android.api.MobeelizerFieldCredentials;
import com.mobeelizer.mobile.android.api.MobeelizerFieldDefinition;
import com.mobeelizer.mobile.android.definition.MobeelizerModelFieldCredentialsDefinition;
import com.mobeelizer.mobile.android.definition.MobeelizerModelFieldDefinition;
import com.mobeelizer.mobile.android.types.FieldType;

public class MobeelizerFieldDefinitionImpl implements MobeelizerFieldDefinition {

    private final String name;

    private final boolean required;

    private final Object defaultValue;

    private final Field field;

    private final FieldType type;

    private final Map<String, String> options;

    private final MobeelizerFieldCredentials credentials;

    public MobeelizerFieldDefinitionImpl(final Class<?> clazz, final MobeelizerModelFieldDefinition field,
            final MobeelizerModelFieldCredentialsDefinition credentials) {
        this.name = field.getName();
        this.credentials = new MobeelizerFieldCredentialsImpl(credentials);
        this.options = field.getOptions();
        this.type = field.getType();
        this.field = getField(clazz, name, type.getAccessibleTypes());
        this.required = field.isRequired();
        this.defaultValue = type.convertDefaultValue(this.field, field.getDefaultValue(), options);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object getDefaultValue() {
        return defaultValue;
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    @Override
    public MobeelizerFieldCredentials getCredentials() {
        return credentials;
    }

    public <T> void setValueFromEntityToDatabase(final ContentValues values, final T entity, final MobeelizerErrorsImpl errors) {
        type.setValueFromEntityToDatabase(values, entity, field, required, options, errors);
    }

    public <T> void setValueFromDatabaseToEntity(final Cursor cursor, final T entity) {
        type.setValueFromDatabaseToEntity(cursor, entity, field, options);
    }

    public void setValueFromDatabaseToMap(final Cursor cursor, final Map<String, String> map) {
        type.setValueFromDatabaseToMap(cursor, map, field, options);
    }

    public String[] getDefinition() {
        return type.getDefinition(field, required, defaultValue, options);
    }

    public void setValueFromMapToDatabase(final ContentValues values, final Map<String, String> map,
            final MobeelizerErrorsImpl errors) {
        type.setValueFromMapToDatabase(values, map, field, required, options, errors);
    }

}

// 
// MobeelizerAndroidField.java
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

import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;

import com.mobeelizer.java.api.MobeelizerField;
import com.mobeelizer.java.api.MobeelizerFieldCredentials;
import com.mobeelizer.java.definition.MobeelizerErrorsHolder;
import com.mobeelizer.java.model.MobeelizerFieldImpl;
import com.mobeelizer.mobile.android.types.FieldType;

public class MobeelizerAndroidField implements MobeelizerField {

    private final MobeelizerFieldImpl field;

    private final FieldType type;

    public MobeelizerAndroidField(final MobeelizerFieldImpl field) {
        this.field = field;
        this.type = FieldType.valueOf(field.getType().name());
    }

    @Override
    public String getName() {
        return field.getName();
    }

    @Override
    public Object getDefaultValue() {
        return field.getDefaultValue();
    }

    @Override
    public boolean isRequired() {
        return field.isRequired();
    }

    @Override
    public MobeelizerFieldCredentials getCredentials() {
        return field.getCredentials();
    }

    public <T> void setValueFromDatabaseToEntity(final Cursor cursor, final T entity) {
        type.setValueFromDatabaseToEntity(cursor, entity, field.getField(), field.getOptions());
    }

    public <T> void setValueFromEntityToDatabase(final ContentValues values, final T entity, final MobeelizerErrorsHolder errors) {
        type.setValueFromEntityToDatabase(values, entity, field.getField(), field.isRequired(), field.getOptions(), errors);
    }

    public void setValueFromDatabaseToMap(final Cursor cursor, final Map<String, String> map) {
        type.setValueFromDatabaseToMap(cursor, map, field.getField(), field.getOptions());
    }

    public String[] getDefinition() {
        return type.getDefinition(field.getField(), field.isRequired(), field.getDefaultValue(), field.getOptions());
    }

    public void setValueFromMapToDatabase(final ContentValues values, final Map<String, String> map,
            final MobeelizerErrorsHolder errors) {
        type.setValueFromMapToDatabase(values, map, field.getField(), field.isRequired(), field.getOptions(), errors);
    }

}

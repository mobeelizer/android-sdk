// 
// FieldType.java
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

package com.mobeelizer.mobile.android.types;

import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;

import com.mobeelizer.java.api.MobeelizerErrorsBuilder;
import com.mobeelizer.java.definition.MobeelizerFieldType;
import com.mobeelizer.java.model.MobeelizerFieldAccessor;

public enum FieldType {

    TEXT(new TextFieldTypeHelper()), INTEGER(new IntegerFieldTypeHelper()), BOOLEAN(new BooleanFieldTypeHelper()), DECIMAL(
            new DecimalFieldTypeHelper()), DATE(new DateFieldTypeHelper()), BELONGS_TO(new BelongsToFieldTypeHelper()), FILE(
            new FileFieldTypeHelper());

    private final FieldTypeHelper helper;

    private FieldType(final FieldTypeHelper helper) {
        this.helper = helper;
    }

    public MobeelizerFieldType getType() {
        return helper.getType();
    }

    public <T> void setValueFromEntityToDatabase(final ContentValues values, final T entity, final MobeelizerFieldAccessor field,
            final boolean required, final Map<String, String> options, final MobeelizerErrorsBuilder errors) {
        helper.setValueFromEntityToDatabase(values, entity, field, required, options, errors);
    }

    public <T> void setValueFromDatabaseToEntity(final Cursor cursor, final T entity, final MobeelizerFieldAccessor field,
            final Map<String, String> options) {
        helper.setValueFromDatabaseToEntity(cursor, entity, field, options);
    }

    public String[] getDefinition(final MobeelizerFieldAccessor field, final boolean required, final Object defaultValue,
            final Map<String, String> options) {
        return helper.getDefinition(field, required, defaultValue, options);
    }

    public void setValueFromDatabaseToMap(final Cursor cursor, final Map<String, String> map,
            final MobeelizerFieldAccessor field, final Map<String, String> options) {
        helper.setValueFromDatabaseToMap(cursor, map, field, options);
    }

    public void setValueFromMapToDatabase(final ContentValues values, final Map<String, String> map,
            final MobeelizerFieldAccessor field, final boolean required, final Map<String, String> options,
            final MobeelizerErrorsBuilder errors) {
        helper.setValueFromMapToDatabase(values, map, field, required, options, errors);
    }

    public <T> boolean hasSameValues(final T entity1, final T entity2, final MobeelizerFieldAccessor field) {
        return helper.hasSameValues(entity1, entity2, field);
    }

    public <T> boolean hasNullOrDefaultValue(final T entity, final MobeelizerFieldAccessor field, final Object defaultValue) {
        return helper.hasNullOrDefaultValue(entity, field, defaultValue);
    }

}

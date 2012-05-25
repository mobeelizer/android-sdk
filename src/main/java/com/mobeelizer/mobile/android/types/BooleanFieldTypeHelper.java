// 
// BooleanFieldTypeHelper.java
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

import static com.mobeelizer.java.model.MobeelizerReflectionUtil.setValue;

import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;

import com.mobeelizer.java.api.MobeelizerErrors;
import com.mobeelizer.java.definition.MobeelizerErrorsHolder;
import com.mobeelizer.java.definition.MobeelizerFieldType;
import com.mobeelizer.java.model.MobeelizerFieldAccessor;

public class BooleanFieldTypeHelper extends FieldTypeHelper {

    public BooleanFieldTypeHelper() {
        super(MobeelizerFieldType.BOOLEAN);
    }

    @Override
    protected String[] getTypeDefinition(final MobeelizerFieldAccessor field, final boolean required, final Object defaultValue,
            final Map<String, String> options) {
        Boolean dV = (Boolean) defaultValue;
        return new String[] { getSingleDefinition(field.getName(), "INTEGER(1)", required, dV == null ? null : dV ? "1" : "0",
                false) };
    }

    @Override
    protected <T> void setNotNullValueFromDatabaseToEntity(final Cursor cursor, final int columnIndex, final T entity,
            final MobeelizerFieldAccessor field, final Map<String, String> options) {
        setValue(field, entity, getType().convertFromDatabaseValueToEntityValue(field, cursor.getInt(columnIndex)));
    }

    @Override
    protected void setNotNullValueFromEntityToDatabase(final ContentValues values, final Object value,
            final MobeelizerFieldAccessor field, final Map<String, String> options, final MobeelizerErrorsHolder errors) {
        Integer intValue = (Integer) getType().convertFromEntityValueToDatabaseValue(field, value, options, errors);

        if (!errors.isValid()) {
            return;
        }

        values.put(field.getName(), intValue);
    }

    @Override
    protected void setNullValueFromEntityToDatabase(final ContentValues values, final MobeelizerFieldAccessor field,
            final Map<String, String> options, final MobeelizerErrors errors) {
        values.put(field.getName(), (Integer) null);
    }

    @Override
    protected void setNotNullValueFromDatabaseToMap(final Cursor cursor, final int columnIndex, final Map<String, String> values,
            final MobeelizerFieldAccessor field, final Map<String, String> options) {
        values.put(field.getName(), Boolean.toString(cursor.getInt(columnIndex) == 1));
    }

    @Override
    protected void setNullValueFromDatabaseToMap(final Cursor cursor, final int columnIndex, final Map<String, String> values,
            final MobeelizerFieldAccessor field, final Map<String, String> options) {
        values.put(field.getName(), null);
    }

    @Override
    protected void setNotNullValueFromMapToDatabase(final ContentValues values, final String value,
            final MobeelizerFieldAccessor field, final Map<String, String> options, final MobeelizerErrors errors) {
        values.put(field.getName(), Boolean.valueOf(value) ? 1 : 0);
    }

    @Override
    protected void setNullValueFromMapToDatabase(final ContentValues values, final MobeelizerFieldAccessor field,
            final Map<String, String> options, final MobeelizerErrors errors) {
        values.put(field.getName(), (Integer) null);
    }
}

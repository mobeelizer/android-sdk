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

import static com.mobeelizer.mobile.android.model.MobeelizerReflectionUtil.setValue;

import java.lang.reflect.Field;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;

import com.mobeelizer.mobile.android.MobeelizerErrorsImpl;
import com.mobeelizer.mobile.android.api.MobeelizerErrors;

public class BooleanFieldTypeHelper extends FieldTypeHelper {

    public BooleanFieldTypeHelper() {
        super(Boolean.class, Boolean.TYPE);
    }

    @Override
    protected String[] getTypeDefinition(final Field field, final boolean required, final Object defaultValue,
            final Map<String, String> options) {
        Boolean dV = (Boolean) defaultValue;
        return new String[] { getSingleDefinition(field.getName(), "INTEGER(1)", required, dV == null ? null : dV ? "1" : "0",
                false) };
    }

    @Override
    protected <T> void setNotNullValueFromDatabaseToEntity(final Cursor cursor, final int columnIndex, final T entity,
            final Field field, final Map<String, String> options) {
        setValue(field, entity, cursor.getInt(columnIndex) == 1);
    }

    @Override
    protected void setNotNullValueFromEntityToDatabase(final ContentValues values, final Object value, final Field field,
            final Map<String, String> options, final MobeelizerErrorsImpl errors) {
        values.put(field.getName(), ((Boolean) value) ? 1 : 0);
    }

    @Override
    protected void setNullValueFromEntityToDatabase(final ContentValues values, final Field field,
            final Map<String, String> options, final MobeelizerErrors errors) {
        values.put(field.getName(), (Integer) null);
    }

    @Override
    protected Object convertTypeDefaultValue(final Field field, final String defaultValue, final Map<String, String> options) {
        if (defaultValue == null) {
            return null;
        } else if ("true".equals(defaultValue)) {
            return true;
        } else if ("false".equals(defaultValue)) {
            return false;
        } else {
            throw new IllegalStateException("Invalid default value '" + defaultValue + "' for Boolean type.");
        }
    }

    @Override
    protected void setNotNullValueFromDatabaseToMap(final Cursor cursor, final int columnIndex, final Map<String, String> values,
            final Field field, final Map<String, String> options) {
        values.put(field.getName(), Boolean.toString(cursor.getInt(columnIndex) == 1));
    }

    @Override
    protected void setNullValueFromDatabaseToMap(final Cursor cursor, final int columnIndex, final Map<String, String> values,
            final Field field, final Map<String, String> options) {
        values.put(field.getName(), null);
    }

    @Override
    protected void setNotNullValueFromMapToDatabase(final ContentValues values, final String value, final Field field,
            final Map<String, String> options, final MobeelizerErrors errors) {
        values.put(field.getName(), Boolean.valueOf(value) ? 1 : 0);
    }

    @Override
    protected void setNullValueFromMapToDatabase(final ContentValues values, final Field field,
            final Map<String, String> options, final MobeelizerErrors errors) {
        values.put(field.getName(), (Integer) null);
    }
}

// 
// FieldTypeHelper.java
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

import static com.mobeelizer.java.model.MobeelizerReflectionUtil.getValue;

import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;

import com.mobeelizer.java.api.MobeelizerErrors;
import com.mobeelizer.java.definition.MobeelizerErrorsHolder;
import com.mobeelizer.java.definition.MobeelizerFieldType;
import com.mobeelizer.java.model.MobeelizerFieldAccessor;

public abstract class FieldTypeHelper {

    private final MobeelizerFieldType type;

    public FieldTypeHelper(final MobeelizerFieldType type) {
        this.type = type;
    }

    protected MobeelizerFieldType getType() {
        return type;
    }

    public <T> void setValueFromDatabaseToEntity(final Cursor cursor, final T entity, final MobeelizerFieldAccessor field,
            final Map<String, String> options) {
        int columnIndex = cursor.getColumnIndex(field.getName());

        if (cursor.isNull(columnIndex)) {
            return;
        }

        setNotNullValueFromDatabaseToEntity(cursor, columnIndex, entity, field, options);
    }

    public void setValueFromDatabaseToMap(final Cursor cursor, final Map<String, String> values,
            final MobeelizerFieldAccessor field, final Map<String, String> options) {
        int columnIndex = cursor.getColumnIndex(field.getName());

        if (cursor.isNull(columnIndex)) {
            setNullValueFromDatabaseToMap(cursor, columnIndex, values, field, options);
        } else {
            setNotNullValueFromDatabaseToMap(cursor, columnIndex, values, field, options);
        }
    }

    protected abstract void setNotNullValueFromDatabaseToMap(final Cursor cursor, final int columnIndex,
            final Map<String, String> values, final MobeelizerFieldAccessor field, final Map<String, String> options);

    protected abstract void setNullValueFromDatabaseToMap(final Cursor cursor, final int columnIndex,
            final Map<String, String> values, final MobeelizerFieldAccessor field, final Map<String, String> options);

    public <T> void setValueFromEntityToDatabase(final ContentValues values, final T entity, final MobeelizerFieldAccessor field,
            final boolean required, final Map<String, String> options, final MobeelizerErrorsHolder errors) {
        Object value = getValue(field, entity);

        if (value == null && required) {
            errors.addFieldCanNotBeEmpty(field.getName());
            return;
        }

        if (value == null) {
            setNullValueFromEntityToDatabase(values, field, options, errors);
        } else {
            setNotNullValueFromEntityToDatabase(values, value, field, options, errors);
        }
    }

    protected abstract void setNotNullValueFromEntityToDatabase(final ContentValues values, final Object value,
            final MobeelizerFieldAccessor field, final Map<String, String> options, final MobeelizerErrorsHolder errors);

    protected abstract void setNullValueFromEntityToDatabase(final ContentValues values, final MobeelizerFieldAccessor field,
            final Map<String, String> options, final MobeelizerErrors errors);

    protected abstract <T> void setNotNullValueFromDatabaseToEntity(final Cursor cursor, final int columnIndex, final T entity,
            final MobeelizerFieldAccessor field, final Map<String, String> options);

    public String[] getDefinition(final MobeelizerFieldAccessor field, final boolean required, final Object defaultValue,
            final Map<String, String> options) {
        if (!required && field.getType().isPrimitive()) {
            throw new IllegalStateException("Field '" + field.getName() + "' mustn't be a primitive type.");
        }
        return getTypeDefinition(field, required, defaultValue, options);
    }

    protected abstract String[] getTypeDefinition(final MobeelizerFieldAccessor field, final boolean required,
            final Object defaultValue, final Map<String, String> options);

    protected String getSingleDefinition(final String name, final String type, final boolean required, final String defaultValue,
            final boolean quoteDefaultValue) {
        StringBuilder sb = new StringBuilder().append(name).append(" ").append(type);
        if (required) {
            sb.append(" NOT NULL");
        }
        if (defaultValue != null) {
            if (quoteDefaultValue) {
                sb.append(" DEFAULT ").append(DatabaseUtils.sqlEscapeString(defaultValue));
            } else {
                sb.append(" DEFAULT ").append(defaultValue);
            }
        }
        return sb.toString();
    }

    public void setValueFromMapToDatabase(final ContentValues values, final Map<String, String> map,
            final MobeelizerFieldAccessor field, final boolean required, final Map<String, String> options,
            final MobeelizerErrorsHolder errors) {
        String value = map.get(field.getName());

        if (value == null && required) {
            errors.addFieldCanNotBeEmpty(field.getName());
            return;
        }

        if (value == null) {
            setNullValueFromMapToDatabase(values, field, options, errors);
        } else {
            setNotNullValueFromMapToDatabase(values, value, field, options, errors);
        }
    }

    protected abstract void setNotNullValueFromMapToDatabase(final ContentValues values, final String value,
            final MobeelizerFieldAccessor field, final Map<String, String> options, final MobeelizerErrors errors);

    protected abstract void setNullValueFromMapToDatabase(final ContentValues values, final MobeelizerFieldAccessor field,
            final Map<String, String> options, final MobeelizerErrors errors);

}

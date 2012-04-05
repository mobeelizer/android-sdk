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

import static com.mobeelizer.mobile.android.model.MobeelizerReflectionUtil.getValue;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;

import com.mobeelizer.mobile.android.MobeelizerErrorsImpl;
import com.mobeelizer.mobile.android.api.MobeelizerErrors;

public abstract class FieldTypeHelper {

    private final Set<Class<?>> accessibleTypes;

    public FieldTypeHelper(final Class<?>... accessibleTypes) {
        this.accessibleTypes = new HashSet<Class<?>>(Arrays.asList(accessibleTypes));
    }

    public <T> void setValueFromDatabaseToEntity(final Cursor cursor, final T entity, final Field field,
            final Map<String, String> options) {
        int columnIndex = cursor.getColumnIndex(field.getName());

        if (cursor.isNull(columnIndex)) {
            return;
        }

        setNotNullValueFromDatabaseToEntity(cursor, columnIndex, entity, field, options);
    }

    public void setValueFromDatabaseToMap(final Cursor cursor, final Map<String, String> values, final Field field,
            final Map<String, String> options) {
        int columnIndex = cursor.getColumnIndex(field.getName());

        if (cursor.isNull(columnIndex)) {
            setNullValueFromDatabaseToMap(cursor, columnIndex, values, field, options);
        } else {
            setNotNullValueFromDatabaseToMap(cursor, columnIndex, values, field, options);
        }
    }

    protected abstract void setNotNullValueFromDatabaseToMap(final Cursor cursor, final int columnIndex,
            final Map<String, String> values, final Field field, final Map<String, String> options);

    protected abstract void setNullValueFromDatabaseToMap(final Cursor cursor, final int columnIndex,
            final Map<String, String> values, final Field field, final Map<String, String> options);

    public <T> void setValueFromEntityToDatabase(final ContentValues values, final T entity, final Field field,
            final boolean required, final Map<String, String> options, final MobeelizerErrorsImpl errors) {
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
            final Field field, final Map<String, String> options, final MobeelizerErrorsImpl errors);

    protected abstract void setNullValueFromEntityToDatabase(final ContentValues values, final Field field,
            final Map<String, String> options, final MobeelizerErrors errors);

    protected abstract <T> void setNotNullValueFromDatabaseToEntity(final Cursor cursor, final int columnIndex, final T entity,
            final Field field, final Map<String, String> options);

    public String[] getDefinition(final Field field, final boolean required, final Object defaultValue,
            final Map<String, String> options) {
        if (!required && field.getType().isPrimitive()) {
            throw new IllegalStateException("Field '" + field.getName() + "' of '" + field.getDeclaringClass().getCanonicalName()
                    + "' mustn't be a primitive type.");
        }
        return getTypeDefinition(field, required, defaultValue, options);
    }

    protected abstract String[] getTypeDefinition(final Field field, final boolean required, final Object defaultValue,
            final Map<String, String> options);

    public Object convertDefaultValue(final Field field, final String defaultValue, final Map<String, String> options) {
        return convertTypeDefaultValue(field, defaultValue, options);
    }

    protected abstract Object convertTypeDefaultValue(final Field field, final String defaultValue,
            final Map<String, String> options);

    public Set<Class<?>> getAccessibleTypes() {
        return accessibleTypes;
    }

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

    public void setValueFromMapToDatabase(final ContentValues values, final Map<String, String> map, final Field field,
            final boolean required, final Map<String, String> options, final MobeelizerErrorsImpl errors) {
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

    protected abstract void setNotNullValueFromMapToDatabase(final ContentValues values, final String value, final Field field,
            final Map<String, String> options, final MobeelizerErrors errors);

    protected abstract void setNullValueFromMapToDatabase(final ContentValues values, final Field field,
            final Map<String, String> options, final MobeelizerErrors errors);

}

// 
// DateFieldTypeHelper.java
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
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;

import com.mobeelizer.mobile.android.MobeelizerErrorsImpl;
import com.mobeelizer.mobile.android.api.MobeelizerErrors;

public class DateFieldTypeHelper extends FieldTypeHelper {

    public DateFieldTypeHelper() {
        super(Date.class, Long.class, Long.TYPE, Calendar.class);
    }

    @Override
    protected void setNotNullValueFromEntityToDatabase(final ContentValues values, final Object value, final Field field,
            final Map<String, String> options, final MobeelizerErrorsImpl errors) {

        Long longValue = null;

        if (value instanceof Date) {
            longValue = ((Date) value).getTime();
        } else if (value instanceof Calendar) {
            longValue = ((Calendar) value).getTime().getTime();
        } else {
            longValue = ((Number) value).longValue();
        }

        values.put(field.getName(), longValue);
    }

    @Override
    protected void setNullValueFromEntityToDatabase(final ContentValues values, final Field field,
            final Map<String, String> options, final MobeelizerErrors errors) {
        values.put(field.getName(), (Long) null);
    }

    @Override
    protected <T> void setNotNullValueFromDatabaseToEntity(final Cursor cursor, final int columnIndex, final T entity,
            final Field field, final Map<String, String> options) {
        Long value = cursor.getLong(columnIndex);

        if (field.getType().equals(Long.TYPE) || field.getType().equals(Long.class)) {
            setValue(field, entity, value);
        } else if (field.getType().equals(Date.class)) {
            setValue(field, entity, new Date(value));
        } else if (field.getType().equals(Calendar.class)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(value));
            setValue(field, entity, calendar);
        } else {
            throw new IllegalStateException("Cannot get date from '" + field.getType().getCanonicalName() + "'.");
        }
    }

    @Override
    protected String[] getTypeDefinition(final Field field, final boolean required, final Object defaultValue,
            final Map<String, String> options) {
        return new String[] { getSingleDefinition(field.getName(), "INTEGER(19)", required,
                defaultValue == null ? null : Long.toString(((Date) defaultValue).getTime()), false) };
    }

    @Override
    protected Object convertTypeDefaultValue(final Field field, final String defaultValue, final Map<String, String> options) {
        if (defaultValue == null) {
            return null;
        } else {
            try {
                return new Date(Long.parseLong(defaultValue));
            } catch (NumberFormatException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }
    }

    @Override
    protected void setNotNullValueFromDatabaseToMap(final Cursor cursor, final int columnIndex, final Map<String, String> values,
            final Field field, final Map<String, String> options) {
        values.put(field.getName(), Long.toString(cursor.getLong(columnIndex)));
    }

    @Override
    protected void setNullValueFromDatabaseToMap(final Cursor cursor, final int columnIndex, final Map<String, String> values,
            final Field field, final Map<String, String> options) {
        values.put(field.getName(), null);
    }

    @Override
    protected void setNotNullValueFromMapToDatabase(final ContentValues values, final String value, final Field field,
            final Map<String, String> options, final MobeelizerErrors errors) {
        values.put(field.getName(), Long.parseLong(value));
    }

    @Override
    protected void setNullValueFromMapToDatabase(final ContentValues values, final Field field,
            final Map<String, String> options, final MobeelizerErrors errors) {
        values.put(field.getName(), (Long) null);
    }

}

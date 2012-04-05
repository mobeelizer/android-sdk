// 
// IntegerFieldTypeHelper.java
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
import java.math.BigInteger;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;

import com.mobeelizer.mobile.android.MobeelizerErrorsImpl;
import com.mobeelizer.mobile.android.api.MobeelizerErrors;

public class IntegerFieldTypeHelper extends FieldTypeHelper {

    public IntegerFieldTypeHelper() {
        super(Integer.class, Integer.TYPE, Short.class, Short.TYPE, Byte.class, Byte.TYPE, Long.class, Long.TYPE,
                BigInteger.class);
    }

    @Override
    protected void setNotNullValueFromEntityToDatabase(final ContentValues values, final Object value, final Field field,
            final Map<String, String> options, final MobeelizerErrorsImpl errors) {
        Long longValue = ((Number) value).longValue();

        int maxValue = getMaxValue(options);
        int minValue = getMinValue(options);

        if (longValue > maxValue) {
            errors.addFieldMustBeLessThan(field.getName(), (long) maxValue);
            return;
        }

        if (longValue < minValue) {
            errors.addFieldMustBeGreaterThan(field.getName(), (long) minValue);
            return;
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

        if (field.getType().equals(Integer.TYPE) || field.getType().equals(Integer.class)) {
            checkRange(value, Integer.MIN_VALUE, Integer.MAX_VALUE);
            setValue(field, entity, value.intValue());
        } else if (field.getType().equals(Short.TYPE) || field.getType().equals(Short.class)) {
            checkRange(value, Short.MIN_VALUE, Short.MAX_VALUE);
            setValue(field, entity, value.shortValue());
        } else if (field.getType().equals(Long.TYPE) || field.getType().equals(Long.class)) {
            setValue(field, entity, value);
        } else if (field.getType().equals(Byte.TYPE) || field.getType().equals(Byte.class)) {
            checkRange(value, Byte.MIN_VALUE, Byte.MAX_VALUE);
            setValue(field, entity, value.byteValue());
        } else if (field.getType().equals(BigInteger.class)) {
            setValue(field, entity, BigInteger.valueOf(value));
        } else {
            throw new IllegalStateException("Cannot get integer from '" + field.getType().getCanonicalName() + "'.");
        }
    }

    private void checkRange(final long value, final int minValue, final int maxValue) {
        if (value < minValue || value > maxValue) {
            throw new IllegalStateException("Value " + value + " out of range <" + minValue + "," + maxValue + ">.");
        }
    }

    @Override
    protected String[] getTypeDefinition(final Field field, final boolean required, final Object defaultValue,
            final Map<String, String> options) {
        int length = Integer.toString(getMaxValue(options)).length();
        return new String[] { getSingleDefinition(field.getName(), "INTEGER(" + length + ")", required,
                defaultValue == null ? null : Long.toString((Long) defaultValue), false) };
    }

    @Override
    protected Object convertTypeDefaultValue(final Field field, final String defaultValue, final Map<String, String> options) {
        if (defaultValue == null) {
            return null;
        } else {
            try {
                return Long.parseLong(defaultValue);
            } catch (NumberFormatException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }
    }

    @Override
    protected void setNotNullValueFromDatabaseToMap(final Cursor cursor, final int columnIndex, final Map<String, String> values,
            final Field field, final Map<String, String> options) {
        values.put(field.getName(), Integer.toString(cursor.getInt(columnIndex)));
    }

    @Override
    protected void setNullValueFromDatabaseToMap(final Cursor cursor, final int columnIndex, final Map<String, String> values,
            final Field field, final Map<String, String> options) {
        values.put(field.getName(), null);
    }

    @Override
    protected void setNotNullValueFromMapToDatabase(final ContentValues values, final String value, final Field field,
            final Map<String, String> options, final MobeelizerErrors errors) {
        values.put(field.getName(), Integer.parseInt(value));
    }

    @Override
    protected void setNullValueFromMapToDatabase(final ContentValues values, final Field field,
            final Map<String, String> options, final MobeelizerErrors errors) {
        values.put(field.getName(), (Long) null);
    }

    private int getMaxValue(final Map<String, String> options) {
        return options.containsKey("maxValue") ? Integer.valueOf(options.get("maxValue")) : Integer.MAX_VALUE;
    }

    private int getMinValue(final Map<String, String> options) {
        return options.containsKey("minValue") ? Integer.valueOf(options.get("minValue")) : Integer.MIN_VALUE;
    }

}

// 
// DecimalFieldTypeHelper.java
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
import java.math.BigDecimal;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;

import com.mobeelizer.mobile.android.MobeelizerErrorsImpl;
import com.mobeelizer.mobile.android.api.MobeelizerErrors;

public class DecimalFieldTypeHelper extends FieldTypeHelper {

    public DecimalFieldTypeHelper() {
        super(Double.class, Double.TYPE, Float.class, Float.TYPE, BigDecimal.class);
    }

    @Override
    protected void setNotNullValueFromEntityToDatabase(final ContentValues values, final Object value, final Field field,
            final Map<String, String> options, final MobeelizerErrorsImpl errors) {
        Double doubleValue = ((Number) value).doubleValue();

        boolean includeMaxValue = getIncludeMaxValue(options);
        boolean includeMinValue = getIncludeMinValue(options);
        BigDecimal minValue = getMinValue(options);
        BigDecimal maxValue = getMaxValue(options);

        if (includeMaxValue && doubleValue > maxValue.doubleValue()) {
            errors.addFieldMustBeLessThanOrEqualTo(field.getName(), maxValue);
            return;
        }

        if (!includeMaxValue && doubleValue >= maxValue.doubleValue()) {
            errors.addFieldMustBeLessThan(field.getName(), maxValue);
            return;
        }

        if (includeMinValue && doubleValue < minValue.doubleValue()) {
            errors.addFieldMustBeGreaterThanOrEqual(field.getName(), minValue);
            return;
        }

        if (!includeMinValue && doubleValue <= minValue.doubleValue()) {
            errors.addFieldMustBeGreaterThan(field.getName(), minValue);
            return;
        }

        values.put(field.getName(), doubleValue);
    }

    @Override
    protected void setNullValueFromEntityToDatabase(final ContentValues values, final Field field,
            final Map<String, String> options, final MobeelizerErrors errors) {
        values.put(field.getName(), (Double) null);
    }

    @Override
    protected <T> void setNotNullValueFromDatabaseToEntity(final Cursor cursor, final int columnIndex, final T entity,
            final Field field, final Map<String, String> options) {
        Double value = cursor.getDouble(columnIndex);

        if (field.getType().equals(Double.TYPE) || field.getType().equals(Double.class)) {
            setValue(field, entity, value);
        } else if (field.getType().equals(Float.TYPE) || field.getType().equals(Float.class)) {
            setValue(field, entity, value.floatValue());
        } else if (field.getType().equals(BigDecimal.class)) {
            setValue(field, entity, BigDecimal.valueOf(value));
        } else {
            throw new IllegalStateException("Cannot get decimal from '" + field.getType().getCanonicalName() + "'.");
        }
    }

    @Override
    protected String[] getTypeDefinition(final Field field, final boolean required, final Object defaultValue,
            final Map<String, String> options) {
        int length = getMaxValue(options).setScale(0, BigDecimal.ROUND_FLOOR).toString().length();
        return new String[] { getSingleDefinition(field.getName(), "REAL(" + length + "," + getScale(options) + ")", required,
                defaultValue == null ? null : ((BigDecimal) defaultValue).toPlainString(), false) };
    }

    @Override
    protected Object convertTypeDefaultValue(final Field field, final String defaultValue, final Map<String, String> options) {
        if (defaultValue == null) {
            return null;
        } else {
            try {
                return new BigDecimal(defaultValue).setScale(getScale(options));
            } catch (ArithmeticException e) {
                throw new IllegalStateException(e.getMessage(), e);
            } catch (NumberFormatException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }
    }

    @Override
    protected void setNotNullValueFromDatabaseToMap(final Cursor cursor, final int columnIndex, final Map<String, String> values,
            final Field field, final Map<String, String> options) {
        values.put(field.getName(), BigDecimal.valueOf(cursor.getDouble(columnIndex)).toPlainString());
    }

    @Override
    protected void setNullValueFromDatabaseToMap(final Cursor cursor, final int columnIndex, final Map<String, String> values,
            final Field field, final Map<String, String> options) {
        values.put(field.getName(), null);
    }

    @Override
    protected void setNotNullValueFromMapToDatabase(final ContentValues values, final String value, final Field field,
            final Map<String, String> options, final MobeelizerErrors errors) {
        values.put(field.getName(), Double.parseDouble(value));
    }

    @Override
    protected void setNullValueFromMapToDatabase(final ContentValues values, final Field field,
            final Map<String, String> options, final MobeelizerErrors errors) {
        values.put(field.getName(), (Double) null);
    }

    private BigDecimal getMaxValue(final Map<String, String> options) {
        return options.containsKey("maxValue") ? new BigDecimal(options.get("maxValue")) : BigDecimal.valueOf(Double.MAX_VALUE);
    }

    private BigDecimal getMinValue(final Map<String, String> options) {
        return options.containsKey("minValue") ? new BigDecimal(options.get("minValue")) : BigDecimal.valueOf(-Double.MAX_VALUE);
    }

    private boolean getIncludeMinValue(final Map<String, String> options) {
        return options.containsKey("includeMinValue") ? "true".equals(options.get("includeMinValue")) : true;
    }

    private boolean getIncludeMaxValue(final Map<String, String> options) {
        return options.containsKey("includeMaxValue") ? "true".equals(options.get("includeMaxValue")) : true;
    }

    private int getScale(final Map<String, String> options) {
        return options.containsKey("scale") ? Integer.valueOf(options.get("scale")) : 3;
    }

}

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

import static com.mobeelizer.java.model.MobeelizerReflectionUtil.setValue;

import java.util.Date;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;

import com.mobeelizer.java.api.MobeelizerErrors;
import com.mobeelizer.java.definition.MobeelizerErrorsHolder;
import com.mobeelizer.java.definition.MobeelizerFieldType;
import com.mobeelizer.java.model.MobeelizerFieldAccessor;

public class DateFieldTypeHelper extends FieldTypeHelper {

    public DateFieldTypeHelper() {
        super(MobeelizerFieldType.DATE);
    }

    @Override
    protected void setNotNullValueFromEntityToDatabase(final ContentValues values, final Object value,
            final MobeelizerFieldAccessor field, final Map<String, String> options, final MobeelizerErrorsHolder errors) {
        Long longValue = (Long) getType().convertFromEntityValueToDatabaseValue(field, value, options, errors);

        if (!errors.isValid()) {
            return;
        }

        values.put(field.getName(), longValue);
    }

    @Override
    protected void setNullValueFromEntityToDatabase(final ContentValues values, final MobeelizerFieldAccessor field,
            final Map<String, String> options, final MobeelizerErrors errors) {
        values.put(field.getName(), (Long) null);
    }

    @Override
    protected <T> void setNotNullValueFromDatabaseToEntity(final Cursor cursor, final int columnIndex, final T entity,
            final MobeelizerFieldAccessor field, final Map<String, String> options) {
        setValue(field, entity, getType().convertFromDatabaseValueToEntityValue(field, cursor.getLong(columnIndex)));
    }

    @Override
    protected String[] getTypeDefinition(final MobeelizerFieldAccessor field, final boolean required, final Object defaultValue,
            final Map<String, String> options) {
        return new String[] { getSingleDefinition(field.getName(), "INTEGER(19)", required,
                defaultValue == null ? null : Long.toString(((Date) defaultValue).getTime()), false) };
    }

    @Override
    protected void setNotNullValueFromDatabaseToMap(final Cursor cursor, final int columnIndex, final Map<String, String> values,
            final MobeelizerFieldAccessor field, final Map<String, String> options) {
        values.put(field.getName(), Long.toString(cursor.getLong(columnIndex)));
    }

    @Override
    protected void setNullValueFromDatabaseToMap(final Cursor cursor, final int columnIndex, final Map<String, String> values,
            final MobeelizerFieldAccessor field, final Map<String, String> options) {
        values.put(field.getName(), null);
    }

    @Override
    protected void setNotNullValueFromMapToDatabase(final ContentValues values, final String value,
            final MobeelizerFieldAccessor field, final Map<String, String> options, final MobeelizerErrors errors) {
        values.put(field.getName(), Long.parseLong(value));
    }

    @Override
    protected void setNullValueFromMapToDatabase(final ContentValues values, final MobeelizerFieldAccessor field,
            final Map<String, String> options, final MobeelizerErrors errors) {
        values.put(field.getName(), (Long) null);
    }

}

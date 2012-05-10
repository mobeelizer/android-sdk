// 
// FileFieldTypeHelper.java
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

import java.lang.reflect.Field;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;

import com.mobeelizer.java.api.MobeelizerErrors;
import com.mobeelizer.java.api.MobeelizerFile;
import com.mobeelizer.java.definition.MobeelizerErrorsHolder;
import com.mobeelizer.java.definition.MobeelizerFieldType;
import com.mobeelizer.mobile.android.MobeelizerDatabaseImpl;
import com.mobeelizer.mobile.android.MobeelizerFileImpl;

public class FileFieldTypeHelper extends FieldTypeHelper {

    public static final String _GUID = "_guid";

    public static final String _NAME = "_name";

    private static final String JSON_GUID = "guid";

    private static final String JSON_NAME = "filename";

    public FileFieldTypeHelper() {
        super(MobeelizerFieldType.FILE);
    }

    @Override
    public void setValueFromDatabaseToMap(final Cursor cursor, final Map<String, String> values, final Field field,
            final Map<String, String> options) {
        int columnIndex = cursor.getColumnIndex(field.getName() + _GUID);

        if (cursor.isNull(columnIndex)) {
            values.put(field.getName(), null);
        } else {
            try {
                JSONObject json = new JSONObject();
                json.put(JSON_GUID, cursor.getString(columnIndex));
                json.put(JSON_NAME, cursor.getString(cursor.getColumnIndex(field.getName() + _NAME)));

                values.put(field.getName(), json.toString());
            } catch (JSONException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }
    }

    @Override
    protected void setNotNullValueFromDatabaseToMap(final Cursor cursor, final int columnIndex, final Map<String, String> values,
            final Field field, final Map<String, String> options) {
        // empty
    }

    @Override
    protected void setNullValueFromDatabaseToMap(final Cursor cursor, final int columnIndex, final Map<String, String> values,
            final Field field, final Map<String, String> options) {
        // empty
    }

    @Override
    protected void setNotNullValueFromEntityToDatabase(final ContentValues values, final Object value, final Field field,
            final Map<String, String> options, final MobeelizerErrorsHolder errors) {
        MobeelizerFile file = (MobeelizerFile) getType().convertFromEntityValueToDatabaseValue(field, value, options, errors);

        if (!errors.isValid()) {
            return;
        }

        values.put(field.getName() + _GUID, file.getGuid());
        values.put(field.getName() + _NAME, file.getName());
    }

    @Override
    protected void setNullValueFromEntityToDatabase(final ContentValues values, final Field field,
            final Map<String, String> options, final MobeelizerErrors errors) {
        values.put(field.getName() + _GUID, (String) null);
        values.put(field.getName() + _NAME, (String) null);
    }

    @Override
    protected <T> void setNotNullValueFromDatabaseToEntity(final Cursor cursor, final int columnIndex, final T entity,
            final Field field, final Map<String, String> options) {
        // empty
    }

    @Override
    public <T> void setValueFromDatabaseToEntity(final Cursor cursor, final T entity, final Field field,
            final Map<String, String> options) {
        int columnIndex = cursor.getColumnIndex(field.getName() + FileFieldTypeHelper._GUID);

        if (cursor.isNull(columnIndex)) {
            return;
        }

        String[] file = new String[] { cursor.getString(columnIndex),
                cursor.getString(cursor.getColumnIndex(field.getName() + FileFieldTypeHelper._NAME)) };

        MobeelizerFile mobeelizerFile = (MobeelizerFile) getType().convertFromDatabaseValueToEntityValue(field, file);

        setValue(field, entity, new MobeelizerFileImpl(mobeelizerFile.getName(), mobeelizerFile.getGuid()));
    }

    @Override
    protected String[] getTypeDefinition(final Field field, final boolean required, final Object defaultValue,
            final Map<String, String> options) {
        return new String[] {
                getSingleDefinition(field.getName() + _GUID, "TEXT(36)", required, null, false) + " REFERENCES "
                        + MobeelizerDatabaseImpl._FILE_TABLE_NAME + "(" + MobeelizerDatabaseImpl._FILE_GUID + ")",
                getSingleDefinition(field.getName() + _NAME, "TEXT(255)", required, null, false) };
    }

    @Override
    protected void setNotNullValueFromMapToDatabase(final ContentValues values, final String value, final Field field,
            final Map<String, String> options, final MobeelizerErrors errors) {
        try {
            JSONObject json = new JSONObject(value);
            values.put(field.getName() + _GUID, json.getString(JSON_GUID));
            values.put(field.getName() + _NAME, json.getString(JSON_NAME));
        } catch (JSONException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @Override
    protected void setNullValueFromMapToDatabase(final ContentValues values, final Field field,
            final Map<String, String> options, final MobeelizerErrors errors) {
        values.put(field.getName() + _GUID, (String) null);
        values.put(field.getName() + _NAME, (String) null);
    }

}

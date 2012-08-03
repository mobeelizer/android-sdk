// 
// TextFieldTypeHelper.java
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

import com.mobeelizer.java.api.MobeelizerErrorsBuilder;
import com.mobeelizer.java.definition.MobeelizerFieldType;
import com.mobeelizer.java.model.MobeelizerFieldAccessor;

public class TextFieldTypeHelper extends FieldTypeHelper {

	public TextFieldTypeHelper() {
		super(MobeelizerFieldType.TEXT);
	}

	@Override
	public String[] getTypeDefinition(final MobeelizerFieldAccessor field, final boolean required, final Object defaultValue,
			final Map<String, String> options) {
		return new String[] { getSingleDefinition(field.getName(), "TEXT(" + getMaxLength(options) + ")", required,
				(String) defaultValue, true) };
	}

	@Override
	protected <T> void setNotNullValueFromDatabaseToEntity(final Cursor cursor, final int columnIndex, final T entity,
			final MobeelizerFieldAccessor field, final Map<String, String> options) {
		setValue(field, entity, getType().convertFromDatabaseValueToEntityValue(field, cursor.getString(columnIndex)));
	}

	@Override
	protected void setNotNullValueFromEntityToDatabase(final ContentValues values, final Object value,
			final MobeelizerFieldAccessor field, final Map<String, String> options,
			final MobeelizerErrorsBuilder errors) {
		String stringValue = (String) getType().convertFromEntityValueToDatabaseValue(field, value, options, errors);

		if (!errors.hasNoErrors()) {
			return;
		}

		values.put(field.getName(), stringValue);
	}

	@Override
	protected void setNullValueFromEntityToDatabase(final ContentValues values, final MobeelizerFieldAccessor field,
			final Map<String, String> options, final MobeelizerErrorsBuilder errors) {
		values.put(field.getName(), (String) null);
	}

	@Override
	protected void setNotNullValueFromDatabaseToMap(final Cursor cursor, final int columnIndex, final Map<String, String> values,
			final MobeelizerFieldAccessor field, final Map<String, String> options) {
		values.put(field.getName(), cursor.getString(columnIndex));
	}

	@Override
	protected void setNullValueFromDatabaseToMap(final Cursor cursor, final int columnIndex, final Map<String, String> values,
			final MobeelizerFieldAccessor field, final Map<String, String> options) {
		values.put(field.getName(), null);
	}

	@Override
	protected void setNotNullValueFromMapToDatabase(final ContentValues values, final String value,
			final MobeelizerFieldAccessor field, final Map<String, String> options,
			final MobeelizerErrorsBuilder errors) {
		values.put(field.getName(), value);
	}

	@Override
	protected void setNullValueFromMapToDatabase(final ContentValues values, final MobeelizerFieldAccessor field,
			final Map<String, String> options, final MobeelizerErrorsBuilder errors) {
		values.put(field.getName(), (String) null);
	}

	private int getMaxLength(final Map<String, String> options) {
		return options.containsKey("maxLength") ? Integer.valueOf(options.get("maxLength")) : 255;
	}

}

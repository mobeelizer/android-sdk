// 
// TextFieldTypeHelperTest.java
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

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;

import com.mobeelizer.java.api.MobeelizerErrorsBuilder;
import com.mobeelizer.java.model.MobeelizerFieldAccessor;
import com.mobeelizer.java.model.MobeelizerReflectionUtil;
import com.mobeelizer.java.model.ReflectionMobeelizerFieldAccessor;
import com.mobeelizer.mobile.android.TestEntity;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ TextFieldTypeHelper.class, ContentValues.class, DatabaseUtils.class })
public class TextFieldTypeHelperTest {

    private MobeelizerFieldAccessor fieldString;

    @Before
    public void init() {
        PowerMockito.mockStatic(DatabaseUtils.class);
        PowerMockito.when(DatabaseUtils.sqlEscapeString("default")).thenReturn("'escapedDefault'");

        fieldString = new ReflectionMobeelizerFieldAccessor(MobeelizerReflectionUtil.getField(TestEntity.class, "string",
                String.class));
    }

    @Test
    public void shouldInsertIntoEnum() throws Exception {
        // given
        Field helperField = FieldType.TEXT.getClass().getDeclaredField("helper");
        helperField.setAccessible(true);
        Object helper = helperField.get(FieldType.TEXT);

        // then
        assertTrue(helper instanceof TextFieldTypeHelper);
    }

    @Test
    public void shouldGetAccessibleTypes() throws Exception {
        // when
        Set<Class<?>> types = FieldType.TEXT.getType().getAccessibleTypes();

        // then
        assertEquals(1, types.size());
        assertTrue(types.contains(String.class));
    }

    @Test
    public void shouldSetValueFromEntityToDatabase() throws Exception {
        Map<String, String> options = new HashMap<String, String>();
        options.put("maxLength", "9");

        ContentValues values = mock(ContentValues.class);
        MobeelizerErrorsBuilder errors = mock(MobeelizerErrorsBuilder.class);
        when(errors.hasNoErrors()).thenReturn(true);

        TestEntity entity = new TestEntity();
        entity.setString("nameValue");

        // when
        FieldType.TEXT.setValueFromEntityToDatabase(values, entity, fieldString, true, options, errors);

        // then
        verify(values).put("string", "nameValue");
    }

    @Test
    public void shouldSetErrorWhileSetNullValueFromEntityToDatabase() throws Exception {
        Map<String, String> options = new HashMap<String, String>();
        options.put("maxLength", "100");

        ContentValues values = mock(ContentValues.class);
        MobeelizerErrorsBuilder errors = mock(MobeelizerErrorsBuilder.class);

        TestEntity entity = new TestEntity();

        // when
        FieldType.TEXT.setValueFromEntityToDatabase(values, entity, fieldString, true, options, errors);

        // then
        verify(values, never()).put(eq("string"), anyString());
        verify(errors).addFieldCanNotBeEmpty("string");
    }

    @Test
    public void shouldSetErrorWhileSetTooLongValueFromEntityToDatabase() throws Exception {
        Map<String, String> options = new HashMap<String, String>();
        options.put("maxLength", "3");

        ContentValues values = mock(ContentValues.class);
        MobeelizerErrorsBuilder errors = mock(MobeelizerErrorsBuilder.class);

        TestEntity entity = new TestEntity();
        entity.setString("qwer");

        // when
        FieldType.TEXT.setValueFromEntityToDatabase(values, entity, fieldString, true, options, errors);

        // then
        verify(values, never()).put(eq("string"), anyString());
        verify(errors).addFieldIsTooLong("string", 3);
    }

    @Test
    public void shouldSetValueFromDatabaseToEntity() throws Exception {
        // given
        Map<String, String> options = new HashMap<String, String>();

        TestEntity entity = new TestEntity();

        Cursor cursor = mock(Cursor.class);
        when(cursor.getColumnIndex("string")).thenReturn(13);
        when(cursor.isNull(13)).thenReturn(false);
        when(cursor.getString(13)).thenReturn("name");

        // when
        FieldType.TEXT.setValueFromDatabaseToEntity(cursor, entity, fieldString, options);

        // then
        assertEquals("name", entity.getString());
    }

    @Test
    public void shouldSetNullValueFromDatabaseToEntity() throws Exception {
        // given
        Map<String, String> options = new HashMap<String, String>();

        TestEntity entity = new TestEntity();

        Cursor cursor = mock(Cursor.class);
        when(cursor.getColumnIndex("string")).thenReturn(13);
        when(cursor.isNull(13)).thenReturn(true);

        // when
        FieldType.TEXT.setValueFromDatabaseToEntity(cursor, entity, fieldString, options);

        // then
        assertNull(entity.getString());
    }

    @Test
    public void shouldGetDefinition() throws Exception {
        Map<String, String> options = new HashMap<String, String>();
        options.put("maxLength", "100");

        // when
        String[] definition = FieldType.TEXT.getDefinition(fieldString, true, "default", options);

        // then
        assertEquals(1, definition.length);
        assertEquals("string TEXT(100) NOT NULL DEFAULT 'escapedDefault'", definition[0]);
    }

    @Test
    public void shouldGetDefinition2() throws Exception {
        Map<String, String> options = new HashMap<String, String>();
        options.put("maxLength", "10");

        // when
        String[] definition = FieldType.TEXT.getDefinition(fieldString, true, null, options);

        // then
        assertEquals(1, definition.length);
        assertEquals("string TEXT(10) NOT NULL", definition[0]);
    }

    @Test
    public void shouldGetDefinition3() throws Exception {
        Map<String, String> options = new HashMap<String, String>();
        options.put("maxLength", "10");

        // when
        String[] definition = FieldType.TEXT.getDefinition(fieldString, false, null, options);

        // then
        assertEquals(1, definition.length);
        assertEquals("string TEXT(10)", definition[0]);
    }

    @Test
    public void shouldGetDefinition4() throws Exception {
        Map<String, String> options = new HashMap<String, String>();
        options.put("maxLength", "10");

        // when
        String[] definition = FieldType.TEXT.getDefinition(fieldString, false, "default", options);

        // then
        assertEquals(1, definition.length);
        assertEquals("string TEXT(10) DEFAULT 'escapedDefault'", definition[0]);
    }

    @Test
    public void shouldConvertNullDefaultValue() throws Exception {
        // when
        Map<String, String> options = new HashMap<String, String>();

        Object defaultValue = FieldType.TEXT.getType().convertDefaultValue(fieldString, null, options);

        // then
        assertNull(defaultValue);
    }

    @Test
    public void shouldConvertDefaultValue() throws Exception {
        // when
        Map<String, String> options = new HashMap<String, String>();

        Object defaultValue = FieldType.TEXT.getType().convertDefaultValue(fieldString, "default", options);

        // then
        assertEquals("default", defaultValue);
    }

    @Test
    public void shouldSetNullValueFromEntityToDatabase() throws Exception {
        Map<String, String> options = new HashMap<String, String>();

        ContentValues values = mock(ContentValues.class);
        MobeelizerErrorsBuilder errors = mock(MobeelizerErrorsBuilder.class);

        TestEntity entity = new TestEntity();
        entity.setString(null);

        // when
        FieldType.TEXT.setValueFromEntityToDatabase(values, entity, fieldString, false, options, errors);

        // then
        verify(values).put("string", (String) null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldSetNullValueFromDatabaseToMap() throws Exception {
        // given
        Map<String, String> options = new HashMap<String, String>();

        Cursor cursor = mock(Cursor.class);
        when(cursor.getColumnIndex("string")).thenReturn(13);
        when(cursor.isNull(13)).thenReturn(true);

        Map<String, String> map = mock(Map.class);
        // when
        FieldType.TEXT.setValueFromDatabaseToMap(cursor, map, fieldString, options);

        // then
        verify(map).put("string", null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldSetNotNullValueFromDatabaseToMap() throws Exception {
        // given
        Map<String, String> options = new HashMap<String, String>();

        Cursor cursor = mock(Cursor.class);
        when(cursor.getColumnIndex("string")).thenReturn(13);
        when(cursor.isNull(13)).thenReturn(false);
        when(cursor.getString(13)).thenReturn("value");

        Map<String, String> map = mock(Map.class);

        // when
        FieldType.TEXT.setValueFromDatabaseToMap(cursor, map, fieldString, options);

        // then
        verify(map).put("string", "value");
    }

    @Test
    public void shouldSetNullValueFromMapToDatabase() throws Exception {
        Map<String, String> options = new HashMap<String, String>();

        ContentValues values = mock(ContentValues.class);
        MobeelizerErrorsBuilder errors = mock(MobeelizerErrorsBuilder.class);

        Map<String, String> map = new HashMap<String, String>();

        // when
        FieldType.TEXT.setValueFromMapToDatabase(values, map, fieldString, false, options, errors);

        // then
        verify(values).put("string", (String) null);
    }

    @Test
    public void shouldSetNotNullValueFromMapToDatabase() throws Exception {
        Map<String, String> options = new HashMap<String, String>();

        ContentValues values = mock(ContentValues.class);
        MobeelizerErrorsBuilder errors = mock(MobeelizerErrorsBuilder.class);

        Map<String, String> map = new HashMap<String, String>();
        map.put("string", "value");

        // when
        FieldType.TEXT.setValueFromMapToDatabase(values, map, fieldString, false, options, errors);

        // then
        verify(values).put("string", "value");
    }

}

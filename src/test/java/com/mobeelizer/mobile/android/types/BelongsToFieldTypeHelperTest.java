// 
// BelongsToFieldTypeHelperTest.java
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
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;

import com.mobeelizer.java.definition.MobeelizerErrorsHolder;
import com.mobeelizer.java.model.MobeelizerFieldAccessor;
import com.mobeelizer.java.model.MobeelizerReflectionUtil;
import com.mobeelizer.java.model.ReflectionMobeelizerFieldAccessor;
import com.mobeelizer.mobile.android.Mobeelizer;
import com.mobeelizer.mobile.android.MobeelizerDatabaseImpl;
import com.mobeelizer.mobile.android.TestEntity;
import com.mobeelizer.mobile.android.model.MobeelizerAndroidModel;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ BelongsToFieldTypeHelper.class, ContentValues.class, DatabaseUtils.class, Mobeelizer.class })
public class BelongsToFieldTypeHelperTest {

    private MobeelizerFieldAccessor fieldString;

    private MobeelizerDatabaseImpl database;

    @Before
    public void init() {
        PowerMockito.mockStatic(Mobeelizer.class);
        database = mock(MobeelizerDatabaseImpl.class);
        PowerMockito.when(Mobeelizer.getDatabase()).thenReturn(database);

        fieldString = new ReflectionMobeelizerFieldAccessor(MobeelizerReflectionUtil.getField(TestEntity.class, "string",
                new HashSet<Class<?>>(Arrays.asList(new Class<?>[] { String.class }))));
    }

    @Test
    public void shouldInsertIntoEnum() throws Exception {
        // given
        Field helperField = FieldType.BELONGS_TO.getClass().getDeclaredField("helper");
        helperField.setAccessible(true);
        Object helper = helperField.get(FieldType.BELONGS_TO);

        // then
        assertTrue(helper instanceof BelongsToFieldTypeHelper);
    }

    @Test
    public void shouldGetAccessibleTypes() throws Exception {
        // when
        Set<Class<?>> types = FieldType.BELONGS_TO.getType().getAccessibleTypes();

        // then
        assertEquals(1, types.size());
        assertTrue(types.contains(String.class));
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void shouldSetValueFromEntityToDatabase() throws Exception {
        // given
        Map<String, String> options = new HashMap<String, String>();
        options.put("model", "modelName");

        ContentValues values = mock(ContentValues.class);
        MobeelizerErrorsHolder errors = mock(MobeelizerErrorsHolder.class);
        when(errors.isValid()).thenReturn(true);

        String uuid = UUID.randomUUID().toString();

        MobeelizerAndroidModel modelDefinition = mock(MobeelizerAndroidModel.class);
        Class clazz = TmpClass.class;
        when(modelDefinition.getMappingClass()).thenReturn(clazz);
        when(database.getModel("modelName")).thenReturn(modelDefinition);
        when(database.exists(clazz, uuid)).thenReturn(true);

        TestEntity entity = new TestEntity();
        entity.setString(uuid);

        // when
        FieldType.BELONGS_TO.setValueFromEntityToDatabase(values, entity, fieldString, true, options, errors);

        // then
        verify(values).put("string", uuid);
    }

    private static class TmpClass {
    };

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void shouldSetErrorWhileSetNotExistingReferenceValueFromEntityToDatabase() throws Exception {
        // given
        Map<String, String> options = new HashMap<String, String>();
        options.put("model", "modelName");

        ContentValues values = mock(ContentValues.class);
        MobeelizerErrorsHolder errors = mock(MobeelizerErrorsHolder.class);
        when(errors.isValid()).thenReturn(true);

        String uuid = UUID.randomUUID().toString();

        MobeelizerAndroidModel modelDefinition = mock(MobeelizerAndroidModel.class);
        Class clazz = TmpClass.class;
        when(modelDefinition.getMappingClass()).thenReturn(clazz);
        when(database.getModel("modelName")).thenReturn(modelDefinition);
        when(database.exists(clazz, uuid)).thenReturn(false);

        TestEntity entity = new TestEntity();
        entity.setString(uuid);

        // when
        FieldType.BELONGS_TO.setValueFromEntityToDatabase(values, entity, fieldString, true, options, errors);

        // then
        verify(values, never()).put("string", uuid);
        verify(errors).addFieldMissingReferenceError("string", uuid);
    }

    @Test
    public void shouldSetErrorWhileSetNullValueFromEntityToDatabase() throws Exception {
        // given
        Map<String, String> options = new HashMap<String, String>();

        ContentValues values = mock(ContentValues.class);
        MobeelizerErrorsHolder errors = mock(MobeelizerErrorsHolder.class);

        TestEntity entity = new TestEntity();

        // when
        FieldType.BELONGS_TO.setValueFromEntityToDatabase(values, entity, fieldString, true, options, errors);

        // then
        verify(values, never()).put(eq("string"), anyInt());
        verify(errors).addFieldCanNotBeEmpty("string");
    }

    @Test
    public void shouldSetValueFromDatabaseToEntity() throws Exception {
        // given
        Map<String, String> options = new HashMap<String, String>();

        TestEntity entity = new TestEntity();

        String uuid = UUID.randomUUID().toString();

        Cursor cursor = mock(Cursor.class);
        when(cursor.getColumnIndex("string")).thenReturn(13);
        when(cursor.isNull(13)).thenReturn(false);
        when(cursor.getString(13)).thenReturn(uuid);

        // when
        FieldType.BELONGS_TO.setValueFromDatabaseToEntity(cursor, entity, fieldString, options);

        // then
        assertEquals(uuid, entity.getString());
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
        FieldType.BELONGS_TO.setValueFromDatabaseToEntity(cursor, entity, fieldString, options);

        // then
        assertNull(entity.getString());
    }

    @Test
    public void shouldGetDefinition() throws Exception {
        // given
        Map<String, String> options = new HashMap<String, String>();
        options.put("model", "modelName");

        // when
        String[] definition = FieldType.BELONGS_TO.getDefinition(fieldString, true, false, options);

        // then
        assertEquals(1, definition.length);
        assertEquals("string TEXT(36) NOT NULL REFERENCES modelName(guid)", definition[0]);
    }

    @Test
    public void shouldGetDefinition2() throws Exception {
        // given
        Map<String, String> options = new HashMap<String, String>();
        options.put("model", "modelName");

        // when
        String[] definition = FieldType.BELONGS_TO.getDefinition(fieldString, false, null, options);

        // then
        assertEquals(1, definition.length);
        assertEquals("string TEXT(36) REFERENCES modelName(guid)", definition[0]);
    }

    @Test
    public void shouldConvertNullDefaultValue() throws Exception {
        // when
        Map<String, String> options = new HashMap<String, String>();

        Object defaultValue = FieldType.BELONGS_TO.getType().convertDefaultValue(fieldString, null, options);

        // then
        assertNull(defaultValue);
    }

    @Test
    public void shouldConvertDefaultValueToNull() throws Exception {
        // when
        Map<String, String> options = new HashMap<String, String>();

        Object defaultValue = FieldType.BELONGS_TO.getType().convertDefaultValue(fieldString, "xxx", options);

        // then
        assertNull(defaultValue);
    }

    @Test
    public void shouldSetNullValueFromEntityToDatabase() throws Exception {
        Map<String, String> options = new HashMap<String, String>();

        ContentValues values = mock(ContentValues.class);
        MobeelizerErrorsHolder errors = mock(MobeelizerErrorsHolder.class);

        TestEntity entity = new TestEntity();

        // when
        FieldType.BELONGS_TO.setValueFromEntityToDatabase(values, entity, fieldString, false, options, errors);

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
        FieldType.BELONGS_TO.setValueFromDatabaseToMap(cursor, map, fieldString, options);

        // then
        verify(map).put("string", null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldSetNotNullValueFromDatabaseToMap() throws Exception {
        // given
        Map<String, String> options = new HashMap<String, String>();

        String uuid = UUID.randomUUID().toString();

        Cursor cursor = mock(Cursor.class);
        when(cursor.getColumnIndex("string")).thenReturn(13);
        when(cursor.isNull(13)).thenReturn(false);
        when(cursor.getString(13)).thenReturn(uuid);

        Map<String, String> map = mock(Map.class);

        // when
        FieldType.BELONGS_TO.setValueFromDatabaseToMap(cursor, map, fieldString, options);

        // then
        verify(map).put("string", uuid);
    }

    @Test
    public void shouldSetNullValueFromMapToDatabase() throws Exception {
        Map<String, String> options = new HashMap<String, String>();

        ContentValues values = mock(ContentValues.class);
        MobeelizerErrorsHolder errors = mock(MobeelizerErrorsHolder.class);

        Map<String, String> map = new HashMap<String, String>();

        // when
        FieldType.BELONGS_TO.setValueFromMapToDatabase(values, map, fieldString, false, options, errors);

        // then
        verify(values).put("string", (String) null);
    }

    @Test
    public void shouldSetNotNullValueFromMapToDatabase() throws Exception {
        Map<String, String> options = new HashMap<String, String>();

        ContentValues values = mock(ContentValues.class);
        MobeelizerErrorsHolder errors = mock(MobeelizerErrorsHolder.class);

        String uuid = UUID.randomUUID().toString();

        Map<String, String> map = new HashMap<String, String>();
        map.put("string", uuid);

        // when
        FieldType.BELONGS_TO.setValueFromMapToDatabase(values, map, fieldString, false, options, errors);

        // then
        verify(values).put("string", uuid);
    }

}

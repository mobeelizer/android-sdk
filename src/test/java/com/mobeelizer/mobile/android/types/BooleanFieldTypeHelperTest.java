// 
// BooleanFieldTypeHelperTest.java
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
import static org.junit.Assert.assertFalse;
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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
@PrepareForTest({ BooleanFieldTypeHelper.class, ContentValues.class, DatabaseUtils.class })
public class BooleanFieldTypeHelperTest {

    private MobeelizerFieldAccessor fieldBooleanP;

    private MobeelizerFieldAccessor fieldBooleanO;

    @Before
    public void init() {
        fieldBooleanP = new ReflectionMobeelizerFieldAccessor(MobeelizerReflectionUtil.getField(TestEntity.class, "booleanP",
                new HashSet<Class<?>>(Arrays.asList(new Class<?>[] { Boolean.class, Boolean.TYPE }))));
        fieldBooleanO = new ReflectionMobeelizerFieldAccessor(MobeelizerReflectionUtil.getField(TestEntity.class, "booleanO",
                new HashSet<Class<?>>(Arrays.asList(new Class<?>[] { Boolean.class, Boolean.TYPE }))));
    }

    @Test
    public void shouldInsertIntoEnum() throws Exception {
        // given
        Field helperField = FieldType.BOOLEAN.getClass().getDeclaredField("helper");
        helperField.setAccessible(true);
        Object helper = helperField.get(FieldType.BOOLEAN);

        // then
        assertTrue(helper instanceof BooleanFieldTypeHelper);
    }

    @Test
    public void shouldGetAccessibleTypes() throws Exception {
        // when
        Set<Class<?>> types = FieldType.BOOLEAN.getType().getAccessibleTypes();

        // then
        assertEquals(2, types.size());
        assertTrue(types.contains(Boolean.class));
        assertTrue(types.contains(Boolean.TYPE));
    }

    @Test
    public void shouldSetValueFromEntityToDatabase() throws Exception {
        // given
        Map<String, String> options = new HashMap<String, String>();

        ContentValues values = mock(ContentValues.class);
        MobeelizerErrorsBuilder errors = mock(MobeelizerErrorsBuilder.class);
        when(errors.hasNoErrors()).thenReturn(true);

        TestEntity entity = new TestEntity();
        entity.setBooleanP(true);

        // when
        FieldType.BOOLEAN.setValueFromEntityToDatabase(values, entity, fieldBooleanP, true, options, errors);

        // then
        verify(values).put("booleanP", Integer.valueOf(1));
    }

    @Test
    public void shouldSetValueFromEntityToDatabase2() throws Exception {
        // given
        Map<String, String> options = new HashMap<String, String>();

        ContentValues values = mock(ContentValues.class);
        MobeelizerErrorsBuilder errors = mock(MobeelizerErrorsBuilder.class);
        when(errors.hasNoErrors()).thenReturn(true);

        TestEntity entity = new TestEntity();
        entity.setBooleanO(true);

        // when
        FieldType.BOOLEAN.setValueFromEntityToDatabase(values, entity, fieldBooleanO, true, options, errors);

        // then
        verify(values).put("booleanO", Integer.valueOf(1));
    }

    @Test
    public void shouldSetValueFromEntityToDatabase3() throws Exception {
        // given
        Map<String, String> options = new HashMap<String, String>();

        ContentValues values = mock(ContentValues.class);
        MobeelizerErrorsBuilder errors = mock(MobeelizerErrorsBuilder.class);
        when(errors.hasNoErrors()).thenReturn(true);

        TestEntity entity = new TestEntity();
        entity.setBooleanP(false);

        // when
        FieldType.BOOLEAN.setValueFromEntityToDatabase(values, entity, fieldBooleanP, true, options, errors);

        // then
        verify(values).put("booleanP", Integer.valueOf(0));
    }

    @Test
    public void shouldSetValueFromEntityToDatabase4() throws Exception {
        // given
        Map<String, String> options = new HashMap<String, String>();

        ContentValues values = mock(ContentValues.class);
        MobeelizerErrorsBuilder errors = mock(MobeelizerErrorsBuilder.class);
        when(errors.hasNoErrors()).thenReturn(true);

        TestEntity entity = new TestEntity();
        entity.setBooleanO(false);

        // when
        FieldType.BOOLEAN.setValueFromEntityToDatabase(values, entity, fieldBooleanO, true, options, errors);

        // then
        verify(values).put("booleanO", Integer.valueOf(0));
    }

    @Test
    public void shouldSetErrorWhileSetNullValueFromEntityToDatabase() throws Exception {
        // given
        Map<String, String> options = new HashMap<String, String>();

        ContentValues values = mock(ContentValues.class);
        MobeelizerErrorsBuilder errors = mock(MobeelizerErrorsBuilder.class);

        TestEntity entity = new TestEntity();

        // when
        FieldType.BOOLEAN.setValueFromEntityToDatabase(values, entity, fieldBooleanO, true, options, errors);

        // then
        verify(values, never()).put(eq("booleanO"), anyInt());
        verify(errors).addFieldCanNotBeEmpty("booleanO");
    }

    @Test
    public void shouldSetValueFromDatabaseToEntity() throws Exception {
        // given
        Map<String, String> options = new HashMap<String, String>();

        TestEntity entity = new TestEntity();

        Cursor cursor = mock(Cursor.class);
        when(cursor.getColumnIndex("booleanP")).thenReturn(13);
        when(cursor.isNull(13)).thenReturn(false);
        when(cursor.getInt(13)).thenReturn(1);

        // when
        FieldType.BOOLEAN.setValueFromDatabaseToEntity(cursor, entity, fieldBooleanP, options);

        // then
        assertTrue(entity.isBooleanP());
    }

    @Test
    public void shouldSetValueFromDatabaseToEntity2() throws Exception {
        // given
        Map<String, String> options = new HashMap<String, String>();

        TestEntity entity = new TestEntity();

        Cursor cursor = mock(Cursor.class);
        when(cursor.getColumnIndex("booleanP")).thenReturn(13);
        when(cursor.isNull(13)).thenReturn(false);
        when(cursor.getInt(13)).thenReturn(0);

        // when
        FieldType.BOOLEAN.setValueFromDatabaseToEntity(cursor, entity, fieldBooleanP, options);

        // then
        assertFalse(entity.isBooleanP());
    }

    @Test
    public void shouldSetValueFromDatabaseToEntity3() throws Exception {
        // given
        Map<String, String> options = new HashMap<String, String>();

        TestEntity entity = new TestEntity();

        Cursor cursor = mock(Cursor.class);
        when(cursor.getColumnIndex("booleanO")).thenReturn(13);
        when(cursor.isNull(13)).thenReturn(false);
        when(cursor.getInt(13)).thenReturn(1);

        // when
        FieldType.BOOLEAN.setValueFromDatabaseToEntity(cursor, entity, fieldBooleanO, options);

        // then
        assertTrue(entity.getBooleanO());
    }

    @Test
    public void shouldSetValueFromDatabaseToEntity4() throws Exception {
        // given
        Map<String, String> options = new HashMap<String, String>();

        TestEntity entity = new TestEntity();

        Cursor cursor = mock(Cursor.class);
        when(cursor.getColumnIndex("booleanO")).thenReturn(13);
        when(cursor.isNull(13)).thenReturn(false);
        when(cursor.getInt(13)).thenReturn(0);

        // when
        FieldType.BOOLEAN.setValueFromDatabaseToEntity(cursor, entity, fieldBooleanO, options);

        // then
        assertFalse(entity.getBooleanO());
    }

    @Test
    public void shouldSetNullValueFromDatabaseToEntity() throws Exception {
        // given
        Map<String, String> options = new HashMap<String, String>();

        TestEntity entity = new TestEntity();

        Cursor cursor = mock(Cursor.class);
        when(cursor.getColumnIndex("booleanO")).thenReturn(13);
        when(cursor.isNull(13)).thenReturn(true);

        // when
        FieldType.BOOLEAN.setValueFromDatabaseToEntity(cursor, entity, fieldBooleanO, options);

        // then
        assertNull(entity.getBooleanO());
    }

    @Test
    public void shouldGetDefinition() throws Exception {
        // given
        Map<String, String> options = new HashMap<String, String>();

        // when
        String[] definition = FieldType.BOOLEAN.getDefinition(fieldBooleanO, true, false, options);

        // then
        assertEquals(1, definition.length);
        assertEquals("booleanO INTEGER(1) NOT NULL DEFAULT 0", definition[0]);
    }

    @Test
    public void shouldGetDefinition2() throws Exception {
        // given
        Map<String, String> options = new HashMap<String, String>();

        // when
        String[] definition = FieldType.BOOLEAN.getDefinition(fieldBooleanO, true, null, options);

        // then
        assertEquals(1, definition.length);
        assertEquals("booleanO INTEGER(1) NOT NULL", definition[0]);
    }

    @Test
    public void shouldGetDefinition3() throws Exception {
        // given
        Map<String, String> options = new HashMap<String, String>();

        // when
        String[] definition = FieldType.BOOLEAN.getDefinition(fieldBooleanO, false, null, options);

        // then
        assertEquals(1, definition.length);
        assertEquals("booleanO INTEGER(1)", definition[0]);
    }

    @Test
    public void shouldGetDefinition4() throws Exception {
        // given
        Map<String, String> options = new HashMap<String, String>();

        // when
        String[] definition = FieldType.BOOLEAN.getDefinition(fieldBooleanO, false, true, options);

        // then
        assertEquals(1, definition.length);
        assertEquals("booleanO INTEGER(1) DEFAULT 1", definition[0]);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailsForPrimitiveBooleanAndNotRequiredField() throws Exception {
        // given
        Map<String, String> options = new HashMap<String, String>();

        // when
        FieldType.BOOLEAN.getDefinition(fieldBooleanP, false, true, options);
    }

    @Test
    public void shouldConvertNullDefaultValue() throws Exception {
        // when
        Map<String, String> options = new HashMap<String, String>();

        Object defaultValue = FieldType.BOOLEAN.getType().convertDefaultValue(fieldBooleanO, null, options);

        // then
        assertNull(defaultValue);
    }

    @Test
    public void shouldConvertDefaultValue() throws Exception {
        // when
        Map<String, String> options = new HashMap<String, String>();

        Object defaultValue = FieldType.BOOLEAN.getType().convertDefaultValue(fieldBooleanP, "true", options);

        // then
        assertEquals(true, defaultValue);
    }

    @Test
    public void shouldConvertDefaultValue2() throws Exception {
        // when
        Map<String, String> options = new HashMap<String, String>();

        Object defaultValue = FieldType.BOOLEAN.getType().convertDefaultValue(fieldBooleanO, "false", options);

        // then
        assertEquals(false, defaultValue);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldConvertDefaultValue3() throws Exception {
        // when
        Map<String, String> options = new HashMap<String, String>();

        FieldType.BOOLEAN.getType().convertDefaultValue(fieldBooleanO, "False", options);
    }

    @Test
    public void shouldSetNullValueFromEntityToDatabase() throws Exception {
        Map<String, String> options = new HashMap<String, String>();

        ContentValues values = mock(ContentValues.class);
        MobeelizerErrorsBuilder errors = mock(MobeelizerErrorsBuilder.class);

        TestEntity entity = new TestEntity();

        // when
        FieldType.BOOLEAN.setValueFromEntityToDatabase(values, entity, fieldBooleanO, false, options, errors);

        // then
        verify(values).put("booleanO", (Integer) null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldSetNullValueFromDatabaseToMap() throws Exception {
        // given
        Map<String, String> options = new HashMap<String, String>();

        Cursor cursor = mock(Cursor.class);
        when(cursor.getColumnIndex("booleanO")).thenReturn(13);
        when(cursor.isNull(13)).thenReturn(true);

        Map<String, String> map = mock(Map.class);
        // when
        FieldType.BOOLEAN.setValueFromDatabaseToMap(cursor, map, fieldBooleanO, options);

        // then
        verify(map).put("booleanO", null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldSetNotNullValueFromDatabaseToMap() throws Exception {
        // given
        Map<String, String> options = new HashMap<String, String>();

        Cursor cursor = mock(Cursor.class);
        when(cursor.getColumnIndex("booleanO")).thenReturn(13);
        when(cursor.isNull(13)).thenReturn(false);
        when(cursor.getInt(13)).thenReturn(1);

        Map<String, String> map = mock(Map.class);

        // when
        FieldType.BOOLEAN.setValueFromDatabaseToMap(cursor, map, fieldBooleanO, options);

        // then
        verify(map).put("booleanO", "true");
    }

    @Test
    public void shouldSetNullValueFromMapToDatabase() throws Exception {
        Map<String, String> options = new HashMap<String, String>();

        ContentValues values = mock(ContentValues.class);
        MobeelizerErrorsBuilder errors = mock(MobeelizerErrorsBuilder.class);

        Map<String, String> map = new HashMap<String, String>();

        // when
        FieldType.BOOLEAN.setValueFromMapToDatabase(values, map, fieldBooleanO, false, options, errors);

        // then
        verify(values).put("booleanO", (Integer) null);
    }

    @Test
    public void shouldSetNotNullValueFromMapToDatabase() throws Exception {
        Map<String, String> options = new HashMap<String, String>();

        ContentValues values = mock(ContentValues.class);
        MobeelizerErrorsBuilder errors = mock(MobeelizerErrorsBuilder.class);

        Map<String, String> map = new HashMap<String, String>();
        map.put("booleanO", "false");

        // when
        FieldType.BOOLEAN.setValueFromMapToDatabase(values, map, fieldBooleanO, false, options, errors);

        // then
        verify(values).put("booleanO", 0);
    }

}

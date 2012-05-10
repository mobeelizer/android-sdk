// 
// DateFieldTypeHelperTest.java
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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

import com.mobeelizer.java.definition.MobeelizerErrorsHolder;
import com.mobeelizer.java.model.MobeelizerReflectionUtil;
import com.mobeelizer.mobile.android.TestEntity;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ DateFieldTypeHelper.class, ContentValues.class, DatabaseUtils.class })
public class DateFieldTypeHelperTest {

    private Field fieldDate;

    private Field fieldCalendar;

    private Field fieldLongO;

    private Field fieldLongP;

    @Before
    public void init() {
        HashSet<Class<?>> types = new HashSet<Class<?>>(Arrays.asList(new Class<?>[] { Date.class, Long.class, Long.TYPE,
                Calendar.class }));
        fieldLongO = MobeelizerReflectionUtil.getField(TestEntity.class, "longO", types);
        fieldLongP = MobeelizerReflectionUtil.getField(TestEntity.class, "longP", types);
        fieldDate = MobeelizerReflectionUtil.getField(TestEntity.class, "date", types);
        fieldCalendar = MobeelizerReflectionUtil.getField(TestEntity.class, "calendar", types);
    }

    @Test
    public void shouldInsertIntoEnum() throws Exception {
        // given
        Field helperField = FieldType.DATE.getClass().getDeclaredField("helper");
        helperField.setAccessible(true);
        Object helper = helperField.get(FieldType.DATE);

        // then
        assertTrue(helper instanceof DateFieldTypeHelper);
    }

    @Test
    public void shouldGetAccessibleTypes() throws Exception {
        // when
        Set<Class<?>> types = FieldType.DATE.getType().getAccessibleTypes();

        // then
        assertEquals(4, types.size());
        assertTrue(types.contains(Long.class));
        assertTrue(types.contains(Long.TYPE));
        assertTrue(types.contains(Date.class));
        assertTrue(types.contains(Calendar.class));
    }

    @Test
    public void shouldSetValueFromEntityToDatabase() throws Exception {
        shouldSetValueFromEntityToDatabase(fieldDate, new Date(10L), 10L);
        shouldSetValueFromEntityToDatabase(fieldLongP, 11L, 11L);
        shouldSetValueFromEntityToDatabase(fieldLongO, Long.valueOf(12L), 12L);
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(13L));
        shouldSetValueFromEntityToDatabase(fieldCalendar, c, 13L);
    }

    private void shouldSetValueFromEntityToDatabase(final Field field, final Object entityValue, final Long contentValue) {
        Map<String, String> options = new HashMap<String, String>();

        ContentValues values = mock(ContentValues.class);
        MobeelizerErrorsHolder errors = mock(MobeelizerErrorsHolder.class);
        when(errors.isValid()).thenReturn(true);

        TestEntity entity = new TestEntity();

        try {
            field.set(entity, entityValue);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }

        // when
        FieldType.DATE.setValueFromEntityToDatabase(values, entity, field, true, options, errors);

        // then
        verify(values).put(field.getName(), contentValue);
    }

    @Test
    public void shouldSetErrorWhileSetNullValueFromEntityToDatabase() throws Exception {
        Map<String, String> options = new HashMap<String, String>();

        ContentValues values = mock(ContentValues.class);
        MobeelizerErrorsHolder errors = mock(MobeelizerErrorsHolder.class);

        TestEntity entity = new TestEntity();

        // when
        FieldType.DATE.setValueFromEntityToDatabase(values, entity, fieldDate, true, options, errors);

        // then
        verify(values, never()).put(eq("date"), anyString());
        verify(errors).addFieldCanNotBeEmpty("date");
    }

    @Test
    public void shouldSetValueFromDatabaseToEntity() throws Exception {
        shouldSetValueFromDatabaseToEntity(fieldDate, 10L, new Date(10L));
        shouldSetValueFromDatabaseToEntity(fieldLongO, 11L, Long.valueOf(11L));
        shouldSetValueFromDatabaseToEntity(fieldLongP, 12L, 12L);
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(13L));
        shouldSetValueFromDatabaseToEntity(fieldCalendar, 13L, c);
    }

    private void shouldSetValueFromDatabaseToEntity(final Field field, final Long databaseValue, final Object entityValue) {
        // given
        Map<String, String> options = new HashMap<String, String>();

        TestEntity entity = new TestEntity();

        Cursor cursor = mock(Cursor.class);
        when(cursor.getColumnIndex(field.getName())).thenReturn(13);
        when(cursor.isNull(13)).thenReturn(false);
        when(cursor.getLong(13)).thenReturn(databaseValue);

        // when
        FieldType.DATE.setValueFromDatabaseToEntity(cursor, entity, field, options);

        // then
        try {
            assertEquals(entityValue, field.get(entity));
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(e.getMessage());
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    @Test
    public void shouldSetNullValueFromDatabaseToEntity() throws Exception {
        // given
        Map<String, String> options = new HashMap<String, String>();

        TestEntity entity = new TestEntity();

        Cursor cursor = mock(Cursor.class);
        when(cursor.getColumnIndex("date")).thenReturn(13);
        when(cursor.isNull(13)).thenReturn(true);

        // when
        FieldType.DATE.setValueFromDatabaseToEntity(cursor, entity, fieldDate, options);

        // then
        assertNull(entity.getDate());
    }

    @Test
    public void shouldGetDefinition() throws Exception {
        Map<String, String> options = new HashMap<String, String>();

        // when
        String[] definition = FieldType.DATE.getDefinition(fieldCalendar, true, new Date(100L), options);

        // then
        assertEquals(1, definition.length);
        assertEquals("calendar INTEGER(19) NOT NULL DEFAULT 100", definition[0]);
    }

    @Test
    public void shouldGetDefinition2() throws Exception {
        Map<String, String> options = new HashMap<String, String>();

        // when
        String[] definition = FieldType.DATE.getDefinition(fieldLongP, true, null, options);

        // then
        assertEquals(1, definition.length);
        assertEquals("longP INTEGER(19) NOT NULL", definition[0]);
    }

    @Test
    public void shouldGetDefinition3() throws Exception {
        Map<String, String> options = new HashMap<String, String>();

        // when
        String[] definition = FieldType.DATE.getDefinition(fieldLongO, false, null, options);

        // then
        assertEquals(1, definition.length);
        assertEquals("longO INTEGER(19)", definition[0]);
    }

    @Test
    public void shouldGetDefinition4() throws Exception {
        Map<String, String> options = new HashMap<String, String>();

        // when
        String[] definition = FieldType.DATE.getDefinition(fieldDate, false, new Date(10L), options);

        // then
        assertEquals(1, definition.length);
        assertEquals("date INTEGER(19) DEFAULT 10", definition[0]);
    }

    @Test
    public void shouldConvertNullDefaultValue() throws Exception {
        // when
        Map<String, String> options = new HashMap<String, String>();

        Object defaultValue = FieldType.DATE.getType().convertDefaultValue(fieldDate, null, options);

        // then
        assertNull(defaultValue);
    }

    @Test
    public void shouldConvertDefaultValue() throws Exception {
        // when
        Map<String, String> options = new HashMap<String, String>();

        Object defaultValue = FieldType.DATE.getType().convertDefaultValue(fieldDate, "10", options);

        // then
        assertEquals(new Date(10L), defaultValue);
    }

    @Test
    public void shouldConvertDefaultValue2() throws Exception {
        // when
        Map<String, String> options = new HashMap<String, String>();

        Object defaultValue = FieldType.DATE.getType().convertDefaultValue(fieldDate, "15", options);

        // then
        assertEquals(new Date(15L), defaultValue);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldConvertDefaultValue3() throws Exception {
        // when
        Map<String, String> options = new HashMap<String, String>();

        FieldType.DATE.getType().convertDefaultValue(fieldDate, "1a", options);
    }

    @Test
    public void shouldSetNullValueFromEntityToDatabase() throws Exception {
        Map<String, String> options = new HashMap<String, String>();

        ContentValues values = mock(ContentValues.class);
        MobeelizerErrorsHolder errors = mock(MobeelizerErrorsHolder.class);

        TestEntity entity = new TestEntity();

        // when
        FieldType.DATE.setValueFromEntityToDatabase(values, entity, fieldDate, false, options, errors);

        // then
        verify(values).put("date", (Long) null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldSetNullValueFromDatabaseToMap() throws Exception {
        // given
        Map<String, String> options = new HashMap<String, String>();

        Cursor cursor = mock(Cursor.class);
        when(cursor.getColumnIndex("date")).thenReturn(13);
        when(cursor.isNull(13)).thenReturn(true);

        Map<String, String> map = mock(Map.class);
        // when
        FieldType.DATE.setValueFromDatabaseToMap(cursor, map, fieldDate, options);

        // then
        verify(map).put("date", null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldSetNotNullValueFromDatabaseToMap() throws Exception {
        // given
        Map<String, String> options = new HashMap<String, String>();

        Cursor cursor = mock(Cursor.class);
        when(cursor.getColumnIndex("date")).thenReturn(13);
        when(cursor.isNull(13)).thenReturn(false);
        when(cursor.getLong(13)).thenReturn(1L);

        Map<String, String> map = mock(Map.class);

        // when
        FieldType.DATE.setValueFromDatabaseToMap(cursor, map, fieldDate, options);

        // then
        verify(map).put("date", "1");
    }

    @Test
    public void shouldSetNullValueFromMapToDatabase() throws Exception {
        Map<String, String> options = new HashMap<String, String>();

        ContentValues values = mock(ContentValues.class);
        MobeelizerErrorsHolder errors = mock(MobeelizerErrorsHolder.class);

        Map<String, String> map = new HashMap<String, String>();

        // when
        FieldType.DATE.setValueFromMapToDatabase(values, map, fieldDate, false, options, errors);

        // then
        verify(values).put("date", (Long) null);
    }

    @Test
    public void shouldSetNotNullValueFromMapToDatabase() throws Exception {
        Map<String, String> options = new HashMap<String, String>();

        ContentValues values = mock(ContentValues.class);
        MobeelizerErrorsHolder errors = mock(MobeelizerErrorsHolder.class);

        Map<String, String> map = new HashMap<String, String>();
        map.put("date", "2");

        // when
        FieldType.DATE.setValueFromMapToDatabase(values, map, fieldDate, false, options, errors);

        // then
        verify(values).put("date", 2L);
    }

}

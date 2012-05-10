// 
// IntegerFieldTypeHelperTest.java
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
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.math.BigInteger;
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

import com.mobeelizer.java.definition.MobeelizerErrorsHolder;
import com.mobeelizer.java.model.MobeelizerReflectionUtil;
import com.mobeelizer.mobile.android.TestEntity;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ IntegerFieldTypeHelper.class, ContentValues.class, DatabaseUtils.class })
public class IntegerFieldTypeHelperTest {

    private Field fieldIntegerO;

    private Field fieldIntegerP;

    private Field fieldShortO;

    private Field fieldShortP;

    private Field fieldLongO;

    private Field fieldLongP;

    private Field fieldByteO;

    private Field fieldByteP;

    private Field fieldBigInteger;

    @Before
    public void init() {
        HashSet<Class<?>> types = new HashSet<Class<?>>(Arrays.asList(new Class<?>[] { Integer.class, Integer.TYPE, Short.class,
                Short.TYPE, Byte.class, Byte.TYPE, Long.class, Long.TYPE, BigInteger.class }));
        fieldIntegerO = MobeelizerReflectionUtil.getField(TestEntity.class, "integerO", types);
        fieldIntegerP = MobeelizerReflectionUtil.getField(TestEntity.class, "integerP", types);
        fieldShortO = MobeelizerReflectionUtil.getField(TestEntity.class, "shortO", types);
        fieldShortP = MobeelizerReflectionUtil.getField(TestEntity.class, "shortP", types);
        fieldLongO = MobeelizerReflectionUtil.getField(TestEntity.class, "longO", types);
        fieldLongP = MobeelizerReflectionUtil.getField(TestEntity.class, "longP", types);
        fieldByteO = MobeelizerReflectionUtil.getField(TestEntity.class, "byteO", types);
        fieldByteP = MobeelizerReflectionUtil.getField(TestEntity.class, "byteP", types);
        fieldBigInteger = MobeelizerReflectionUtil.getField(TestEntity.class, "bigInteger", types);
    }

    @Test
    public void shouldInsertIntoEnum() throws Exception {
        // given
        Field helperField = FieldType.INTEGER.getClass().getDeclaredField("helper");
        helperField.setAccessible(true);
        Object helper = helperField.get(FieldType.INTEGER);

        // then
        assertTrue(helper instanceof IntegerFieldTypeHelper);
    }

    @Test
    public void shouldGetAccessibleTypes() throws Exception {
        // when
        Set<Class<?>> types = FieldType.INTEGER.getType().getAccessibleTypes();

        // then
        assertEquals(9, types.size());
        assertTrue(types.contains(Integer.class));
        assertTrue(types.contains(Integer.TYPE));
        assertTrue(types.contains(Short.class));
        assertTrue(types.contains(Short.TYPE));
        assertTrue(types.contains(Byte.class));
        assertTrue(types.contains(Byte.TYPE));
        assertTrue(types.contains(Long.class));
        assertTrue(types.contains(Long.TYPE));
        assertTrue(types.contains(BigInteger.class));
    }

    @Test
    public void shouldSetValueFromEntityToDatabase() throws Exception {
        shouldSetValueFromEntityToDatabase(fieldIntegerP, 10, 10L);
        shouldSetValueFromEntityToDatabase(fieldIntegerO, Integer.valueOf(-10), -10L);
        shouldSetValueFromEntityToDatabase(fieldShortO, Short.valueOf((short) 4), 4L);
        shouldSetValueFromEntityToDatabase(fieldShortP, (short) -2, -2L);
        shouldSetValueFromEntityToDatabase(fieldLongP, -101L, -101L);
        shouldSetValueFromEntityToDatabase(fieldLongO, Long.valueOf(1001L), 1001L);
        shouldSetValueFromEntityToDatabase(fieldByteP, (byte) -3, -3L);
        shouldSetValueFromEntityToDatabase(fieldByteO, Byte.valueOf((byte) 1), 1L);
        shouldSetValueFromEntityToDatabase(fieldBigInteger, BigInteger.valueOf(123L), 123L);
    }

    private void shouldSetValueFromEntityToDatabase(final Field field, final Object entityValue, final Long contentValue) {
        Map<String, String> options = new HashMap<String, String>();
        options.put("minValue", "-101");
        options.put("maxValue", "1001");

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
        FieldType.INTEGER.setValueFromEntityToDatabase(values, entity, field, true, options, errors);

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
        FieldType.INTEGER.setValueFromEntityToDatabase(values, entity, fieldIntegerO, true, options, errors);

        // then
        verify(values, never()).put(eq("integerO"), anyString());
        verify(errors).addFieldCanNotBeEmpty("integerO");
    }

    @Test
    public void shouldSetErrorWhileSetGreaterThanMaxValueFromEntityToDatabase() throws Exception {
        Map<String, String> options = new HashMap<String, String>();
        options.put("maxValue", "10");

        ContentValues values = mock(ContentValues.class);
        MobeelizerErrorsHolder errors = mock(MobeelizerErrorsHolder.class);

        TestEntity entity = new TestEntity();
        entity.setIntegerP(11);

        // when
        FieldType.INTEGER.setValueFromEntityToDatabase(values, entity, fieldIntegerP, true, options, errors);

        // then
        verify(values, never()).put(eq("integerP"), anyString());
        verify(errors).addFieldMustBeLessThan("integerP", 10L);
    }

    @Test
    public void shouldSetErrorWhileSetLowerThanMinValueFromEntityToDatabase() throws Exception {
        Map<String, String> options = new HashMap<String, String>();
        options.put("minValue", "9");

        ContentValues values = mock(ContentValues.class);
        MobeelizerErrorsHolder errors = mock(MobeelizerErrorsHolder.class);

        TestEntity entity = new TestEntity();
        entity.setIntegerP(8);

        // when
        FieldType.INTEGER.setValueFromEntityToDatabase(values, entity, fieldIntegerP, true, options, errors);

        // then
        verify(values, never()).put(eq("integerP"), anyString());
        verify(errors).addFieldMustBeGreaterThan("integerP", 9L);
    }

    @Test
    public void shouldSetValueFromDatabaseToEntity() throws Exception {
        shouldSetValueFromDatabaseToEntity(fieldIntegerO, 10L, Integer.valueOf(10));
        shouldSetValueFromDatabaseToEntity(fieldIntegerP, -10L, -10);
        shouldSetValueFromDatabaseToEntity(fieldShortO, 4L, Short.valueOf((short) 4));
        shouldSetValueFromDatabaseToEntity(fieldShortP, -2L, (short) -2);
        shouldSetValueFromDatabaseToEntity(fieldLongO, -123L, Long.valueOf(-123L));
        shouldSetValueFromDatabaseToEntity(fieldLongP, 22L, 22L);
        shouldSetValueFromDatabaseToEntity(fieldByteO, 2L, Byte.valueOf((byte) 2));
        shouldSetValueFromDatabaseToEntity(fieldByteP, 3L, (byte) 3);
        shouldSetValueFromDatabaseToEntity(fieldBigInteger, 10L, BigInteger.valueOf(10L));

        shouldSetValueFromDatabaseToEntity(fieldIntegerO, Long.valueOf(Integer.MAX_VALUE), Integer.MAX_VALUE);
        shouldSetValueFromDatabaseToEntity(fieldIntegerP, Long.valueOf(Integer.MIN_VALUE), Integer.MIN_VALUE);
        shouldSetValueFromDatabaseToEntity(fieldShortO, Long.valueOf(Short.MAX_VALUE), Short.MAX_VALUE);
        shouldSetValueFromDatabaseToEntity(fieldShortP, Long.valueOf(Short.MIN_VALUE), Short.MIN_VALUE);
        shouldSetValueFromDatabaseToEntity(fieldLongO, Long.MAX_VALUE, Long.MAX_VALUE);
        shouldSetValueFromDatabaseToEntity(fieldLongP, Long.MIN_VALUE, Long.MIN_VALUE);
        shouldSetValueFromDatabaseToEntity(fieldByteO, Long.valueOf(Byte.MAX_VALUE), Byte.MAX_VALUE);
        shouldSetValueFromDatabaseToEntity(fieldByteP, Long.valueOf(Byte.MIN_VALUE), Byte.MIN_VALUE);
        shouldSetValueFromDatabaseToEntity(fieldBigInteger, Long.MAX_VALUE, BigInteger.valueOf(Long.MAX_VALUE));
        shouldSetValueFromDatabaseToEntity(fieldBigInteger, Long.MIN_VALUE, BigInteger.valueOf(Long.MIN_VALUE));

        shouldNotSetValueFromDatabaseToEntity(fieldIntegerO, Long.valueOf(Integer.MAX_VALUE) + 1);
        shouldNotSetValueFromDatabaseToEntity(fieldIntegerP, Long.valueOf(Integer.MIN_VALUE) - 1);
        shouldNotSetValueFromDatabaseToEntity(fieldShortO, Long.valueOf(Short.MAX_VALUE) + 1);
        shouldNotSetValueFromDatabaseToEntity(fieldShortP, Long.valueOf(Short.MIN_VALUE) - 1);
        shouldNotSetValueFromDatabaseToEntity(fieldByteO, Long.valueOf(Byte.MAX_VALUE) + 1);
        shouldNotSetValueFromDatabaseToEntity(fieldByteP, Long.valueOf(Byte.MIN_VALUE) - 1);
    }

    private void shouldNotSetValueFromDatabaseToEntity(final Field field, final Long databaseValue) {
        // given
        Map<String, String> options = new HashMap<String, String>();

        TestEntity entity = new TestEntity();

        Cursor cursor = mock(Cursor.class);
        when(cursor.getColumnIndex(field.getName())).thenReturn(13);
        when(cursor.isNull(13)).thenReturn(false);
        when(cursor.getLong(13)).thenReturn(databaseValue);

        // when
        try {
            FieldType.INTEGER.setValueFromDatabaseToEntity(cursor, entity, field, options);
            fail();
        } catch (IllegalStateException e) {
            // ignore
        }
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
        FieldType.INTEGER.setValueFromDatabaseToEntity(cursor, entity, field, options);

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
        when(cursor.getColumnIndex("integerO")).thenReturn(13);
        when(cursor.isNull(13)).thenReturn(true);

        // when
        FieldType.INTEGER.setValueFromDatabaseToEntity(cursor, entity, fieldIntegerO, options);

        // then
        assertNull(entity.getIntegerO());
    }

    @Test
    public void shouldGetDefinition() throws Exception {
        Map<String, String> options = new HashMap<String, String>();
        options.put("maxValue", "500");

        // when
        String[] definition = FieldType.INTEGER.getDefinition(fieldIntegerP, true, 100L, options);

        // then
        assertEquals(1, definition.length);
        assertEquals("integerP INTEGER(3) NOT NULL DEFAULT 100", definition[0]);
    }

    @Test
    public void shouldGetDefinition2() throws Exception {
        Map<String, String> options = new HashMap<String, String>();
        options.put("maxValue", "10");

        // when
        String[] definition = FieldType.INTEGER.getDefinition(fieldIntegerP, true, null, options);

        // then
        assertEquals(1, definition.length);
        assertEquals("integerP INTEGER(2) NOT NULL", definition[0]);
    }

    @Test
    public void shouldGetDefinition3() throws Exception {
        Map<String, String> options = new HashMap<String, String>();
        options.put("maxValue", "150");

        // when
        String[] definition = FieldType.INTEGER.getDefinition(fieldShortO, false, null, options);

        // then
        assertEquals(1, definition.length);
        assertEquals("shortO INTEGER(3)", definition[0]);
    }

    @Test
    public void shouldGetDefinition4() throws Exception {
        Map<String, String> options = new HashMap<String, String>();
        options.put("maxValue", "1000");

        // when
        String[] definition = FieldType.INTEGER.getDefinition(fieldIntegerO, false, 10L, options);

        // then
        assertEquals(1, definition.length);
        assertEquals("integerO INTEGER(4) DEFAULT 10", definition[0]);
    }

    @Test
    public void shouldConvertNullDefaultValue() throws Exception {
        // when
        Map<String, String> options = new HashMap<String, String>();

        Object defaultValue = FieldType.INTEGER.getType().convertDefaultValue(fieldIntegerP, null, options);

        // then
        assertNull(defaultValue);
    }

    @Test
    public void shouldConvertDefaultValue() throws Exception {
        // when
        Map<String, String> options = new HashMap<String, String>();

        Object defaultValue = FieldType.INTEGER.getType().convertDefaultValue(fieldIntegerP, "10", options);

        // then
        assertEquals(10L, defaultValue);
    }

    @Test
    public void shouldConvertDefaultValue2() throws Exception {
        // when
        Map<String, String> options = new HashMap<String, String>();

        Object defaultValue = FieldType.INTEGER.getType().convertDefaultValue(fieldIntegerO, "-15", options);

        // then
        assertEquals(-15L, defaultValue);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldConvertDefaultValue3() throws Exception {
        // when
        Map<String, String> options = new HashMap<String, String>();

        FieldType.INTEGER.getType().convertDefaultValue(fieldIntegerO, "1a", options);
    }

    @Test
    public void shouldSetNullValueFromEntityToDatabase() throws Exception {
        Map<String, String> options = new HashMap<String, String>();

        ContentValues values = mock(ContentValues.class);
        MobeelizerErrorsHolder errors = mock(MobeelizerErrorsHolder.class);

        TestEntity entity = new TestEntity();

        // when
        FieldType.INTEGER.setValueFromEntityToDatabase(values, entity, fieldIntegerO, false, options, errors);

        // then
        verify(values).put("integerO", (Long) null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldSetNullValueFromDatabaseToMap() throws Exception {
        // given
        Map<String, String> options = new HashMap<String, String>();

        Cursor cursor = mock(Cursor.class);
        when(cursor.getColumnIndex("integerO")).thenReturn(13);
        when(cursor.isNull(13)).thenReturn(true);

        Map<String, String> map = mock(Map.class);
        // when
        FieldType.INTEGER.setValueFromDatabaseToMap(cursor, map, fieldIntegerO, options);

        // then
        verify(map).put("integerO", null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldSetNotNullValueFromDatabaseToMap() throws Exception {
        // given
        Map<String, String> options = new HashMap<String, String>();

        Cursor cursor = mock(Cursor.class);
        when(cursor.getColumnIndex("integerO")).thenReturn(13);
        when(cursor.isNull(13)).thenReturn(false);
        when(cursor.getLong(13)).thenReturn(1L);

        Map<String, String> map = mock(Map.class);

        // when
        FieldType.INTEGER.setValueFromDatabaseToMap(cursor, map, fieldIntegerO, options);

        // then
        verify(map).put("integerO", "1");
    }

    @Test
    public void shouldSetNullValueFromMapToDatabase() throws Exception {
        Map<String, String> options = new HashMap<String, String>();

        ContentValues values = mock(ContentValues.class);
        MobeelizerErrorsHolder errors = mock(MobeelizerErrorsHolder.class);

        Map<String, String> map = new HashMap<String, String>();

        // when
        FieldType.INTEGER.setValueFromMapToDatabase(values, map, fieldIntegerO, false, options, errors);

        // then
        verify(values).put("integerO", (Long) null);
    }

    @Test
    public void shouldSetNotNullValueFromMapToDatabase() throws Exception {
        Map<String, String> options = new HashMap<String, String>();

        ContentValues values = mock(ContentValues.class);
        MobeelizerErrorsHolder errors = mock(MobeelizerErrorsHolder.class);

        Map<String, String> map = new HashMap<String, String>();
        map.put("integerO", "2");

        // when
        FieldType.INTEGER.setValueFromMapToDatabase(values, map, fieldIntegerO, false, options, errors);

        // then
        verify(values).put("integerO", 2);
    }

}

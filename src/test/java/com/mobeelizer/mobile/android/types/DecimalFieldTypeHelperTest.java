// 
// DecimalFieldTypeHelperTest.java
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
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;

import com.mobeelizer.java.definition.MobeelizerErrorsHolder;
import com.mobeelizer.java.model.MobeelizerFieldAccessor;
import com.mobeelizer.java.model.MobeelizerReflectionUtil;
import com.mobeelizer.java.model.ReflectionMobeelizerFieldAccessor;
import com.mobeelizer.mobile.android.TestEntity;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ DecimalFieldTypeHelper.class, ContentValues.class, DatabaseUtils.class })
public class DecimalFieldTypeHelperTest {

    private MobeelizerFieldAccessor fieldDoubleO;

    private MobeelizerFieldAccessor fieldDoubleP;

    private MobeelizerFieldAccessor fieldFloatO;

    private MobeelizerFieldAccessor fieldFloatP;

    private MobeelizerFieldAccessor fieldBigDecimal;

    @Before
    public void init() {
        HashSet<Class<?>> types = new HashSet<Class<?>>(Arrays.asList(new Class<?>[] { Double.class, Double.TYPE, Float.class,
                Float.TYPE, BigDecimal.class }));
        fieldDoubleO = new ReflectionMobeelizerFieldAccessor(
                MobeelizerReflectionUtil.getField(TestEntity.class, "doubleO", types));
        fieldDoubleP = new ReflectionMobeelizerFieldAccessor(
                MobeelizerReflectionUtil.getField(TestEntity.class, "doubleP", types));
        fieldFloatO = new ReflectionMobeelizerFieldAccessor(MobeelizerReflectionUtil.getField(TestEntity.class, "floatO", types));
        fieldFloatP = new ReflectionMobeelizerFieldAccessor(MobeelizerReflectionUtil.getField(TestEntity.class, "floatP", types));
        fieldBigDecimal = new ReflectionMobeelizerFieldAccessor(MobeelizerReflectionUtil.getField(TestEntity.class, "bigDecimal",
                types));
    }

    @Test
    public void shouldInsertIntoEnum() throws Exception {
        // given
        Field helperField = FieldType.DECIMAL.getClass().getDeclaredField("helper");
        helperField.setAccessible(true);
        Object helper = helperField.get(FieldType.DECIMAL);

        // then
        assertTrue(helper instanceof DecimalFieldTypeHelper);
    }

    @Test
    public void shouldGetAccessibleTypes() throws Exception {
        // when
        Set<Class<?>> types = FieldType.DECIMAL.getType().getAccessibleTypes();

        // then
        assertEquals(5, types.size());
        assertTrue(types.contains(Float.class));
        assertTrue(types.contains(Float.TYPE));
        assertTrue(types.contains(Double.class));
        assertTrue(types.contains(Double.TYPE));
        assertTrue(types.contains(BigDecimal.class));
    }

    @Test
    public void shouldSetValueFromEntityToDatabase() throws Exception {
        shouldSetValueFromEntityToDatabase(fieldDoubleP, 231.21, 231.21);
        shouldSetValueFromEntityToDatabase(fieldDoubleO, Double.valueOf(-10.1), -10.1);
        shouldSetValueFromEntityToDatabase(fieldFloatO, Float.valueOf((float) 44.1), 44.1);
        shouldSetValueFromEntityToDatabase(fieldFloatP, (float) -2.1, -2.1);
        shouldSetValueFromEntityToDatabase(fieldBigDecimal, BigDecimal.valueOf(123), 123.0);
    }

    private void shouldSetValueFromEntityToDatabase(final MobeelizerFieldAccessor field, final Object entityValue,
            final Double contentValue) {
        Map<String, String> options = new HashMap<String, String>();

        ContentValues values = mock(ContentValues.class);
        MobeelizerErrorsHolder errors = mock(MobeelizerErrorsHolder.class);
        when(errors.isValid()).thenReturn(true);

        TestEntity entity = new TestEntity();

        try {
            field.set(entity, entityValue);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }

        // when
        FieldType.DECIMAL.setValueFromEntityToDatabase(values, entity, field, true, options, errors);

        // then
        verify(values).put(eq(field.getName()), doubleEq(contentValue));
    }

    @Test
    public void shouldSetErrorWhileSetNullValueFromEntityToDatabase() throws Exception {
        Map<String, String> options = new HashMap<String, String>();

        ContentValues values = mock(ContentValues.class);
        MobeelizerErrorsHolder errors = mock(MobeelizerErrorsHolder.class);

        TestEntity entity = new TestEntity();

        // when
        FieldType.DECIMAL.setValueFromEntityToDatabase(values, entity, fieldDoubleO, true, options, errors);

        // then
        verify(values, never()).put(eq("doubleO"), anyString());
        verify(errors).addFieldCanNotBeEmpty("doubleO");
    }

    @Test
    public void shouldSetValueFromEntityToDatabase2() throws Exception {
        Map<String, String> options = new HashMap<String, String>();
        options.put("maxValue", "10");
        options.put("includeMaxValue", "true");
        options.put("minValue", "10");
        options.put("includeMinValue", "true");

        ContentValues values = mock(ContentValues.class);
        MobeelizerErrorsHolder errors = mock(MobeelizerErrorsHolder.class);
        when(errors.isValid()).thenReturn(true);

        TestEntity entity = new TestEntity();
        entity.setDoubleP(10.0);

        // when
        FieldType.DECIMAL.setValueFromEntityToDatabase(values, entity, fieldDoubleP, true, options, errors);

        // then
        verify(values).put(eq("doubleP"), doubleEq(10.0));
    }

    private static Double doubleEq(final double d) {
        return Mockito.doubleThat(new BaseMatcher<Double>() {

            @Override
            public boolean matches(final Object o) {
                return (Math.abs(d - ((Double) o)) < 0.01);
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText(Double.toString(d));
            }

        });
    }

    @Test
    public void shouldSetErrorWhileSetGreaterThanMaxValueFromEntityToDatabase() throws Exception {
        Map<String, String> options = new HashMap<String, String>();
        options.put("maxValue", "10.0");
        options.put("includeMaxValue", "true");

        ContentValues values = mock(ContentValues.class);
        MobeelizerErrorsHolder errors = mock(MobeelizerErrorsHolder.class);

        TestEntity entity = new TestEntity();
        entity.setDoubleP(11.0);

        // when
        FieldType.DECIMAL.setValueFromEntityToDatabase(values, entity, fieldDoubleP, true, options, errors);

        // then
        verify(values, never()).put(eq("doubleP"), anyString());
        verify(errors).addFieldMustBeLessThanOrEqualTo("doubleP", new BigDecimal("10.0"));
    }

    @Test
    public void shouldSetErrorWhileSetGreaterThanMaxValueFromEntityToDatabase2() throws Exception {
        Map<String, String> options = new HashMap<String, String>();
        options.put("maxValue", "10");
        options.put("includeMaxValue", "false");

        ContentValues values = mock(ContentValues.class);
        MobeelizerErrorsHolder errors = mock(MobeelizerErrorsHolder.class);

        TestEntity entity = new TestEntity();
        entity.setDoubleP(10.0);

        // when
        FieldType.DECIMAL.setValueFromEntityToDatabase(values, entity, fieldDoubleP, true, options, errors);

        // then
        verify(values, never()).put(eq("doubleP"), anyString());
        verify(errors).addFieldMustBeLessThan("doubleP", BigDecimal.valueOf(10));
    }

    @Test
    public void shouldSetErrorWhileSetLowerThanMinValueFromEntityToDatabase() throws Exception {
        Map<String, String> options = new HashMap<String, String>();
        options.put("minValue", "9");
        options.put("includeMinValue", "true");

        ContentValues values = mock(ContentValues.class);
        MobeelizerErrorsHolder errors = mock(MobeelizerErrorsHolder.class);

        TestEntity entity = new TestEntity();
        entity.setDoubleP(8.1);

        // when
        FieldType.DECIMAL.setValueFromEntityToDatabase(values, entity, fieldDoubleP, true, options, errors);

        // then
        verify(values, never()).put(eq("doubleP"), anyString());
        verify(errors).addFieldMustBeGreaterThanOrEqual("doubleP", BigDecimal.valueOf(9L));
    }

    @Test
    public void shouldSetErrorWhileSetLowerThanMinValueFromEntityToDatabase2() throws Exception {
        Map<String, String> options = new HashMap<String, String>();
        options.put("minValue", "9");
        options.put("includeMinValue", "false");

        ContentValues values = mock(ContentValues.class);
        MobeelizerErrorsHolder errors = mock(MobeelizerErrorsHolder.class);

        TestEntity entity = new TestEntity();
        entity.setDoubleP(9.0);

        // when
        FieldType.DECIMAL.setValueFromEntityToDatabase(values, entity, fieldDoubleP, true, options, errors);

        // then
        verify(values, never()).put(eq("doubleP"), anyString());
        verify(errors).addFieldMustBeGreaterThan("doubleP", BigDecimal.valueOf(9L));
    }

    @Test
    public void shouldSetValueFromDatabaseToEntity() throws Exception {
        shouldSetValueFromDatabaseToEntity(fieldDoubleO, 10.0, Double.valueOf(10.0));
        shouldSetValueFromDatabaseToEntity(fieldDoubleP, -10.1, -10.1);
        shouldSetValueFromDatabaseToEntity(fieldFloatO, 4.2, Float.valueOf((float) 4.2));
        shouldSetValueFromDatabaseToEntity(fieldFloatP, -2.4, (float) -2.4);
        shouldSetValueFromDatabaseToEntity(fieldBigDecimal, 10.0, BigDecimal.valueOf(10L));
    }

    private void shouldSetValueFromDatabaseToEntity(final MobeelizerFieldAccessor field, final Double databaseValue,
            final Object entityValue) {
        // given
        Map<String, String> options = new HashMap<String, String>();

        TestEntity entity = new TestEntity();

        Cursor cursor = mock(Cursor.class);
        when(cursor.getColumnIndex(field.getName())).thenReturn(13);
        when(cursor.isNull(13)).thenReturn(false);
        when(cursor.getDouble(13)).thenReturn(databaseValue);

        // when
        FieldType.DECIMAL.setValueFromDatabaseToEntity(cursor, entity, field, options);

        // then
        try {
            Number numberEntityValue = (Number) entityValue;
            Number numberFieldValue = (Number) field.get(entity);
            assertEquals(numberEntityValue.doubleValue(), numberFieldValue.doubleValue(), 0.01);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    @Test
    public void shouldSetNullValueFromDatabaseToEntity() throws Exception {
        // given
        Map<String, String> options = new HashMap<String, String>();

        TestEntity entity = new TestEntity();

        Cursor cursor = mock(Cursor.class);
        when(cursor.getColumnIndex("doubleO")).thenReturn(13);
        when(cursor.isNull(13)).thenReturn(true);

        // when
        FieldType.DECIMAL.setValueFromDatabaseToEntity(cursor, entity, fieldDoubleO, options);

        // then
        assertNull(entity.getDoubleO());
    }

    @Test
    public void shouldGetDefinition() throws Exception {
        Map<String, String> options = new HashMap<String, String>();
        options.put("maxValue", "1000");
        options.put("scale", "2");

        // when
        String[] definition = FieldType.DECIMAL.getDefinition(fieldDoubleP, true, new BigDecimal("100.01"), options);

        // then
        assertEquals(1, definition.length);
        assertEquals("doubleP REAL(4,2) NOT NULL DEFAULT 100.01", definition[0]);
    }

    @Test
    public void shouldGetDefinition2() throws Exception {
        Map<String, String> options = new HashMap<String, String>();
        options.put("maxValue", "100");
        options.put("scale", "1");

        // when
        String[] definition = FieldType.DECIMAL.getDefinition(fieldDoubleP, true, null, options);

        // then
        assertEquals(1, definition.length);
        assertEquals("doubleP REAL(3,1) NOT NULL", definition[0]);
    }

    @Test
    public void shouldGetDefinition3() throws Exception {
        Map<String, String> options = new HashMap<String, String>();
        options.put("maxValue", "1000");
        options.put("scale", "2");

        // when
        String[] definition = FieldType.DECIMAL.getDefinition(fieldFloatO, false, null, options);

        // then
        assertEquals(1, definition.length);
        assertEquals("floatO REAL(4,2)", definition[0]);
    }

    @Test
    public void shouldGetDefinition4() throws Exception {
        Map<String, String> options = new HashMap<String, String>();
        options.put("maxValue", "1000.43");
        options.put("scale", "3");

        // when
        String[] definition = FieldType.DECIMAL.getDefinition(fieldDoubleO, false, new BigDecimal("10.2"), options);

        // then
        assertEquals(1, definition.length);
        assertEquals("doubleO REAL(4,3) DEFAULT 10.2", definition[0]);
    }

    @Test
    public void shouldConvertNullDefaultValue() throws Exception {
        // when
        Map<String, String> options = new HashMap<String, String>();

        Object defaultValue = FieldType.DECIMAL.getType().convertDefaultValue(fieldDoubleP, null, options);

        // then
        assertNull(defaultValue);
    }

    @Test
    public void shouldConvertDefaultValue() throws Exception {
        // when
        Map<String, String> options = new HashMap<String, String>();
        options.put("scale", "3");

        Object defaultValue = FieldType.DECIMAL.getType().convertDefaultValue(fieldDoubleP, "10.23", options);

        // then
        assertEquals(new BigDecimal("10.230"), defaultValue);
    }

    @Test
    public void shouldConvertDefaultValue2() throws Exception {
        // when
        Map<String, String> options = new HashMap<String, String>();
        options.put("scale", "4");

        Object defaultValue = FieldType.DECIMAL.getType().convertDefaultValue(fieldDoubleO, "-15.4", options);

        // then
        assertEquals(new BigDecimal("-15.4000"), defaultValue);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldConvertDefaultValue3() throws Exception {
        // when
        Map<String, String> options = new HashMap<String, String>();

        FieldType.DECIMAL.getType().convertDefaultValue(fieldDoubleO, "1a", options);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldConvertDefaultValue4() throws Exception {
        // when
        Map<String, String> options = new HashMap<String, String>();
        options.put("scale", "1");

        FieldType.DECIMAL.getType().convertDefaultValue(fieldDoubleO, "1.11", options);
    }

    @Test
    public void shouldSetNullValueFromEntityToDatabase() throws Exception {
        Map<String, String> options = new HashMap<String, String>();

        ContentValues values = mock(ContentValues.class);
        MobeelizerErrorsHolder errors = mock(MobeelizerErrorsHolder.class);

        TestEntity entity = new TestEntity();

        // when
        FieldType.DECIMAL.setValueFromEntityToDatabase(values, entity, fieldDoubleO, false, options, errors);

        // then
        verify(values).put("doubleO", (Double) null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldSetNullValueFromDatabaseToMap() throws Exception {
        // given
        Map<String, String> options = new HashMap<String, String>();

        Cursor cursor = mock(Cursor.class);
        when(cursor.getColumnIndex("doubleO")).thenReturn(13);
        when(cursor.isNull(13)).thenReturn(true);

        Map<String, String> map = mock(Map.class);
        // when
        FieldType.DECIMAL.setValueFromDatabaseToMap(cursor, map, fieldDoubleO, options);

        // then
        verify(map).put("doubleO", null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldSetNotNullValueFromDatabaseToMap() throws Exception {
        // given
        Map<String, String> options = new HashMap<String, String>();

        Cursor cursor = mock(Cursor.class);
        when(cursor.getColumnIndex("doubleO")).thenReturn(13);
        when(cursor.isNull(13)).thenReturn(false);
        when(cursor.getDouble(13)).thenReturn(1.1);

        Map<String, String> map = mock(Map.class);

        // when
        FieldType.DECIMAL.setValueFromDatabaseToMap(cursor, map, fieldDoubleO, options);

        // then
        verify(map).put("doubleO", "1.1");
    }

    @Test
    public void shouldSetNullValueFromMapToDatabase() throws Exception {
        Map<String, String> options = new HashMap<String, String>();

        ContentValues values = mock(ContentValues.class);
        MobeelizerErrorsHolder errors = mock(MobeelizerErrorsHolder.class);

        Map<String, String> map = new HashMap<String, String>();

        // when
        FieldType.DECIMAL.setValueFromMapToDatabase(values, map, fieldDoubleO, false, options, errors);

        // then
        verify(values).put("doubleO", (Double) null);
    }

    @Test
    public void shouldSetNotNullValueFromMapToDatabase() throws Exception {
        Map<String, String> options = new HashMap<String, String>();

        ContentValues values = mock(ContentValues.class);
        MobeelizerErrorsHolder errors = mock(MobeelizerErrorsHolder.class);

        Map<String, String> map = new HashMap<String, String>();
        map.put("doubleO", "2.2");

        // when
        FieldType.DECIMAL.setValueFromMapToDatabase(values, map, fieldDoubleO, false, options, errors);

        // then
        verify(values).put("doubleO", 2.2);
    }

}

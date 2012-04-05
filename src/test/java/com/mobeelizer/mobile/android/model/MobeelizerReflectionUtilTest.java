// 
// MobeelizerReflectionUtilTest.java
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

package com.mobeelizer.mobile.android.model;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.mobeelizer.mobile.android.TestEntity;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ MobeelizerReflectionUtil.class, Field.class, Class.class })
public class MobeelizerReflectionUtilTest {

    @Test(expected = IllegalStateException.class)
    public void shouldGetFieldFailIfFieldNotFound() throws Exception {
        // when
        MobeelizerReflectionUtil.getField(String.class, "notExistingFieldName", String.class);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldGetFieldFailIfFieldHasWrongType() throws Exception {
        // when
        MobeelizerReflectionUtil.getField(TestEntity.class, "guid", Boolean.TYPE);
    }

    @Test
    public void shouldGetField() throws Exception {
        // when
        Field field = MobeelizerReflectionUtil.getField(TestEntity.class, "guid", String.class);

        // then
        assertEquals("guid", field.getName());
        assertEquals(String.class, field.getType());
    }

    @Test
    public void shouldGetFieldUsingManyTypes() throws Exception {
        // given
        Set<Class<?>> types = new HashSet<Class<?>>();
        types.add(String.class);
        types.add(Boolean.TYPE);

        // when
        Field field = MobeelizerReflectionUtil.getField(TestEntity.class, "guid", types);

        // then
        assertEquals("guid", field.getName());
        assertEquals(String.class, field.getType());
    }

    @Test
    public void shouldGetStringValue() throws Exception {
        // given
        Field field = MobeelizerReflectionUtil.getField(TestEntity.class, "guid", String.class);

        TestEntity entity = new TestEntity();
        entity.setGuid("guid");

        // when
        Object value = MobeelizerReflectionUtil.getValue(field, entity);

        // then
        assertEquals("guid", value);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldGetStringValueWithException() throws Exception {
        // given
        Field field = PowerMockito.mock(Field.class);

        TestEntity entity = new TestEntity();
        entity.setGuid("guid");

        when(field.get(entity)).thenThrow(new IllegalAccessException());

        // when
        MobeelizerReflectionUtil.getValue(field, entity);
    }

    @Test
    public void shouldSetValue() throws Exception {
        // given
        Field field = MobeelizerReflectionUtil.getField(TestEntity.class, "owner", String.class);

        TestEntity entity = new TestEntity();

        // when
        MobeelizerReflectionUtil.setValue(field, entity, "owner");

        // then
        assertEquals("owner", entity.getOwner());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldSetValueWithException() throws Exception {
        // given
        Field field = PowerMockito.mock(Field.class);

        TestEntity entity = new TestEntity();

        PowerMockito.doThrow(new IllegalAccessException()).when(field).set(entity, "value");

        // when
        MobeelizerReflectionUtil.setValue(field, entity, "value");
    }

}

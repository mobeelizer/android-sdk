// 
// MobeelizerReflectionUtil.java
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

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Set;

public final class MobeelizerReflectionUtil {

    private MobeelizerReflectionUtil() {
    }

    public static Field getOptionalField(final Class<?> clazz, final String name, final Class<?> type) {
        boolean hasField = false;

        for (Field field : clazz.getDeclaredFields()) {
            if (name.equals(field.getName())) {
                hasField = true;
                break;
            }
        }
        if (!hasField) {
            return null;
        }
        return getField(clazz, name, type);
    }

    public static Field getField(final Class<?> clazz, final String name, final Class<?> type) {
        return getField(clazz, name, Collections.<Class<?>> singleton(type));
    }

    public static Field getField(final Class<?> clazz, final String name, final Set<Class<?>> types) {
        try {
            Field field = clazz.getDeclaredField(name);

            field.setAccessible(true);

            if (!types.contains(field.getType())) {
                throw new IllegalStateException("Field '" + name + "' of '" + clazz.getCanonicalName() + "' must be '" + types
                        + "' type.");
            }

            return field;
        } catch (SecurityException e) {
            throw new IllegalStateException("Cannot find field '" + name + "' of '" + clazz.getCanonicalName() + "'.", e);
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException("Cannot find field '" + name + "' of '" + clazz.getCanonicalName() + "'.", e);
        }
    }

    public static Object getValue(final Field field, final Object entity) {
        try {
            return field.get(entity);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public static void setValue(final Field field, final Object entity, final Object value) {
        try {
            field.set(entity, value);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

}

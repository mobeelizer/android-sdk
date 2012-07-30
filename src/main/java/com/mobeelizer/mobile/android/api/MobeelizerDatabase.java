// 
// MobeelizerDatabase.java
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

package com.mobeelizer.mobile.android.api;

import java.util.List;
import java.util.Map;

import com.mobeelizer.java.api.MobeelizerErrors;
import com.mobeelizer.java.api.MobeelizerModel;
import com.mobeelizer.mobile.android.Mobeelizer;

/**
 * Representation of the database.
 * 
 * @see Mobeelizer#getDatabase()
 * @since 1.0
 */
public interface MobeelizerDatabase {

    /**
     * Get the definition of model for the given class.
     * 
     * @param name
     *            name
     * @return definition of model
     * @since 1.0
     */
    MobeelizerModel getModel(final String name);

    /**
     * Prepare the query builder for the given class.
     * 
     * @param clazz
     *            class
     * @return criteria builder
     * @since 1.0
     */
    <T> MobeelizerCriteriaBuilder<T> find(final Class<T> clazz);

    MobeelizerCriteriaBuilder<Map<String, Object>> find(final String model);

    /**
     * Get all entities for the given class from the database.
     * 
     * @param clazz
     *            class
     * @return list of entities
     * @since 1.0
     */
    <T> List<T> list(final Class<T> clazz);

    List<Map<String, Object>> listAsMaps(final String model);

    /**
     * Delete all entities for the given class from the database.
     * 
     * @param clazz
     *            class
     * @return null if operation completed successfully, errors otherwise
     * @since 1.4
     */
    <T> MobeelizerErrors deleteAll(final Class<T> clazz);

    MobeelizerErrors deleteAll(final String model);

    /**
     * Delete the entities for the given class and guids from the database.
     * 
     * @param clazz
     *            class
     * @param guids
     *            guids
     * @return null if operation completed successfully, errors otherwise
     * @since 1.4
     */
    <T> MobeelizerErrors delete(final Class<T> clazz, final String... guids);

    MobeelizerErrors delete(final String model, final String... guids);

    /**
     * Delete the given entities from the database.
     * 
     * @param entity
     *            entity
     * @param otherEntities
     *            other entities
     * @return null if operation completed successfully, errors otherwise
     * @since 1.4
     */
    <T> MobeelizerErrors delete(final T entity, final T... otherEntities);

    MobeelizerErrors deleteMap(final Map<String, Object> entity, final Map<String, Object>... otherEntities);

    /**
     * Check whether the entity for the given class and guid exist.
     * 
     * @param clazz
     *            class
     * @param guid
     *            guid
     * @return true if exists
     * @since 1.0
     */
    <T> boolean exists(final Class<T> clazz, final String guid);

    boolean exists(final String model, final String guid);

    /**
     * Get an entity for the given class and guid. If not found return null.
     * 
     * @param clazz
     *            class
     * @param guid
     *            guid
     * @return entity or null if not exists
     * @since 1.0
     */
    <T> T get(final Class<T> clazz, final String guid);

    Map<String, Object> getAsMap(final String model, final String guid);

    /**
     * Return the count of the entities of the given class.
     * 
     * @param clazz
     *            class
     * @return count
     * @since 1.0
     */
    <T> long count(final Class<T> clazz);

    long count(final String model);

    /**
     * Save the given entity in the database and return validation errors.
     * 
     * @param entity
     *            entity
     * @return null if operation completed successfully, errors otherwise
     * @since 1.4
     */
    <T> MobeelizerErrors save(final T entity);

    MobeelizerErrors save(final Map<String, Object> entity);

}

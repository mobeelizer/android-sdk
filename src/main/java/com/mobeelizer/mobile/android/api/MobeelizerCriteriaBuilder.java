// 
// MobeelizerCriteriaBuilder.java
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

/**
 * Representation of the query builder.<br/>
 * <br/>
 * Examples:<br/>
 * 
 * <ul>
 * <li>query.list() - list all entities</li>
 * <li>query.setMaxResults(1).uniqueResult() - get first entity</li>
 * <li>query.add(MobeelizerResrictions.eq("name", "xxx")).list() - list the entities with name equals to "xxx"</li>
 * <li>query.addOrder(MobeelizerOrders.asc("name").list() - get all entities sorted ascending by name</li>
 * <li>query.add(MobeelizerResrictions.or(MobeelizerResrictions.eq("name", "xxx"), MobeelizerResrictions.eq("name",
 * "yyy"))).list() - get the entities with name equals to "xxx" or "yyy"</li>
 * </ul>
 * 
 * @since 1.0
 */
public interface MobeelizerCriteriaBuilder<T> {

    /**
     * List the entities matching to this query.
     * 
     * @return entities
     * @since 1.0
     */
    // @throws IllegalStateException if projection was set
    List<T> list();

    /**
     * Count the entities matching to this query.
     * 
     * @return count
     * @since 1.0
     */
    long count();

    // /**
    // * Finds entities using this criteria and projection.
    // *
    // * @return search result
    // */
    // Object query();

    /**
     * Find the unique entity matching to this query.
     * 
     * @return entity or null if not found
     * @throws IllegalStateException
     *             if too many entities have been matched
     * @since 1.0
     */
    // @throws IllegalStateException if projection was set
    T uniqueResult();

    /**
     * Set the max results, by default there is no limit.
     * 
     * @param maxResults
     *            max results
     * @return this query builder
     * @since 1.0
     */
    MobeelizerCriteriaBuilder<T> setMaxResults(final int maxResults);

    /**
     * Set the first result, by default the first result is equal to zero.
     * 
     * @param firstResult
     *            first result
     * @return this query builder
     * @since 1.0
     */
    MobeelizerCriteriaBuilder<T> setFirstResult(final int firstResult);

    // /**
    // * Adds projection to the criteria.
    // *
    // * @param projection
    // * projection
    // * @return this search builder
    // */
    // MobeelizerCriteriaBuilder<T> setProjection(final MobeelizerProjection projection);

    /**
     * Add restriction to the query.
     * 
     * @param criterion
     *            criterion
     * @return this query builder
     * @since 1.0
     */
    MobeelizerCriteriaBuilder<T> add(final MobeelizerCriterion criterion);

    /**
     * Add order to the query.
     * 
     * @param order
     *            order
     * @return this query builder
     * @since 1.0
     */
    MobeelizerCriteriaBuilder<T> addOrder(final MobeelizerOrder order);

    // /**
    // * Create alias for the association to the criteria.
    // *
    // * @param association
    // * association
    // * @param alias
    // * alias
    // * @return this search builder
    // */
    // MobeelizerCriteriaBuilder<T> createAlias(final String association, final String alias);

    // /**
    // * Create create for the association to the criteria.
    // *
    // * @param association
    // * association
    // * @param alias
    // * alias
    // * @return search builder for the subcriteria
    // */
    // MobeelizerCriteriaBuilder<T> createCriteria(final String association, final String alias);

}

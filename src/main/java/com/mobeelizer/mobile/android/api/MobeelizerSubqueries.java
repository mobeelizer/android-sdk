// 
// MobeelizerSubqueries.java
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

/**
 * Utility with factory methods for {@link MobeelizerCriterion} related with subqueries.
 * 
 * @since 1.0
 */
public final class MobeelizerSubqueries {

    private MobeelizerSubqueries() {
    }

    // /**
    // * Creates criterion which checks if none row exists in given subquery.
    // *
    // * @param criteria
    // * subcriteria
    // * @return criteria
    // */
    // public static MobeelizerCriterion notExists(final MobeelizerCriteriaBuilder<?> criteria) {
    // return null;
    // }
    //
    // /**
    // * Creates criterion which checks if any row exists in given subquery.
    // *
    // * @param criteria
    // * subcriteria
    // * @return criteria
    // */
    // public static MobeelizerCriterion exists(final MobeelizerCriteriaBuilder<?> criteria) {
    // return null;
    // }
    //
    // /**
    // * Creates criterion which checks if given value exists in given subquery.
    // *
    // * @param value
    // * value
    // * @param criteria
    // * subcriteria
    // * @return criteria
    // */
    // public static MobeelizerCriterion in(final Object value, final MobeelizerCriteriaBuilder<?> criteria) {
    // return null;
    // }
    //
    // /**
    // * Creates criterion which checks if given field's value exists in given subquery.
    // *
    // * @param field
    // * field
    // * @param criteria
    // * subcriteria
    // * @return criteria
    // */
    // public static MobeelizerCriterion fieldIn(final String field, final MobeelizerCriteriaBuilder<?> criteria) {
    // return null;
    // }
    //
    // /**
    // * Creates criterion which checks if given value doesn't exist in given subquery.
    // *
    // * @param value
    // * value
    // * @param criteria
    // * subcriteria
    // * @return criteria
    // */
    // public static MobeelizerCriterion notIn(final Object value, final MobeelizerCriteriaBuilder<?> criteria) {
    // return null;
    // }
    //
    // /**
    // * Creates criterion which checks if given field's value doesn't exist in given subquery.
    // *
    // * @param field
    // * field
    // * @param criteria
    // * subcriteria
    // * @return criteria
    // */
    // public static MobeelizerCriterion fieldNotIn(final String field, final MobeelizerCriteriaBuilder<?> criteria) {
    // return null;
    // }
    //
    // public static MobeelizerCriterion eq(final Object value, final MobeelizerCriteriaBuilder<?> criteria) {
    // return null;
    // }
    //
    // public static MobeelizerCriterion eqAll(final Object value, final MobeelizerCriteriaBuilder<?> criteria) {
    // return null;
    // }
    //
    // public static MobeelizerCriterion fieldEq(final String field, final MobeelizerCriteriaBuilder<?> criteria) {
    // return null;
    // }
    //
    // public static MobeelizerCriterion fieldEqAll(final String field, final MobeelizerCriteriaBuilder<?> criteria) {
    // return null;
    // }
    //
    // public static MobeelizerCriterion ge(final Object value, final MobeelizerCriteriaBuilder<?> criteria) {
    // return null;
    // }
    //
    // public static MobeelizerCriterion geAll(final Object value, final MobeelizerCriteriaBuilder<?> criteria) {
    // return null;
    // }
    //
    // public static MobeelizerCriterion geSome(final Object value, final MobeelizerCriteriaBuilder<?> criteria) {
    // return null;
    // }
    //
    // public static MobeelizerCriterion fieldGe(final String field, final MobeelizerCriteriaBuilder<?> criteria) {
    // return null;
    // }
    //
    // public static MobeelizerCriterion fieldGeAll(final String field, final MobeelizerCriteriaBuilder<?> criteria) {
    // return null;
    // }
    //
    // public static MobeelizerCriterion fieldGeSome(final String field, final MobeelizerCriteriaBuilder<?> criteria) {
    // return null;
    // }
    //
    // public static MobeelizerCriterion gt(final Object value, final MobeelizerCriteriaBuilder<?> criteria) {
    // return null;
    // }
    //
    // public static MobeelizerCriterion gtAll(final Object value, final MobeelizerCriteriaBuilder<?> criteria) {
    // return null;
    // }
    //
    // public static MobeelizerCriterion gtSome(final Object value, final MobeelizerCriteriaBuilder<?> criteria) {
    // return null;
    // }
    //
    // public static MobeelizerCriterion fieldGt(final String field, final MobeelizerCriteriaBuilder<?> criteria) {
    // return null;
    // }
    //
    // public static MobeelizerCriterion fieldGtAll(final String field, final MobeelizerCriteriaBuilder<?> criteria) {
    // return null;
    // }
    //
    // public static MobeelizerCriterion fieldGtSome(final String field, final MobeelizerCriteriaBuilder<?> criteria) {
    // return null;
    // }
    //
    // public static MobeelizerCriterion lt(final Object value, final MobeelizerCriteriaBuilder<?> criteria) {
    // return null;
    // }
    //
    // public static MobeelizerCriterion ltAll(final Object value, final MobeelizerCriteriaBuilder<?> criteria) {
    // return null;
    // }
    //
    // public static MobeelizerCriterion ltSome(final Object value, final MobeelizerCriteriaBuilder<?> criteria) {
    // return null;
    // }
    //
    // public static MobeelizerCriterion fieldLt(final String field, final MobeelizerCriteriaBuilder<?> criteria) {
    // return null;
    // }
    //
    // public static MobeelizerCriterion fieldLtAll(final String field, final MobeelizerCriteriaBuilder<?> criteria) {
    // return null;
    // }
    //
    // public static MobeelizerCriterion fieldLtSome(final String field, final MobeelizerCriteriaBuilder<?> criteria) {
    // return null;
    // }
    //
    // public static MobeelizerCriterion le(final Object value, final MobeelizerCriteriaBuilder<?> criteria) {
    // return null;
    // }
    //
    // public static MobeelizerCriterion leAll(final Object value, final MobeelizerCriteriaBuilder<?> criteria) {
    // return null;
    // }
    //
    // public static MobeelizerCriterion leSome(final Object value, final MobeelizerCriteriaBuilder<?> criteria) {
    // return null;
    // }
    //
    // public static MobeelizerCriterion fieldLe(final String field, final MobeelizerCriteriaBuilder<?> criteria) {
    // return null;
    // }
    //
    // public static MobeelizerCriterion fieldLeAll(final String field, final MobeelizerCriteriaBuilder<?> criteria) {
    // return null;
    // }
    //
    // public static MobeelizerCriterion fieldLeSome(final String field, final MobeelizerCriteriaBuilder<?> criteria) {
    // return null;
    // }
    //
    // public static MobeelizerCriterion ne(final Object value, final MobeelizerCriteriaBuilder<?> criteria) {
    // return null;
    // }
    //
    // public static MobeelizerCriterion fieldNe(final String field, final MobeelizerCriteriaBuilder<?> criteria) {
    // return null;
    // }

}

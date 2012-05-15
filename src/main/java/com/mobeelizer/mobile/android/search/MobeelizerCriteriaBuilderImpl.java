// 
// MobeelizerCriteriaBuilderImpl.java
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

package com.mobeelizer.mobile.android.search;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteQuery;
import android.util.Log;

import com.mobeelizer.mobile.android.api.MobeelizerCriteriaBuilder;
import com.mobeelizer.mobile.android.api.MobeelizerCriterion;
import com.mobeelizer.mobile.android.api.MobeelizerOrder;
import com.mobeelizer.mobile.android.model.MobeelizerAndroidModel;

public class MobeelizerCriteriaBuilderImpl<T> implements MobeelizerCriteriaBuilder<T> {

    private static final String TAG = "mobeelizer:criteriabuilderimpl";

    private final MobeelizerAndroidModel model;

    private final SQLiteDatabase database;

    private final List<MobeelizerOrder> orders = new ArrayList<MobeelizerOrder>();

    private final List<MobeelizerCriterion> criterions = new ArrayList<MobeelizerCriterion>();

    // private MobeelizerProjection projection = null;

    private int firstResult = 0;

    private int maxResults = Integer.MAX_VALUE;

    public MobeelizerCriteriaBuilderImpl(final MobeelizerAndroidModel model, final SQLiteDatabase database) {
        this.model = model;
        this.database = database;
    }

    private Cursor getCursor() {
        List<String> selectionBuilder = new ArrayList<String>();
        List<String> groupByBuilder = new ArrayList<String>();
        List<String> havingBuilder = new ArrayList<String>();
        List<String> orderBuilder = new ArrayList<String>();

        List<String> columns = new ArrayList<String>();
        List<String> selectionArgs = new ArrayList<String>();

        selectionBuilder.add(MobeelizerAndroidModel._DELETED + " = 0");

        for (MobeelizerCriterion criterion : criterions) {
            selectionBuilder.add(((MobeelizerInternalCriterion) criterion).addToQuery(selectionArgs));
        }

        for (MobeelizerOrder order : orders) {
            orderBuilder.add(((MobeelizerInternalOrder) order).addToQuery());
        }

        CursorFactory cursorFactory = new CursorFactory() {

            @Override
            public Cursor newCursor(final SQLiteDatabase db, final SQLiteCursorDriver masterQuery, final String editTable,
                    final SQLiteQuery query) {
                Log.d(TAG, query.toString());
                return new SQLiteCursor(db, masterQuery, editTable, query);
            }
        };

        return database.queryWithFactory(cursorFactory, false, model.getTableName(),
                columns.isEmpty() ? null : columns.toArray(new String[columns.size()]),
                "(" + joinList(selectionBuilder, ") and (") + ")",
                selectionArgs.isEmpty() ? null : selectionArgs.toArray(new String[selectionArgs.size()]),
                joinList(groupByBuilder, ", "), joinList(havingBuilder, ", "), joinList(orderBuilder, ", "), firstResult + ","
                        + maxResults);
    }

    static String joinList(final List<String> strings, final String delimiter) {
        if (strings.isEmpty()) {
            return null;
        }
        StringBuilder builder = new StringBuilder();

        for (String string : strings) {
            if (builder.length() > 0) {
                builder.append(delimiter);
            }
            builder.append(string);
        }

        return builder.toString();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> list() {
        // if (projection != null) {
        // throw new IllegalStateException("Cannot invoke list() with projection. Please use query() instead.");
        // }

        Cursor cursor = getCursor();

        List<T> entities = new ArrayList<T>();

        while (cursor.moveToNext()) {
            entities.add((T) model.getEntity(cursor));
        }

        cursor.close();

        return entities;
    }

    @Override
    public long count() {
        // if (projection != null) {
        // throw new IllegalStateException("Cannot invoke count() with projection. Please use query() instead.");
        // }

        Cursor cursor = getCursor();

        int count = cursor.getCount();

        cursor.close();

        return count;
    }

    // @Override
    // public Object query() {
    // if (projection == null) {
    // throw new IllegalStateException(
    // "Cannot invoke query() without projection. Please use list(), count() or uniqueResult() instead.");
    // }
    //
    // Cursor cursor = getCursor();
    //
    // // TODO V3 criteria api - different return types for projections
    //
    // List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
    //
    // while (cursor.moveToNext()) {
    // Map<String, Object> row = new HashMap<String, Object>();
    //
    // for (int i = 0; i < cursor.getColumnCount(); i++) {
    // row.put(cursor.getColumnName(i), getColumnValue(cursor, i));
    // }
    // }
    //
    // cursor.close();
    //
    // return rows;
    // }

    // private Object getColumnValue(final Cursor cursor, final int i) {
    // if (cursor.isNull(i)) {
    // return null;
    // }
    //
    // // TODO V3 criteria api - get column value for projections
    //
    // return cursor.getString(i);
    // }

    @Override
    @SuppressWarnings("unchecked")
    public T uniqueResult() {
        // if (projection != null) {
        // throw new IllegalStateException("Cannot invoke uniqueResult() with projection. Please use query() instead.");
        // }

        Cursor cursor = getCursor();

        T entity = null;

        if (cursor.moveToNext()) {
            entity = (T) model.getEntity(cursor);
        }

        boolean tooManyEntities = cursor.moveToNext();

        cursor.close();

        if (tooManyEntities) {
            throw new IllegalStateException("Query in uniqueResult() has to return single record.");
        }

        return entity;
    }

    @Override
    public MobeelizerCriteriaBuilder<T> setMaxResults(final int maxResults) {
        this.maxResults = maxResults;
        return this;
    }

    @Override
    public MobeelizerCriteriaBuilder<T> setFirstResult(final int firstResult) {
        this.firstResult = firstResult;
        return this;
    }

    // @Override
    // public MobeelizerCriteriaBuilder<T> setProjection(final MobeelizerProjection projection) {
    // this.projection = projection;
    // return this;
    // }

    @Override
    public MobeelizerCriteriaBuilder<T> add(final MobeelizerCriterion criterion) {
        if (!(criterion instanceof MobeelizerInternalCriterion)) {
            throw new IllegalStateException("Invalid restriction " + criterion.getClass().getCanonicalName());
        }
        criterions.add(criterion);
        return this;
    }

    @Override
    public MobeelizerCriteriaBuilder<T> addOrder(final MobeelizerOrder order) {
        if (!(order instanceof MobeelizerInternalOrder)) {
            throw new IllegalStateException("Invalid order " + order.getClass().getCanonicalName());
        }
        orders.add(order);
        return this;
    }

    // @Override
    // public MobeelizerCriteriaBuilder<T> createAlias(final String association, final String alias) {
    // // TODO V3 criteria api - joins
    // throw new UnsupportedOperationException("Joins are not supported yet.");
    // }
    //
    // @Override
    // public MobeelizerCriteriaBuilder<T> createCriteria(final String association, final String alias) {
    // // TODO V3 criteria api - joins
    // throw new UnsupportedOperationException("Joins are not supported yet.");
    // }

}

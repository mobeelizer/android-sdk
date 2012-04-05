// 
// MobeelizerOrders.java
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

import com.mobeelizer.mobile.android.search.MobeelizerOrderImpl;

/**
 * Utility with factory methods for {@link MobeelizerOrder}.
 * 
 * @since 1.0
 */
public final class MobeelizerOrders {

    private MobeelizerOrders() {
    }

    /**
     * Create ascending order for the given field.
     * 
     * @param field
     *            field
     * @return order
     * @since 1.0
     */
    public static MobeelizerOrder asc(final String field) {
        return new MobeelizerOrderImpl(field, true);
    }

    /**
     * Create descending order for the given field.
     * 
     * @param field
     *            field
     * @return order
     * @since 1.0
     */
    public static MobeelizerOrder desc(final String field) {
        return new MobeelizerOrderImpl(field, false);
    }

}

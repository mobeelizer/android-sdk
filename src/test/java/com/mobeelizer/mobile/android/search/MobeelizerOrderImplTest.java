// 
// MobeelizerOrderImplTest.java
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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MobeelizerOrderImplTest {

    @Test
    public void shouldAddToQuery() throws Exception {
        // given
        MobeelizerOrderImpl order = new MobeelizerOrderImpl("field", true);

        // when
        String query = order.addToQuery();

        // then
        assertEquals("field asc", query);
    }

    @Test
    public void shouldAddToQueryWithDesc() throws Exception {
        // given
        MobeelizerOrderImpl order = new MobeelizerOrderImpl("field", false);

        // when
        String query = order.addToQuery();

        // then
        assertEquals("field desc", query);
    }

}

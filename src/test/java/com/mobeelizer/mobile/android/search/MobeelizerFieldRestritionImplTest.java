// 
// MobeelizerFieldRestritionImplTest.java
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
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.mobeelizer.mobile.android.model.MobeelizerAndroidModel;

public class MobeelizerFieldRestritionImplTest {

    @Test
    public void shouldAddToQuery() throws Exception {
        // given
        MobeelizerFieldRestritionImpl restrition = new MobeelizerFieldRestritionImpl("field", "operator", "field2");
        List<String> selectionArgs = new ArrayList<String>();

        MobeelizerAndroidModel model = mock(MobeelizerAndroidModel.class);

        // when
        String query = restrition.addToQuery(selectionArgs, model);

        // then
        assertEquals(0, selectionArgs.size());
        assertEquals("field operator field2", query);
    }

}

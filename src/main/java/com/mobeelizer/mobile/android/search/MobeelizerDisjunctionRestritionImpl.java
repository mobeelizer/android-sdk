// 
// MobeelizerDisjunctionRestritionImpl.java
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

import com.mobeelizer.mobile.android.api.MobeelizerCriterion;
import com.mobeelizer.mobile.android.api.MobeelizerDisjunction;
import com.mobeelizer.mobile.android.model.MobeelizerAndroidModel;

public class MobeelizerDisjunctionRestritionImpl implements MobeelizerInternalCriterion, MobeelizerDisjunction {

    private final List<MobeelizerCriterion> criterions = new ArrayList<MobeelizerCriterion>();

    @Override
    public MobeelizerDisjunction add(final MobeelizerCriterion criterion) {
        criterions.add(criterion);
        return this;
    }

    @Override
    public String addToQuery(final List<String> selectionArgs, final MobeelizerAndroidModel model) {
        if (criterions.isEmpty()) {
            return "1 = 1";
        }

        List<String> builder = new ArrayList<String>();

        for (MobeelizerCriterion criterion : criterions) {
            builder.add(((MobeelizerInternalCriterion) criterion).addToQuery(selectionArgs, model));
        }

        return "(" + MobeelizerCriteriaBuilderImpl.joinList(builder, ") or (") + ")";
    }

}

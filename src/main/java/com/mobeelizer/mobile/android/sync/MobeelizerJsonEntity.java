// 
// MobeelizerJsonEntity.java
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

package com.mobeelizer.mobile.android.sync;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class MobeelizerJsonEntity {

    public enum ConflictState {
        NO_IN_CONFLICT, IN_CONFLICT_BECAUSE_OF_YOU, IN_CONFLICT
    }

    private Map<String, String> fields;

    private String model;

    private String guid;

    private ConflictState conflictState;

    private String owner;

    public MobeelizerJsonEntity() {
        // empty
    }

    @SuppressWarnings("unchecked")
    public MobeelizerJsonEntity(final String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        model = jsonObject.getString("model");
        guid = jsonObject.getString("guid");

        if (jsonObject.has("owner")) {
            owner = jsonObject.getString("owner");
        }

        if (jsonObject.has("conflictState")) {
            conflictState = ConflictState.valueOf(jsonObject.getString("conflictState"));
        } else {
            conflictState = ConflictState.NO_IN_CONFLICT;
        }

        if (jsonObject.has("fields")) {
            JSONObject jsonFields = jsonObject.getJSONObject("fields");
            Iterator<String> keys = jsonFields.keys();
            fields = new HashMap<String, String>();

            while (keys.hasNext()) {
                String key = keys.next();
                fields.put(key, jsonFields.isNull(key) ? null : jsonFields.getString(key));
            }
        }
    }

    public void setModel(final String model) {
        this.model = model;
    }

    public String getModel() {
        return model;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(final String guid) {
        this.guid = guid;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(final String owner) {
        this.owner = owner;
    }

    public Map<String, String> getFields() {
        return fields;
    }

    public void setFields(final Map<String, String> fields) {
        this.fields = fields;
    }

    public boolean containsValue(final String field) {
        return fields == null ? false : fields.containsKey(field);
    }

    public String getValue(final String field) {
        return fields == null ? null : fields.get(field);
    }

    public ConflictState getConflictState() {
        return conflictState;
    }

    public void setConflictState(final ConflictState conflictState) {
        this.conflictState = conflictState;
    }

    public boolean isDeleted() {
        String deleted = getValue("s_deleted");

        if (deleted == null) {
            throw new IllegalStateException("Cannot find s_deleted field in " + getGuid() + ": " + fields);
        }

        if ("true".equals(deleted)) {
            return true;
        } else if ("false".equals(deleted)) {
            return false;
        }

        throw new IllegalStateException("Illegal value '" + deleted + "' of s_deleted field in " + getGuid());
    }

    public String getJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("model", model);
        json.put("guid", guid);
        json.put("resolveConflict", "false");
        json.put("owner", owner);
        json.put("conflictState", conflictState == null ? ConflictState.NO_IN_CONFLICT : conflictState.name());

        if (fields != null) {
            JSONObject jsonFields = new JSONObject();

            for (Map.Entry<String, String> field : fields.entrySet()) {
                jsonFields.put(field.getKey(), field.getValue());
            }

            json.put("fields", jsonFields);
        }

        return json.toString();
    }

    @Override
    public String toString() {
        try {
            return getJson();
        } catch (JSONException e) {
            return "invalid json";
        }
    }

}

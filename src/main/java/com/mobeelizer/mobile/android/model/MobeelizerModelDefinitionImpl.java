// 
// MobeelizerModelDefinitionImpl.java
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

import static com.mobeelizer.mobile.android.model.MobeelizerReflectionUtil.getField;
import static com.mobeelizer.mobile.android.model.MobeelizerReflectionUtil.getOptionalField;
import static com.mobeelizer.mobile.android.model.MobeelizerReflectionUtil.getValue;
import static com.mobeelizer.mobile.android.model.MobeelizerReflectionUtil.setValue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.mobeelizer.mobile.android.MobeelizerErrorsImpl;
import com.mobeelizer.mobile.android.api.MobeelizerFieldDefinition;
import com.mobeelizer.mobile.android.api.MobeelizerModelCredentials;
import com.mobeelizer.mobile.android.api.MobeelizerModelDefinition;
import com.mobeelizer.mobile.android.definition.MobeelizerModelCredentialsDefinition;
import com.mobeelizer.mobile.android.sync.MobeelizerJsonEntity;
import com.mobeelizer.mobile.android.sync.MobeelizerJsonEntity.ConflictState;

public class MobeelizerModelDefinitionImpl implements MobeelizerModelDefinition {

    private final static String TAG = "mobeelizer:modeldefinition";

    public static final String _GUID = "_guid";

    public static final String _OWNER = "_owner";

    public static final String _DELETED = "_deleted";

    public static final String _MODIFIED = "_modified";

    public static final String _CONFLICTED = "_conflicted";

    private final Class<?> clazz;

    private final String tableName;

    private final Field guidField;

    private final Field ownerField;

    private final Field conflictedField;

    private final Field modifiedField;

    private final Field deletedField;

    private final Set<MobeelizerFieldDefinitionImpl> fields;

    private final ContentValues valuesForDelete;

    private final String name;

    private final MobeelizerModelCredentials credentials;

    public MobeelizerModelDefinitionImpl(final Class<?> clazz, final String name,
            final MobeelizerModelCredentialsDefinition credentials, final Set<MobeelizerFieldDefinitionImpl> fields) {
        this.clazz = clazz;
        this.name = name;
        this.fields = fields;
        this.credentials = new MobeelizerModelCredentialsImpl(credentials);
        tableName = clazz.getSimpleName().toLowerCase(Locale.ENGLISH);
        guidField = getField(clazz, "guid", String.class);
        ownerField = getOptionalField(clazz, "owner", String.class);
        conflictedField = getOptionalField(clazz, "conflicted", Boolean.TYPE);
        modifiedField = getOptionalField(clazz, "modified", Boolean.TYPE);
        deletedField = getOptionalField(clazz, "deleted", Boolean.TYPE);
        valuesForDelete = new ContentValues();
        valuesForDelete.put(_MODIFIED, 1);
        valuesForDelete.put(_DELETED, 1);
    }

    @Override
    public Class<?> getMappingClass() {
        return clazz;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public MobeelizerModelCredentials getCredentials() {
        return credentials;
    }

    public String getTableName() {
        return tableName;
    }

    @Override
    public Set<MobeelizerFieldDefinition> getFields() {
        return new HashSet<MobeelizerFieldDefinition>(fields);
    }

    public boolean exists(final SQLiteDatabase database, final String guid) {
        Cursor cursor = getByGuid(database, guid);

        boolean exists = cursor.moveToNext();

        cursor.close();

        return exists;
    }

    public <T> boolean exists(final SQLiteDatabase database, final T entity) {
        String guid = (String) getValue(guidField, entity);
        return guid != null && exists(database, guid);
    }

    public <T> void create(final SQLiteDatabase database, final T entity, final String owner, final MobeelizerErrorsImpl errors) {
        String guid = UUID.randomUUID().toString();

        ContentValues values = new ContentValues();

        setValue(guidField, entity, guid);

        if (ownerField != null) {
            setValue(ownerField, entity, owner);
        }

        values.put(_GUID, guid);
        values.put(_OWNER, owner);
        values.put(_CONFLICTED, Integer.valueOf(0));
        values.put(_DELETED, Integer.valueOf(0));
        values.put(_MODIFIED, Integer.valueOf(1));

        for (MobeelizerFieldDefinitionImpl field : fields) {
            field.setValueFromEntityToDatabase(values, entity, errors);
        }

        if (errors.isValid()) {
            if (modifiedField != null) {
                setValue(modifiedField, entity, true);
            }

            insertEntity(database, values);
        } else {
            setValue(guidField, entity, null);

            if (ownerField != null) {
                setValue(ownerField, entity, null);
            }
        }
    }

    public <T> void update(final SQLiteDatabase database, final T entity, final MobeelizerErrorsImpl errors) {
        String guid = (String) getValue(guidField, entity);

        ContentValues values = new ContentValues();

        for (MobeelizerFieldDefinitionImpl field : fields) {
            field.setValueFromEntityToDatabase(values, entity, errors);
        }

        values.put(_MODIFIED, Integer.valueOf(1));

        if (errors.isValid()) {
            updateEntity(database, values, guid);

            if (modifiedField != null) {
                setValue(modifiedField, entity, true);
            }
        }
    }

    public <T> T get(final SQLiteDatabase database, final String guid) {
        Cursor cursor = getByGuid(database, guid);

        T entity = null;

        if (cursor.moveToNext()) {
            entity = getEntity(cursor);
        }

        cursor.close();

        return entity;
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> list(final SQLiteDatabase database) {
        Cursor cursor = database.query(tableName, null, _DELETED + " = 0", null, null, null, null);

        List<T> entities = new ArrayList<T>();

        while (cursor.moveToNext()) {
            entities.add((T) getEntity(cursor));
        }

        cursor.close();

        return entities;
    }

    public <T> void delete(final SQLiteDatabase database, final T entity) {
        database.update(tableName, valuesForDelete, _GUID + " = ? AND " + _DELETED + " = 0",
                new String[] { (String) getValue(guidField, entity) });
    }

    public void deleteByGuid(final SQLiteDatabase database, final String guid) {
        database.update(tableName, valuesForDelete, _GUID + " = ? AND " + _DELETED + " = 0", new String[] { guid });
    }

    public void deleteAll(final SQLiteDatabase database) {
        database.update(tableName, valuesForDelete, _DELETED + " = 0", null);
    }

    public void onCreate(final SQLiteDatabase database) {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE ").append(tableName).append(" (");
        sql.append(_GUID).append(" TEXT(36) PRIMARY KEY").append(", ");
        sql.append(_OWNER).append(" TEXT(255) NOT NULL").append(", ");
        sql.append(_DELETED).append(" INTEGER(1) NOT NULL DEFAULT 0").append(", ");
        sql.append(_MODIFIED).append(" INTEGER(1) NOT NULL DEFAULT 0").append(", ");
        sql.append(_CONFLICTED).append(" INTEGER(1) NOT NULL DEFAULT 0");

        for (MobeelizerFieldDefinitionImpl field : fields) {
            for (String definition : field.getDefinition()) {
                sql.append(", ").append(definition);
            }
        }

        sql.append(");");

        database.execSQL(sql.toString());

        Log.i(TAG, sql.toString());
    }

    public void onUpgrade(final SQLiteDatabase database) {
        database.execSQL("DROP TABLE IF EXISTS " + tableName);

        Log.i(TAG, "DROP TABLE IF EXISTS " + tableName);

        onCreate(database);
    }

    public long count(final SQLiteDatabase database) {
        return DatabaseUtils.queryNumEntries(database, tableName);
    }

    private <T> Cursor getByGuid(final SQLiteDatabase database, final String guid) {
        return database.query(tableName, null, _GUID + " = ? AND " + _DELETED + " = 0", new String[] { guid }, null, null, null);
    }

    private <T> Cursor getByGuidWithDeleted(final SQLiteDatabase database, final String guid) {
        return database.query(tableName, null, _GUID + " = ?", new String[] { guid }, null, null, null);
    }

    @SuppressWarnings("unchecked")
    public <T> T getEntity(final Cursor cursor) {
        try {
            T entity = (T) clazz.newInstance();

            setValue(guidField, entity, cursor.getString(cursor.getColumnIndex(_GUID)));

            if (ownerField != null) {
                setValue(ownerField, entity, cursor.getString(cursor.getColumnIndex(_OWNER)));
            }

            if (conflictedField != null) {
                setValue(conflictedField, entity, cursor.getInt(cursor.getColumnIndex(_CONFLICTED)) == 1);
            }

            if (modifiedField != null) {
                setValue(modifiedField, entity, cursor.getInt(cursor.getColumnIndex(_MODIFIED)) != 0);
            }

            if (deletedField != null) {
                setValue(deletedField, entity, cursor.getInt(cursor.getColumnIndex(_DELETED)) == 1);
            }

            for (MobeelizerFieldDefinitionImpl field : fields) {
                field.setValueFromDatabaseToEntity(cursor, entity);
            }

            return entity;
        } catch (InstantiationException e) {
            throw new IllegalStateException(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public void lockModifiedFlag(final SQLiteDatabase database) {
        ContentValues values = new ContentValues();
        values.put(_MODIFIED, 2);
        database.update(tableName, values, _MODIFIED + " = 1", null);
    }

    public void unlockModifiedFlag(final SQLiteDatabase database) {
        ContentValues values = new ContentValues();
        values.put(_MODIFIED, 1);
        database.update(tableName, values, _MODIFIED + " = 2", null);
    }

    public void clearModifiedFlag(final SQLiteDatabase database) {
        database.delete(tableName, _DELETED + " = 1 AND " + _CONFLICTED + " = 0 AND " + _MODIFIED + " = 2", new String[0]);

        ContentValues values = new ContentValues();
        values.put(_MODIFIED, 0);
        database.update(tableName, values, _MODIFIED + " = 2", null);
    }

    public Cursor getEntitiesToSync(final SQLiteDatabase database) {
        return database.query(tableName, null, _MODIFIED + " = 2", null, null, null, null);
    }

    public MobeelizerJsonEntity getJsonEntity(final Cursor cursor) {
        MobeelizerJsonEntity entity = new MobeelizerJsonEntity();
        entity.setModel(name);
        entity.setGuid(cursor.getString(cursor.getColumnIndex(_GUID)));
        entity.setOwner(cursor.getString(cursor.getColumnIndex(_OWNER)));

        Map<String, String> values = new HashMap<String, String>();
        values.put("s_deleted", Boolean.toString(cursor.getInt(cursor.getColumnIndex(_DELETED)) == 1));

        for (MobeelizerFieldDefinitionImpl field : fields) {
            field.setValueFromDatabaseToMap(cursor, values);
        }
        entity.setFields(values);

        return entity;
    }

    public boolean updateEntityFromSync(final SQLiteDatabase database, final MobeelizerJsonEntity entity) {
        Cursor cursor = getByGuidWithDeleted(database, entity.getGuid());

        boolean exists = cursor.moveToNext();
        boolean modifiedByUser = exists && cursor.getInt(cursor.getColumnIndex(_MODIFIED)) == 1;

        cursor.close();

        if (modifiedByUser || !exists && entity.isDeleted()) {
            return true;
        }

        if (entity.getConflictState() == ConflictState.NO_IN_CONFLICT && entity.isDeleted()) {
            if (exists) {
                database.delete(tableName, _GUID + " = ?", new String[] { entity.getGuid() });
            }
            return true;
        }

        ContentValues values = new ContentValues();

        if (entity.getConflictState() == ConflictState.IN_CONFLICT_BECAUSE_OF_YOU || entity.getFields() == null) {
            values.put(_CONFLICTED, Integer.valueOf(1));
            values.put(_MODIFIED, Integer.valueOf(0));
            updateEntity(database, values, entity.getGuid());
            return true;
        } else if (entity.getConflictState() == ConflictState.IN_CONFLICT) {
            values.put(_CONFLICTED, Integer.valueOf(1));
        } else {
            values.put(_CONFLICTED, Integer.valueOf(0));
        }

        values.put(_OWNER, entity.getOwner());
        values.put(_MODIFIED, Integer.valueOf(0));
        values.put(_DELETED, Integer.valueOf(entity.isDeleted() ? 1 : 0));

        MobeelizerErrorsImpl errors = new MobeelizerErrorsImpl();

        for (MobeelizerFieldDefinitionImpl field : fields) {
            field.setValueFromMapToDatabase(values, entity.getFields(), errors);
        }

        if (!errors.isValid()) {
            return false;
        }

        if (exists) {
            updateEntity(database, values, entity.getGuid());
        } else {
            values.put(_GUID, entity.getGuid());
            insertEntity(database, values);
        }

        return true;
    }

    private void insertEntity(final SQLiteDatabase database, final ContentValues values) {
        database.insert(tableName, null, values);
    }

    private void updateEntity(final SQLiteDatabase database, final ContentValues values, final String guid) {
        database.update(tableName, values, _GUID + " = ?", new String[] { guid });
    }

    public void clearData(final SQLiteDatabase database) {
        database.delete(tableName, null, null);
    }

}

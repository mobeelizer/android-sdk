// 
// MobeelizerAndroidModel.java
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

import static com.mobeelizer.java.model.MobeelizerReflectionUtil.getValue;
import static com.mobeelizer.java.model.MobeelizerReflectionUtil.setValue;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.mobeelizer.java.api.MobeelizerCredential;
import com.mobeelizer.java.api.MobeelizerErrorsBuilder;
import com.mobeelizer.java.api.MobeelizerField;
import com.mobeelizer.java.api.MobeelizerModel;
import com.mobeelizer.java.api.MobeelizerModelCredentials;
import com.mobeelizer.java.model.MobeelizerFieldImpl;
import com.mobeelizer.java.model.MobeelizerModelImpl;
import com.mobeelizer.java.sync.MobeelizerJsonEntity;
import com.mobeelizer.java.sync.MobeelizerJsonEntity.ConflictState;

public class MobeelizerAndroidModel implements MobeelizerModel {

    private final static String TAG = "mobeelizer:modeldefinition";

    public static final String _GUID = "_guid";

    public static final String _OWNER = "_owner";

    public static final String _GROUP = "_group";

    public static final String _DELETED = "_deleted";

    public static final String _MODIFIED = "_modified";

    public static final String _CONFLICTED = "_conflicted";

    private final String tableName;

    private final Map<String, MobeelizerAndroidField> fields = new HashMap<String, MobeelizerAndroidField>();

    private final ContentValues valuesForDelete;

    private final MobeelizerModelImpl model;

    private final String currentUser;

    private final String currentGroup;

    public MobeelizerAndroidModel(final MobeelizerModelImpl model, final String currentUser, final String currentGroup) {
        this.model = model;
        this.currentUser = currentUser;
        this.currentGroup = currentGroup;

        for (MobeelizerField field : this.model.getFields()) {
            fields.put(field.getName(), new MobeelizerAndroidField((MobeelizerFieldImpl) field));
        }

        tableName = model.getName().toLowerCase(Locale.ENGLISH);
        valuesForDelete = new ContentValues();
        valuesForDelete.put(_MODIFIED, 1);
        valuesForDelete.put(_DELETED, 1);
    }

    @Override
    public Class<?> getMappingClass() {
        return model.getMappingClass();
    }

    @Override
    public String getName() {
        return model.getName();
    }

    @Override
    public MobeelizerModelCredentials getCredentials() {
        return model.getCredentials();
    }

    public String getTableName() {
        return tableName;
    }

    @Override
    public Set<MobeelizerField> getFields() {
        return model.getFields();
    }

    public String convertToDatabaseValue(final String field, final Object value) {
        if (value instanceof Boolean) {
            return (Boolean) value ? "1" : "0";
        } else if (value instanceof Date) {
            return ((Date) value).getTime() + "";
        } else {
            return value.toString();
        }
    }

    public boolean exists(final SQLiteDatabase database, final String guid) {
        Cursor cursor = getByGuid(database, guid);

        boolean exists = cursor.moveToNext();

        cursor.close();

        return exists;
    }

    public <T> boolean exists(final SQLiteDatabase database, final T entity) {
        String guid = (String) getValue(model.getGuidField(), entity);
        return guid != null && exists(database, guid);
    }

    public <T> void create(final SQLiteDatabase database, final T entity, final String owner, final String group,
            final MobeelizerErrorsBuilder builder) {

        MobeelizerCredential createCredentials = model.getCredentials().getCreateAllowed();
        if ((createCredentials == MobeelizerCredential.NONE)) {
            builder.addNoCredentialsToPerformOperationOnModel("create");
        }

        String guid = UUID.randomUUID().toString();

        ContentValues values = new ContentValues();

        setValue(model.getGuidField(), entity, guid);

        if (model.getOwnerField() != null) {
            setValue(model.getOwnerField(), entity, owner);
        }

        if (model.getGroupField() != null) {
            setValue(model.getGroupField(), entity, group);
        }

        values.put(_GUID, guid);
        values.put(_OWNER, owner);
        values.put(_GROUP, group);
        values.put(_CONFLICTED, Integer.valueOf(0));
        values.put(_DELETED, Integer.valueOf(0));
        values.put(_MODIFIED, Integer.valueOf(1));

        for (MobeelizerAndroidField field : fields.values()) {
            if (!field.hasNullOrDefaultValue(entity) && !checkCredential(field.getCredentials().getCreateAllowed(), owner, group)) {
                builder.addNoCredentialsToPerformOperationOnField(field.getName(), "create");
            }
            field.setValueFromEntityToDatabase(values, entity, builder);
        }

        if (builder.hasNoErrors()) {
            if (model.getModifiedField() != null) {
                setValue(model.getModifiedField(), entity, true);
            }

            insertEntity(database, values);
        } else {
            setValue(model.getGuidField(), entity, null);

            if (model.getOwnerField() != null) {
                setValue(model.getOwnerField(), entity, null);
            }

            if (model.getGroupField() != null) {
                setValue(model.getGroupField(), entity, null);
            }
        }
    }

    public <T> void update(final SQLiteDatabase database, final T entity, final MobeelizerErrorsBuilder builder) {

        String guid = (String) getValue(model.getGuidField(), entity);
        Cursor cursor = getByGuid(database, guid);
        if (!cursor.moveToNext()) {
            throw new IllegalStateException("Entity doesn't exists");
        }

        MobeelizerCredential updateCredentials = model.getCredentials().getUpdateAllowed();
        String owner = cursor.getString(cursor.getColumnIndex(_OWNER));
        String group = cursor.getString(cursor.getColumnIndex(_GROUP));

        if (!checkCredential(updateCredentials, owner, group)) {
            builder.addNoCredentialsToPerformOperationOnModel("update");
        }

        T oldEntity = getEntity(cursor);
        cursor.close();

        ContentValues values = new ContentValues();

        for (MobeelizerAndroidField field : fields.values()) {
            if (!field.hasSameValues(oldEntity, entity)
                    && !checkCredential(field.getCredentials().getUpdateAllowed(), owner, group)) {
                builder.addNoCredentialsToPerformOperationOnField(field.getName(), "update");
            }
            field.setValueFromEntityToDatabase(values, entity, builder);
        }

        values.put(_MODIFIED, Integer.valueOf(1));

        if (builder.hasNoErrors()) {
            updateEntity(database, values, guid);

            if (model.getModifiedField() != null) {
                setValue(model.getModifiedField(), entity, true);
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

    public Map<String, Object> getAsMap(final SQLiteDatabase database, final String guid) {
        Cursor cursor = getByGuid(database, guid);
        Map<String, Object> entity = null;
        if (cursor.moveToNext()) {
            entity = getEntityAsMap(cursor);
        }
        cursor.close();
        return entity;
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> list(final SQLiteDatabase database) {
        Cursor cursor = getListCursor(database);
        List<T> entities = new ArrayList<T>();
        while (cursor.moveToNext()) {
            T entity = (T) getEntity(cursor);
            if (entity != null) {
                entities.add(entity);
            }
        }
        cursor.close();
        return entities;
    }

    public List<Map<String, Object>> listOfMaps(final SQLiteDatabase database) {
        Cursor cursor = getListCursor(database);
        List<Map<String, Object>> entities = new ArrayList<Map<String, Object>>();
        while (cursor.moveToNext()) {
            Map<String, Object> entity = getEntityAsMap(cursor);
            if (entity != null) {
                entities.add(entity);
            }
        }
        cursor.close();
        return entities;
    }

    private Cursor getListCursor(final SQLiteDatabase database) {
        return database.query(tableName, null, _DELETED + " = 0", null, null, null, null);
    }

    public <T> void delete(final SQLiteDatabase database, final T entity, final MobeelizerErrorsBuilder builder) {
        String guid = (String) getValue(model.getGuidField(), entity);
        deleteByGuid(database, guid, builder);
    }

    public void deleteByGuid(final SQLiteDatabase database, final String guid, final MobeelizerErrorsBuilder builder) {
        Cursor cursor = getByGuid(database, guid);
        if (cursor.moveToNext()) {
            deleteFromCursor(database, cursor, builder);
        }
        cursor.close();
    }

    public void deleteAll(final SQLiteDatabase database, final MobeelizerErrorsBuilder builder) {
        if (model.getCredentials().getDeleteAllowed() == MobeelizerCredential.ALL) {
            database.update(tableName, valuesForDelete, _DELETED + " = 0", null);
        } else {
            Cursor cursor = getListCursor(database);
            while (cursor.moveToNext()) {
                deleteFromCursor(database, cursor, builder);
            }
            cursor.close();
        }
    }

    private void deleteFromCursor(final SQLiteDatabase database, final Cursor cursor, final MobeelizerErrorsBuilder builder) {
        String owner = cursor.getString(cursor.getColumnIndex(_OWNER));
        String group = cursor.getString(cursor.getColumnIndex(_GROUP));
        String guid = cursor.getString(cursor.getColumnIndex(_GUID));

        MobeelizerCredential deleteCredential = model.getCredentials().getDeleteAllowed();
        if (!checkCredential(deleteCredential, owner, group)) {
            builder.addNoCredentialsToPerformOperationOnModel("delete");
        }

        database.update(tableName, valuesForDelete, _GUID + " = ? AND " + _DELETED + " = 0", new String[] { guid });
    }

    public void onCreate(final SQLiteDatabase database) {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE ").append(tableName).append(" (");
        sql.append(_GUID).append(" TEXT(36) PRIMARY KEY").append(", ");
        sql.append(_OWNER).append(" TEXT(255) NOT NULL").append(", ");
        sql.append(_GROUP).append(" TEXT(255) NOT NULL").append(", ");
        sql.append(_DELETED).append(" INTEGER(1) NOT NULL DEFAULT 0").append(", ");
        sql.append(_MODIFIED).append(" INTEGER(1) NOT NULL DEFAULT 0").append(", ");
        sql.append(_CONFLICTED).append(" INTEGER(1) NOT NULL DEFAULT 0");

        for (MobeelizerAndroidField field : fields.values()) {
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
        Cursor cursor = database.query(tableName, new String[] { "count(*)" }, _DELETED + " = 0", null, null, null, null);
        long count = 0;
        if (cursor.moveToNext()) {
            count = cursor.getLong(0);
        }
        cursor.close();
        return count;
    }

    private <T> Cursor getByGuid(final SQLiteDatabase database, final String guid) {
        return database.query(tableName, null, _GUID + " = ? AND " + _DELETED + " = 0", new String[] { guid }, null, null, null);
    }

    private <T> Cursor getByGuidWithDeleted(final SQLiteDatabase database, final String guid) {
        return database.query(tableName, null, _GUID + " = ?", new String[] { guid }, null, null, null);
    }

    public Map<String, Object> getEntityAsMap(final Cursor cursor) {
        if (!hasReadPermission(cursor)) {
            return null;
        }
        Map<String, Object> entity = new HashMap<String, Object>();
        fillEntity(entity, cursor);
        entity.put("model", getName());
        return entity;
    }

    @SuppressWarnings("unchecked")
    public <T> T getEntity(final Cursor cursor) {
        try {
            if (!hasReadPermission(cursor)) {
                return null;
            }
            T entity = null;
            if (model.getMappingClass() == null) {
                entity = (T) new HashMap<String, Object>();
            } else {
                entity = (T) model.getMappingClass().newInstance();
            }
            fillEntity(entity, cursor);
            return entity;
        } catch (InstantiationException e) {
            throw new IllegalStateException(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private boolean hasReadPermission(final Cursor cursor) {
        String owner = cursor.getString(cursor.getColumnIndex(_OWNER));
        String group = cursor.getString(cursor.getColumnIndex(_GROUP));
        MobeelizerCredential readCredential = model.getCredentials().getReadAllowed();
        return checkCredential(readCredential, owner, group);
    }

    private boolean checkCredential(final MobeelizerCredential credentials, final String owner, final String group) {
        return credentials == MobeelizerCredential.ALL
                || (credentials == MobeelizerCredential.GROUP && group.equals(currentGroup))
                || (credentials == MobeelizerCredential.OWN && owner.equals(currentUser));
    }

    private <T> T fillEntity(final T entity, final Cursor cursor) {
        setValue(model.getGuidField(), entity, cursor.getString(cursor.getColumnIndex(_GUID)));
        String owner = cursor.getString(cursor.getColumnIndex(_OWNER));
        String group = cursor.getString(cursor.getColumnIndex(_GROUP));

        if (model.getOwnerField() != null) {
            setValue(model.getOwnerField(), entity, owner);
        }

        if (model.getGroupField() != null) {
            setValue(model.getGroupField(), entity, group);
        }

        if (model.getConflictedField() != null) {
            setValue(model.getConflictedField(), entity, cursor.getInt(cursor.getColumnIndex(_CONFLICTED)) == 1);
        }

        if (model.getModifiedField() != null) {
            setValue(model.getModifiedField(), entity, cursor.getInt(cursor.getColumnIndex(_MODIFIED)) != 0);
        }

        if (model.getDeletedField() != null) {
            setValue(model.getDeletedField(), entity, cursor.getInt(cursor.getColumnIndex(_DELETED)) == 1);
        }

        for (MobeelizerAndroidField field : fields.values()) {
            if (checkCredential(field.getCredentials().getReadAllowed(), owner, group)) {
                field.setValueFromDatabaseToEntity(cursor, entity);
            }
        }

        return entity;
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
        entity.setModel(model.getName());
        entity.setGuid(cursor.getString(cursor.getColumnIndex(_GUID)));
        entity.setOwner(cursor.getString(cursor.getColumnIndex(_OWNER)));
        entity.setGroup(cursor.getString(cursor.getColumnIndex(_GROUP)));

        Map<String, String> values = new HashMap<String, String>();
        values.put("s_deleted", Boolean.toString(cursor.getInt(cursor.getColumnIndex(_DELETED)) == 1));

        for (MobeelizerAndroidField field : fields.values()) {
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
        values.put(_GROUP, entity.getGroup());
        values.put(_MODIFIED, Integer.valueOf(0));
        values.put(_DELETED, Integer.valueOf(entity.isDeleted() ? 1 : 0));

        MobeelizerErrorsBuilder builder = new MobeelizerErrorsBuilder();

        for (MobeelizerAndroidField field : fields.values()) {
            field.setValueFromMapToDatabase(values, entity.getFields(), builder);
        }

        if (!builder.hasNoErrors()) {
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

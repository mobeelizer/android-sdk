// 
// MobeelizerApplicationDefinition.java
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

package com.mobeelizer.mobile.android.definition;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class MobeelizerApplicationDefinition implements Serializable {

    private static final long serialVersionUID = 8722163290185825711L;

    private String digest;

    private String vendor;

    private String application;

    private String conflictMode;

    private Set<MobeelizerDeviceDefinition> devices;

    private Set<MobeelizerGroupDefinition> groups;

    private Set<MobeelizerRoleDefinition> roles;

    private Set<MobeelizerModelDefinition> models;

    public String getDigest() {
        if (digest == null) {
            StringBuilder sb = new StringBuilder();

            sb.append(vendor).append("$").append(application).append("$").append(conflictMode).append("$");

            MobeelizerApplicationDefinition.digestSortJoinAndAdd(sb, devices);

            sb.append("$");

            MobeelizerApplicationDefinition.digestSortJoinAndAdd(sb, groups);

            sb.append("$");

            MobeelizerApplicationDefinition.digestSortJoinAndAdd(sb, roles);

            sb.append("$");

            MobeelizerApplicationDefinition.digestSortJoinAndAdd(sb, models);

            System.out.println(sb.toString());

            digest = encrypt(sb.toString());
        }
        return digest;
    }

    public Set<MobeelizerDeviceDefinition> getDevices() {
        return devices;
    }

    public String getVendor() {
        return vendor;
    }

    public String getApplication() {
        return application;
    }

    public Set<MobeelizerGroupDefinition> getGroups() {
        return groups;
    }

    public Set<MobeelizerModelDefinition> getModels() {
        return models;
    }

    public Set<MobeelizerRoleDefinition> getRoles() {
        return roles;
    }

    public void setApplication(final String application) {
        this.application = application;
    }

    public void setConflictMode(final String conflictMode) {
        this.conflictMode = conflictMode;
    }

    public void setDevices(final Set<MobeelizerDeviceDefinition> devices) {
        this.devices = devices;
    }

    public void setGroups(final Set<MobeelizerGroupDefinition> groups) {
        this.groups = groups;
    }

    public void setModels(final Set<MobeelizerModelDefinition> models) {
        this.models = models;
    }

    public void setRoles(final Set<MobeelizerRoleDefinition> roles) {
        this.roles = roles;
    }

    public void setVendor(final String vendor) {
        this.vendor = vendor;
    }

    public String getConflictMode() {
        return conflictMode;
    }

    public static void digestSortJoinAndAdd(final StringBuilder sb, final Collection<? extends Object> collection) {
        if (collection == null || collection.isEmpty()) {
            return;
        }

        List<String> list = new ArrayList<String>();

        Method method = null;

        for (Object object : collection) {
            try {
                if (method == null) {
                    method = object.getClass().getDeclaredMethod("getDigestString");
                    method.setAccessible(true);
                }
                list.add((String) method.invoke(object));
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException(e.getMessage(), e);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e.getMessage(), e);
            } catch (InvocationTargetException e) {
                throw new IllegalStateException(e.getMessage(), e);
            } catch (SecurityException e) {
                throw new IllegalStateException(e.getMessage(), e);
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }

        Collections.sort(list);

        boolean first = true;

        for (String string : list) {
            if (first) {
                first = false;
            } else {
                sb.append("&");
            }

            sb.append(string);
        }
    }

    private String encrypt(final String string) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(string.getBytes("UTF-8"));
            String byteArrayToHexString = byteArrayToHexString(md.digest());
            return byteArrayToHexString;
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e.getMessage(), e);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private String byteArrayToHexString(final byte[] b) {
        StringBuilder result = new StringBuilder();
        for (byte element : b) {
            result.append(Integer.toString((element & 0xff) + 0x100, 16).substring(1));
        }
        return result.toString();
    }

}

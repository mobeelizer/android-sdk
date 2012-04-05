// 
// MobeelizerDefinitionParser.java
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

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.mobeelizer.mobile.android.api.MobeelizerCredential;
import com.mobeelizer.mobile.android.types.FieldType;

public class MobeelizerDefinitionParser {

    public static MobeelizerApplicationDefinition parse(final InputStream source) {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

        try {
            SAXParser saxParser = saxParserFactory.newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();
            RadApplicationDefinitionParser radApplicationDefinitionParser = new RadApplicationDefinitionParser();
            xmlReader.setContentHandler(radApplicationDefinitionParser);
            xmlReader.parse(new InputSource(source));
            return radApplicationDefinitionParser.getRadApplicationDefinition();
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException(e.getMessage(), e);
        } catch (SAXException e) {
            throw new IllegalStateException(e.getMessage(), e);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private static class RadApplicationDefinitionParser extends DefaultHandler {

        private MobeelizerApplicationDefinition application;

        private MobeelizerModelDefinition model;

        private MobeelizerModelFieldDefinition field;

        private String option;

        public MobeelizerApplicationDefinition getRadApplicationDefinition() {
            return application;
        }

        @Override
        public void startElement(final String uri, final String localName, final String qName, final Attributes attibutes)
                throws SAXException {

            String name = localName;

            if (name == null || name.length() == 0) {
                name = qName;
            }

            if ("application".equals(name)) {
                application = new MobeelizerApplicationDefinition();
                application.setApplication(attibutes.getValue("application"));
                application.setVendor(attibutes.getValue("vendor"));
                if (attibutes.getValue("conflictMode") != null) {
                    application.setConflictMode(attibutes.getValue("conflictMode"));
                } else {
                    application.setConflictMode("MANUAL");
                }
                application.setDevices(new HashSet<MobeelizerDeviceDefinition>());
                application.setRoles(new HashSet<MobeelizerRoleDefinition>());
                application.setGroups(new HashSet<MobeelizerGroupDefinition>());
                application.setModels(new HashSet<MobeelizerModelDefinition>());
            } else if ("device".equals(name)) {
                MobeelizerDeviceDefinition device = new MobeelizerDeviceDefinition();
                device.setName(attibutes.getValue("name"));
                application.getDevices().add(device);
            } else if ("group".equals(name)) {
                MobeelizerGroupDefinition group = new MobeelizerGroupDefinition();
                group.setName(attibutes.getValue("name"));
                application.getGroups().add(group);
            } else if ("role".equals(name)) {
                MobeelizerRoleDefinition role = new MobeelizerRoleDefinition();
                role.setDevice(attibutes.getValue("device"));
                role.setGroup(attibutes.getValue("group"));
                application.getRoles().add(role);
            } else if ("model".equals(name)) {
                model = new MobeelizerModelDefinition();
                model.setName(attibutes.getValue("name"));
                model.setFields(new HashSet<MobeelizerModelFieldDefinition>());
                model.setCredentials(new HashSet<MobeelizerModelCredentialsDefinition>());
                application.getModels().add(model);
            } else if ("field".equals(name)) {
                field = new MobeelizerModelFieldDefinition();
                field.setName(attibutes.getValue("name"));
                field.setType(FieldType.valueOf(attibutes.getValue("type")));
                field.setDefaultValue(attibutes.getValue("defaultValue"));
                if (attibutes.getValue("required") != null) {
                    field.setRequired("true".equals(attibutes.getValue("required")));
                }
                field.setCredentials(new HashSet<MobeelizerModelFieldCredentialsDefinition>());
                field.setOptions(new HashMap<String, String>());
                model.getFields().add(field);
            } else if ("option".equals(name)) {
                option = attibutes.getValue("name");
            } else if ("credential".equals(name) && field != null) {
                MobeelizerModelFieldCredentialsDefinition credentials = new MobeelizerModelFieldCredentialsDefinition();
                credentials.setRole(attibutes.getValue("role"));
                credentials.setReadAllowed(MobeelizerCredential.valueOf(attibutes.getValue("readAllowed")));
                credentials.setUpdateAllowed(MobeelizerCredential.valueOf(attibutes.getValue("updateAllowed")));
                credentials.setCreateAllowed(MobeelizerCredential.valueOf(attibutes.getValue("createAllowed")));
                field.getCredentials().add(credentials);
            } else if ("credential".equals(name) && model != null) {
                MobeelizerModelCredentialsDefinition credentials = new MobeelizerModelCredentialsDefinition();
                credentials.setRole(attibutes.getValue("role"));
                credentials.setReadAllowed(MobeelizerCredential.valueOf(attibutes.getValue("readAllowed")));
                credentials.setUpdateAllowed(MobeelizerCredential.valueOf(attibutes.getValue("updateAllowed")));
                credentials.setCreateAllowed(MobeelizerCredential.valueOf(attibutes.getValue("createAllowed")));
                credentials.setDeleteAllowed(MobeelizerCredential.valueOf(attibutes.getValue("deleteAllowed")));
                if (attibutes.getValue("resolveConflictAllowed") != null) {
                    credentials.setResolveConflictAllowed("true".equals(attibutes.getValue("resolveConflictAllowed")));
                }
                model.getCredentials().add(credentials);
            }
        }

        @Override
        public void characters(final char[] chars, final int start, final int length) throws SAXException {
            if (option != null) {
                field.getOptions().put(option, new String(chars, start, length));
            }
        }

        @Override
        public void endElement(final String uri, final String localName, final String qName) throws SAXException {
            String name = localName;

            if (name == null || name.length() == 0) {
                name = qName;
            }

            if ("model".equals(name)) {
                model = null;
            } else if ("field".equals(name)) {
                field = null;
            } else if ("option".equals(name)) {
                option = null;
            }
        }

    }
}

/**
 * Copyright (C) 2015 BonitaSoft S.A.
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth
 * Floor, Boston, MA 02110-1301, USA.
 */

package org.bonitasoft.engine.page.impl;

import java.util.Collections;

import org.bonitasoft.engine.builder.BuilderFactory;
import org.bonitasoft.engine.commons.exceptions.SDeletionException;
import org.bonitasoft.engine.commons.exceptions.SObjectCreationException;
import org.bonitasoft.engine.commons.exceptions.SObjectModificationException;
import org.bonitasoft.engine.commons.exceptions.SObjectNotFoundException;
import org.bonitasoft.engine.events.model.SDeleteEvent;
import org.bonitasoft.engine.events.model.SEvent;
import org.bonitasoft.engine.events.model.SInsertEvent;
import org.bonitasoft.engine.events.model.SUpdateEvent;
import org.bonitasoft.engine.events.model.builders.SEventBuilderFactory;
import org.bonitasoft.engine.page.SPageMapping;
import org.bonitasoft.engine.page.PageMappingService;
import org.bonitasoft.engine.persistence.ReadPersistenceService;
import org.bonitasoft.engine.persistence.SBonitaReadException;
import org.bonitasoft.engine.persistence.SelectOneDescriptor;
import org.bonitasoft.engine.recorder.Recorder;
import org.bonitasoft.engine.recorder.SRecorderException;
import org.bonitasoft.engine.recorder.model.DeleteRecord;
import org.bonitasoft.engine.recorder.model.EntityUpdateDescriptor;
import org.bonitasoft.engine.recorder.model.InsertRecord;
import org.bonitasoft.engine.recorder.model.UpdateRecord;

/**
 * @author Baptiste Mesta
 */
public class PageMappingServiceImpl implements PageMappingService {

    public static final String PAGE_MAPPING = "PAGE_MAPPING";
    private Recorder recorder;
    private ReadPersistenceService persistenceService;

    public PageMappingServiceImpl(Recorder recorder, ReadPersistenceService persistenceService) {
        this.recorder = recorder;
        this.persistenceService = persistenceService;
    }

    @Override
    public SPageMapping create(String key, Long pageId) throws SObjectCreationException {
        SPageMappingImpl entity = new SPageMappingImpl();
        entity.setPageId(pageId);
        entity.setKey(key);
        return insert(entity);
    }

    SPageMapping insert(SPageMappingImpl entity) throws SObjectCreationException {
        InsertRecord record = new InsertRecord(entity);
        try {
            recorder.recordInsert(record, getInsertEvent(entity));
        } catch (SRecorderException e) {
            throw new SObjectCreationException(e);
        }
        return entity;
    }

    @Override
    public SPageMapping create(String key, String url, String urlAdapter) throws SObjectCreationException, SRecorderException {
        SPageMappingImpl entity = new SPageMappingImpl();
        entity.setUrl(url);
        entity.setUrlAdapter(urlAdapter);
        entity.setKey(key);
        return insert(entity);

    }

    SInsertEvent getInsertEvent(SPageMappingImpl entity) {
        return (SInsertEvent) BuilderFactory.get(SEventBuilderFactory.class).createInsertEvent(PAGE_MAPPING).setObject(entity).done();
    }

    @Override
    public SPageMapping get(String key) throws SObjectNotFoundException, SBonitaReadException {
        SPageMapping sPageMapping = persistenceService.selectOne(new SelectOneDescriptor<SPageMapping>("getPageMappingByKey", Collections.<String, Object>singletonMap("key", key), SPageMapping.class));
        if (sPageMapping == null) {
            throw new SObjectNotFoundException("No page mapping found with key " + key);
        }
        return sPageMapping;
    }

    @Override
    public void delete(SPageMapping sPageMapping) throws SDeletionException {
        try {
            recorder.recordDelete(new DeleteRecord(sPageMapping), getDeleteRecord(sPageMapping));
        } catch (SRecorderException e) {
            throw new SDeletionException("Unable to delete the page mapping with key " + sPageMapping.getKey(), e);
        }

    }

    SDeleteEvent getDeleteRecord(SPageMapping sPageMapping) {
        return (SDeleteEvent) BuilderFactory.get(SEventBuilderFactory.class).createDeleteEvent(PAGE_MAPPING).setObject(sPageMapping).done();
    }

    @Override
    public void update(String key, Long pageId) throws SObjectModificationException, SObjectNotFoundException, SBonitaReadException {
        final EntityUpdateDescriptor descriptor = getEntityUpdateDescriptor(pageId, null, null);
        update(key, descriptor);

    }

    @Override
    public void update(String key, String url, String urlAdapter) throws SObjectModificationException, SObjectNotFoundException, SBonitaReadException {
        final EntityUpdateDescriptor descriptor = getEntityUpdateDescriptor(null, url, urlAdapter);
        update(key, descriptor);

    }

    EntityUpdateDescriptor getEntityUpdateDescriptor(Long pageId, String url, String urlAdapter) {
        final EntityUpdateDescriptor descriptor = new EntityUpdateDescriptor();
        descriptor.addField("pageId", pageId);
        descriptor.addField("url", url);
        descriptor.addField("urlAdapter", urlAdapter);
        return descriptor;
    }

    void update(String key, EntityUpdateDescriptor descriptor) throws SObjectNotFoundException, SBonitaReadException, SObjectModificationException {
        try {
            SPageMapping sPageMapping = get(key);
            final UpdateRecord updateRecord = UpdateRecord.buildSetFields(sPageMapping, descriptor);
            recorder.recordUpdate(updateRecord, getUpdateEvent(sPageMapping));
        } catch (SRecorderException e) {
            throw new SObjectModificationException(e);
        }
    }

    SUpdateEvent getUpdateEvent(SPageMapping sPageMapping) {
        return (SUpdateEvent) BuilderFactory.get(SEventBuilderFactory.class).createUpdateEvent(PAGE_MAPPING).setObject(sPageMapping).done();
    }
}

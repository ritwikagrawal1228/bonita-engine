/*******************************************************************************
 * Copyright (C) 2014 BonitaSoft S.A.
 * BonitaSoft is a trademark of BonitaSoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * BonitaSoft, 32 rue Gustave Eiffel – 38000 Grenoble
 * or BonitaSoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 *******************************************************************************/
package com.bonitasoft.engine.api;

import java.util.List;

import org.bonitasoft.engine.api.ImportStatus;
import org.bonitasoft.engine.exception.AlreadyExistsException;
import org.bonitasoft.engine.exception.CreationException;
import org.bonitasoft.engine.exception.DeletionException;
import org.bonitasoft.engine.exception.ExecutionException;
import org.bonitasoft.engine.exception.SearchException;
import org.bonitasoft.engine.exception.UpdateException;
import org.bonitasoft.engine.search.SearchOptions;
import org.bonitasoft.engine.search.SearchResult;

import com.bonitasoft.engine.business.application.Application;
import com.bonitasoft.engine.business.application.ApplicationCreator;
import com.bonitasoft.engine.business.application.ApplicationImportPolicy;
import com.bonitasoft.engine.business.application.ApplicationMenu;
import com.bonitasoft.engine.business.application.ApplicationMenuCreator;
import com.bonitasoft.engine.business.application.ApplicationMenuNotFoundException;
import com.bonitasoft.engine.business.application.ApplicationMenuSearchDescriptor;
import com.bonitasoft.engine.business.application.ApplicationMenuUpdater;
import com.bonitasoft.engine.business.application.ApplicationNotFoundException;
import com.bonitasoft.engine.business.application.ApplicationPage;
import com.bonitasoft.engine.business.application.ApplicationPageNotFoundException;
import com.bonitasoft.engine.business.application.ApplicationPageSearchDescriptor;
import com.bonitasoft.engine.business.application.ApplicationPageUpdater;
import com.bonitasoft.engine.business.application.ApplicationSearchDescriptor;
import com.bonitasoft.engine.business.application.ApplicationUpdater;
import com.bonitasoft.engine.page.Page;

/**
 * This API allows to list and manage Bonita Living Applications ({@link Application}).
 *
 * @author Elias Ricken de Medeiros
 * @see Application
 * @since 6.4
 */
public interface ApplicationAPI {

    /**
     * Creates a new {@link Application} based on the supplied {@link ApplicationCreator}
     *
     * @param applicationCreator creator describing characteristics of application to be created
     * @return the created <code>Application</code>
     * @throws AlreadyExistsException if an application already exists with the same name
     * @throws CreationException if an error occurs during the creation
     * @see Application
     * @see ApplicationCreator
     */
    Application createApplication(ApplicationCreator applicationCreator) throws AlreadyExistsException, CreationException;

    /**
     * Retrieves an {@link Application} from its identifier.
     *
     * @param applicationId the application identifier
     * @return an <code>Application</code> from its identifier.
     * @throws ApplicationNotFoundException if no application is found for the given identifier
     * @see Application
     */
    Application getApplication(final long applicationId) throws ApplicationNotFoundException;

    /**
     * Deletes an {@link Application} by its identifier. All related {@link com.bonitasoft.engine.business.application.ApplicationPage}s and
     * {@link com.bonitasoft.engine.business.application.ApplicationMenu}s will be automatically deleted.
     *
     * @param applicationId the <code>Application</code> identifier
     * @throws DeletionException if an error occurs during the deletion
     * @see Application
     * @see com.bonitasoft.engine.business.application.ApplicationPage
     * @see com.bonitasoft.engine.business.application.ApplicationMenu
     */
    void deleteApplication(long applicationId) throws DeletionException;

    /**
     * Updates an {@link Application} based on the information supplied by the {@link ApplicationUpdater}
     *
     * @param applicationId a long representing the application identifier
     * @param updater an <code>ApplicationUpdater</code> describing the fields to be updated.
     * @return the <code>Application</code> as it is after the update.
     * @throws ApplicationNotFoundException if no <code>Application</code> is found for the given id
     * @throws AlreadyExistsException if another <code>Application</code> already exists with the new name value
     * @throws UpdateException if an error occurs during the update
     * @see Application
     * @see ApplicationUpdater
     */
    Application updateApplication(long applicationId, ApplicationUpdater updater) throws ApplicationNotFoundException, UpdateException, AlreadyExistsException;

    /**
     * Searches for {@link Application}s with specific search criteria. Use {@link ApplicationSearchDescriptor} to know the available filters.
     *
     * @param searchOptions the search criteria. See {@link SearchOptions} for details.
     * @return a {@link SearchResult} containing the number and the list of applications matching the search criteria.
     * @throws SearchException if an error occurs during search
     * @see Application
     * @see ApplicationSearchDescriptor
     * @see SearchOptions
     * @see SearchResult
     */
    SearchResult<Application> searchApplications(final SearchOptions searchOptions) throws SearchException;

    /**
     * Creates an {@link ApplicationPage}
     *
     * @param applicationId the identifier of the {@link com.bonitasoft.engine.business.application.Application} to which the
     *        {@link com.bonitasoft.engine.page.Page} will be associated
     * @param pagedId the identifier of <code>Page</code> to be associated to the <code>Application</code>
     * @param token the token that this <code>Page</code> will take in this <code>ApplicationPage</code>. The token must be unique for a given application and
     *        should contain only alpha numeric characters and the following special characters '-', '.', '_' or '~'.
     * @return the created {@link ApplicationPage}
     * @throws AlreadyExistsException if the token is already used by another <code>ApplicationPage</code> on this <code>Application</code>
     * @throws CreationException if an error occurs during the creation
     * @see ApplicationPage
     * @see Application
     * @see Page
     */
    ApplicationPage createApplicationPage(long applicationId, long pagedId, String token) throws AlreadyExistsException, CreationException;

    /**
     * Updates an {@link com.bonitasoft.engine.business.application.ApplicationPage} based on the information supplied by the
     * {@link com.bonitasoft.engine.business.application.ApplicationPageUpdater}
     *
     * @param applicationPageId the {@code ApplicationPage} identifier
     * @param updater the {@code ApplicationPageUpdater} describing the fields to be updated.
     * @return the {@code ApplicationPage} up to date
     * @throws ApplicationPageNotFoundException if no {@code ApplicationPage} is found for the given identifier
     * @throws UpdateException if an exception occurs during the update
     * @throws AlreadyExistsException if the token is updated and the new value is already used by another <code>ApplicationPage</code> on this
     *         <code>Application</code>
     */
    ApplicationPage updateApplicationPage(long applicationPageId, ApplicationPageUpdater updater) throws ApplicationPageNotFoundException, UpdateException,
            AlreadyExistsException;

    /**
     * Retrieves the {@link ApplicationPage} for the given {@code Application} token and {@code ApplicationPage} token
     *
     * @param applicationToken the <code>Application</code> name
     * @param applicationPageToken the <code>ApplicationPage</code> token
     * @return the {@link ApplicationPage} for the given {@code Application} token and {@code ApplicationPage} token
     * @throws ApplicationPageNotFoundException if no {@link ApplicationPage} is found for the given <code>Application</code> token and
     *         <code>ApplicationPage</code> token
     * @see ApplicationPage
     */
    ApplicationPage getApplicationPage(String applicationToken, String applicationPageToken) throws ApplicationPageNotFoundException;

    /**
     * Retrieves the {@link ApplicationPage} from its identifier
     *
     * @param applicationPageId the {@code ApplicationPage} identifier
     * @return the {@link ApplicationPage} from its identifier
     * @throws ApplicationPageNotFoundException if no {@link ApplicationPage} is found for the given identifier
     * @see ApplicationPage
     */
    ApplicationPage getApplicationPage(long applicationPageId) throws ApplicationPageNotFoundException;

    /**
     * Deletes an {@link ApplicationPage} by its identifier. All related {@link com.bonitasoft.engine.business.application.ApplicationMenu} will be
     * automatically deleted.
     *
     * @param applicationPageId the {@code ApplicationPage} identifier
     * @throws DeletionException if an error occurs during the deletion
     * @see ApplicationPage
     * @see com.bonitasoft.engine.business.application.ApplicationMenu
     */
    void deleteApplicationPage(long applicationPageId) throws DeletionException;

    /**
     * Searches for {@link ApplicationPage}s with specific search criteria.
     *
     * @param searchOptions the search criteria. See {@link SearchOptions} for details. Use {@link ApplicationPageSearchDescriptor} to know the available
     *        filters.
     * @return a {@link SearchResult} containing the number and the list of {@code ApplicationPageSearchDescriptor}s matching the search criteria.
     * @throws SearchException if an error occurs during the search execution
     * @see ApplicationPage
     * @see ApplicationPageSearchDescriptor
     * @see SearchOptions
     * @see SearchResult
     */
    SearchResult<ApplicationPage> searchApplicationPages(final SearchOptions searchOptions) throws SearchException;

    /**
     * Defines which {@link ApplicationPage} will represent the {@link Application} home page
     *
     * @param applicationId the {@code Application} identifier
     * @param applicationPageId the identifier of the {@code ApplicationPage} to be used as home page
     * @throws UpdateException if an error occurs during the update
     * @throws ApplicationNotFoundException if no {@code Application} is found with the given id
     * @see Application
     * @see ApplicationPage
     */
    void setApplicationHomePage(long applicationId, long applicationPageId) throws UpdateException, ApplicationNotFoundException;

    /**
     * Retrieves the {@link ApplicationPage} defined as the {@link Application} home page
     *
     * @param applicationId the {@code Application} identifier
     * @return the t{@code ApplicationPage} defined as {@code Application} home page
     * @throws ApplicationPageNotFoundException if no home page is found for the given application
     * @see Application
     * @see ApplicationPage
     */
    ApplicationPage getApplicationHomePage(long applicationId) throws ApplicationPageNotFoundException;

    /**
     * Creates a {@link ApplicationMenu} based on the supplied {@link ApplicationMenuCreator}
     *
     * @param applicationMenuCreator creator describing the characteristics of the {@code ApplicationMenu} to be created
     * @return the created {@code ApplicationMenu}
     * @throws CreationException if an error occurs during the creation
     * @see ApplicationMenu
     * @see ApplicationMenuCreator
     */
    ApplicationMenu createApplicationMenu(ApplicationMenuCreator applicationMenuCreator) throws CreationException;

    /**
     * Updates an {@link com.bonitasoft.engine.business.application.ApplicationMenu} based on the information supplied by the
     * {@link com.bonitasoft.engine.business.application.ApplicationMenuUpdater}
     * 
     * @param applicationMenuId the {@code ApplicationMenu} identifier
     * @param updater the {@code ApplicationMenuUpdater} describing the fields to be updated.
     * @return the {@code ApplicationMenu} up to date
     * @throws ApplicationMenuNotFoundException if no {@code ApplicationMenu} is found for the given identifier
     * @throws UpdateException if an exception occurs during the update
     * @see com.bonitasoft.engine.business.application.ApplicationMenu
     * @see com.bonitasoft.engine.business.application.ApplicationMenuUpdater
     */
    ApplicationMenu updateApplicationMenu(long applicationMenuId, ApplicationMenuUpdater updater) throws ApplicationMenuNotFoundException, UpdateException;

    /**
     * Retrieves the {@link ApplicationMenu} from its identifier
     *
     * @param applicationMenuId the {@code ApplicationMenu} menu identifier
     * @return the {@code ApplicationMenu} from its identifier
     * @throws ApplicationMenuNotFoundException if no {@code ApplicationMenu} is found for the given identifier
     * @see ApplicationMenu
     */
    ApplicationMenu getApplicationMenu(long applicationMenuId) throws ApplicationMenuNotFoundException;

    /**
     * Deletes an {@link ApplicationMenu} by its identifier. All children {@code ApplicationMenu} will be automatically deleted.
     *
     * @param applicationMenuId the {@code ApplicationMenu} identifier
     * @throws DeletionException if an error occurs during the deletion
     * @see ApplicationMenu
     */
    void deleteApplicationMenu(long applicationMenuId) throws DeletionException;

    /**
     * Searches for {@link ApplicationMenu}s with specific search criteria.
     *
     * @param searchOptions the search criteria. See {@link SearchOptions} for details. Use {@link ApplicationMenuSearchDescriptor} to know the available
     *        filters
     * @return a {@link SearchResult} containing the number and the list of {@code ApplicationMenu}s matching the search criteria.
     * @throws SearchException if an error occurs during search
     * @see ApplicationMenu
     * @see SearchOptions
     * @see ApplicationMenuSearchDescriptor
     * @see SearchResult
     */
    SearchResult<ApplicationMenu> searchApplicationMenus(final SearchOptions searchOptions) throws SearchException;

    /**
     * Exports the {@link com.bonitasoft.engine.business.application.Application}s which identifier is in {@code applicationIds}
     * @param applicationIds the identifiers of {@code Application}s to be exported
     * @return a byte array representing the content of XML file containing the exported {@code Application}s
     * @throws ExecutionException if an exception occurs during the export.
     * @see com.bonitasoft.engine.business.application.Application
     */
    byte[] exportApplications(long... applicationIds) throws ExecutionException;

    /**
     * Imports {@link com.bonitasoft.engine.business.application.Application}s based on a XML file content
     * @param xmlContent a byte array representing the content of XML file containing the applications to be imported.
     * @param policy the {@link com.bonitasoft.engine.business.application.ApplicationImportPolicy} used to execute the import
     * @return a {@link java.util.List} of {@link org.bonitasoft.engine.api.ImportStatus} representing the {@code ImportStatus} for each imported {@code Application}
     * @throws ExecutionException if an error occurs during the import
     * @see com.bonitasoft.engine.business.application.Application
     * @see com.bonitasoft.engine.business.application.ApplicationImportPolicy
     * @see org.bonitasoft.engine.api.ImportStatus
     */
    List<ImportStatus> importApplications(final byte[] xmlContent, final ApplicationImportPolicy policy) throws ExecutionException;

}

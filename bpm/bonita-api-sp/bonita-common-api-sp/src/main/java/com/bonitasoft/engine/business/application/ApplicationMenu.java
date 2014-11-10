/*******************************************************************************
 * Copyright (C) 2014 BonitaSoft S.A.
 * BonitaSoft is a trademark of BonitaSoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * BonitaSoft, 32 rue Gustave Eiffel – 38000 Grenoble
 * or BonitaSoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 *******************************************************************************/
package com.bonitasoft.engine.business.application;

import org.bonitasoft.engine.bpm.BaseElement;


/**
 * @author Elias Ricken de Medeiros
 *
 */
public interface ApplicationMenu extends BaseElement {

    String getDisplayName();

    long getApplicationPageId();

    /**
     * Retrieves the identifier of the parent menu. If the menu does not have a parent menu, this method will return null.
     *
     * @return the identifier of the parent menu or null if the menu has no parent.
     */
    Long getParentId();

    int getIndex();

}
/**
 * Copyright (C) 2019 Bonitasoft S.A.
 * Bonitasoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth
 * Floor, Boston, MA 02110-1301, USA.
 **/
package org.bonitasoft.engine.bpm.flownode.impl.internal;

import org.bonitasoft.engine.bpm.flownode.FlowNodeType;
import org.bonitasoft.engine.bpm.flownode.UserTaskInstance;

/**
 * @author Matthieu Chaffotte
 * @author Celine Souchet
 */
public class UserTaskInstanceImpl extends HumanTaskInstanceImpl implements UserTaskInstance {

    private static final long serialVersionUID = -1791657324743303807L;

    /**
     * @param name
     * @param flownodeDefinitionId
     * @param actorId
     */
    public UserTaskInstanceImpl(final String name, final long flownodeDefinitionId, final long actorId) {
        super(name, flownodeDefinitionId, actorId);
    }

    @Override
    public FlowNodeType getType() {
        return FlowNodeType.USER_TASK;
    }

}

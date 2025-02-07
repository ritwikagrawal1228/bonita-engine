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
package org.bonitasoft.engine.theme.exception;

import org.bonitasoft.engine.exception.NotFoundException;

/**
 * @author Celine Souchet
 * @deprecated since 7.13.0, ThemeAPI does nothing. There is no replacement, as it used to serve old removed feature.
 */
@Deprecated(since = "7.13.0")
public class ThemeNotFoundException extends NotFoundException {

    private static final long serialVersionUID = 1692118270602416856L;

    public ThemeNotFoundException(final Throwable cause) {
        super(cause);
    }

}

/*
 * BriarJar -- a GUI and TUI prototype for the messenger Briar.
 * Copyright (C) 2022 BriarJar Project Team
 *
 * This file is part of BriarJar.
 *
 * BriarJar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * BriarJar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with BriarJar.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.briarjar.briarjar.utils;

import java.util.Objects;


/**
 * Simple reference/variable checker (validator)
 *
 * @since 1.00
 */
public interface Checker {

	/**
	 * Checks if a passed object reference which should not be null is actually
	 * not null or otherwise throw an
	 * {@link IllegalArgumentException}. The
	 * Exception type differs from {@link Objects#requireNonNull}'s
	 * {@link NullPointerException}.
	 * <p>
	 * <b>Note</b>: Since it's an unchecked exception, an IDE will not complain
	 * about a missing optional catch block!
	 *
	 * @param name  a {@code string}, to be included in the detail message
	 *
	 * @param object  an {@link Object object} to be checked for {@code null}
	 *
	 * @param <T>  reference type
	 *
	 * @return  the checked {@code object} if it's not null
	 *
	 * @throws IllegalArgumentException if {@code object} is null
	 */
	static <T> T throwOnNullParam(String name, T object) throws IllegalArgumentException
	{
		if ( object == null )
			throw new IllegalArgumentException( name+" can not be null" );
		return object;
	}
}

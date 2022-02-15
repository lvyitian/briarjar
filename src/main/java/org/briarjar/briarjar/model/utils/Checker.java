package org.briarjar.briarjar.model.utils;

import java.util.Objects;


/**
 * Simple reference/variable checker (validator)
 *
 *<p></p> //TODO @version 1.0, 2021-mm-dd hh:mm
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

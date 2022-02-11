package org.briarjar.briarjar.model.utils;


/**
 * Simple reference/variable checker (validator)
 *
 *<p></p> //TODO @version 1.0, 2021-mm-dd hh:mm
 */
public interface Checker {

	/**
	 * Checks if a passed parameter <b>from a previous method caller</b> which
	 * should not be null is actually not null. Otherwise the method caller
	 * should be notified about the illegal behaviour / bug.
	 * <p>
	 * But its up to the user of this method to notify the call causer, since
	 * the thrown {@link IllegalArgumentException} extends the
	 * {@link RuntimeException} and is therefore an unchecked exception, too.
	 * <b>Note</b>: an IDE will likely not remind to catch an unchecked
	 * exception.
	 *
	 * @param name  a {@code string}, to be included in the detail message
	 *
	 * @param o     an {@link Object object} to be checked for {@code null}
	 *
	 * @throws IllegalArgumentException if {@code o} is null
	 */
	static void throwOnNullParam(String name, Object o) throws IllegalArgumentException
	{
		if ( o == null )
			throw new IllegalArgumentException( name+" can not be null" );
	}
}

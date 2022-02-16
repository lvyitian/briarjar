package org.briarjar.briarjar.model.exceptions;


/**
 * Used to throw a unified {@link Exception} with appropriate information
 * included to be directly display-able by UI pop-up windows.
 * <p>
 * If possible or existent, a caught exception should always be included via
 * constructor.
 * <p>
 * Additionally, a title can be specified which is indented to be used as window
 * title by a UI.
 * <p>
 * To make the messages more meaningful, the booleans {@link #mentionCause} and
 * {@code preferClassSimpleNameAsMsg} exist in some constructors too. That is
 * also where they get explained in more detail.
 * <p>
 * //TODO @version 1.0, 2021-mm-dd hh:mm
 */
public class GeneralException extends Exception {

	private String title = "";
	private boolean mentionCause;


	/**
	 * Specifies just a message, should only be used when no
	 * {@link Throwable throwable} cause exists or should be included.
	 *
	 * @param message  a {@link String string}, not null
	 *
	 * @since 1.0
	 */
	public GeneralException(String message)
	{
		super(message);
	}


	/**
	 * Specifies a message and title, should only be used when no
	 * {@link Throwable throwable} cause exists or should be included.
	 *
	 * @param message  a {@link String string}, not null
	 * @param title    a {@link String string}, not null
	 *
	 * @since 1.0
	 */
	public GeneralException(String message, String title)
	{
		super(message);
		this.title = title;
	}


	/**
	 * Includes a {@link Throwable throwable} cause for further information and
	 * specifies a message.
	 *
	 * @param message  a {@link String string}, not null
	 * @param cause    a {@link Throwable throwable} to be included, not null
	 *
	 * @since 1.0
	 */
	public GeneralException(String message, Throwable cause)
	{
		super(message, cause);
	}


	/**
	 * Includes a {@link Throwable throwable} cause for further information,
	 * specifies a message (but no title) and whether the cause's simple class
	 * name should be appended within brackets to the message or not - that is
	 * useful when not expecting a specific / single cause to be thrown.
	 *
	 * @param message  a {@link String string}, not null
	 * @param cause    a {@link Throwable throwable} to be included, not null
	 * @param mentionCause  a {@link Boolean boolean}, true for mentioning the
	 *                      cause
	 *
	 * @since 1.0
	 */
	public GeneralException(String message, Throwable cause, boolean mentionCause)
	{
		this(message, null, cause, mentionCause);
	}


	/**
	 * Includes a {@link Throwable throwable} cause for further information,
	 * specifies a message, title and whether the cause's simple class name
	 * should be appended within brackets to the message or not - that is useful
	 * when not expecting a specific / single cause to be thrown.
	 *
	 * @param message  a {@link String string}, not null
	 * @param title    a {@link String string}, not null
	 * @param cause    a {@link Throwable throwable} to be included, not null
	 * @param mentionCause  a {@link Boolean boolean}, true for mentioning the
	 *                      cause
	 * @since 1.0
	 */
	public GeneralException(String message, String title, Throwable cause, boolean mentionCause)
	{
		super(message, cause);
		this.mentionCause = mentionCause;
		if ( title != null )
			this.title = title;
	}


	/**
	 * Includes only a {@link Throwable throwable} cause and inherits its
	 * message unconditionally if preferClassSimpleNameAsMsg is set to false. If
	 * set to true, the message will be set to the cause's simple class name if
	 * either the cause's message is null, blank or just the full class name.
	 *
	 * @param cause  a {@link Throwable throwable} to be included, not null
	 * @param preferClassSimpleNameAsMsg  a {@link Boolean boolean}, true if it
	 *                     should be tried to make the message more informative
	 *
	 * @since 1.0
	 */
	public GeneralException(Throwable cause, boolean preferClassSimpleNameAsMsg)
	{
		this(cause, preferClassSimpleNameAsMsg, null);
	}


	/**
	 * Specifies a title, includes a {@link Throwable throwable} cause and
	 * inherits its message unconditionally if preferClassSimpleNameAsMsg is set
	 * to false. If set to true, the message will be set to the cause's simple
	 * class name if either the cause's message is null, blank or just the full
	 * class name.
	 *
	 * @param cause  a {@link Throwable throwable} to be included, not null
	 * @param preferClassSimpleNameAsMsg  a {@link Boolean boolean}, true if it
	 *                                    should be tried to make the message
	 *                                    more informative
	 * @param title  a {@link String string}, not null
	 *
	 * @since 1.0
	 */
	public GeneralException( Throwable cause,
	                         boolean preferClassSimpleNameAsMsg,
	                         String title )
	{
		super(  ( !preferClassSimpleNameAsMsg ? cause.getMessage() :

	                   /* If the message is useless ... */
		               cause.getMessage() == null ||
		               cause.getMessage().equals(cause.getClass().toString()) ||
		               cause.getMessage().isBlank()

		               /* ... use cause's SimpleName, otherwise don't touch */
		               ? cause.getClass().getSimpleName() : cause.getMessage()

				) , cause
		);

		if ( title != null && !title.isBlank() )
			this.title = title;
	}


	/**
	 * Returns the cause's simple name if existent or otherwise "null".
	 *
	 * @return  a {@link String string}
	 *
	 * @since 1.0
	 */
	public String getCauseSimpleName()
	{
		return getCause().getClass().getSimpleName();
	}


	/**
	 * Returns the message either with or without mentioning the cause appended
	 * depending on set {@link #mentionCause}.
	 *
	 * @return a {@link String string}
	 *
	 * @since 1.0
	 */
	@Override
	public String getMessage()
	{
		if ( mentionCause )
			return super.getMessage()+" ("+getCauseSimpleName()+")";
		else
			return super.getMessage();
	}


	/**
	 * Returns the title if existent or otherwise a blank {@link String string}
	 *
	 * @return a {@link String string}
	 *
	 * @since 1.0
	 */
	public String getTitle()
	{
		return title;
	}


	/**
	 * Returns a {@link String string} separated by ";", consisting of:
	 * <ul>
	 * <li>the word "GeneralException"
	 * <li>the title or "(no title)"
	 * <li>the message or "(no message)"
	 * <li>"Inherited cause: " and the cause or "(none)"
	 * </ul>
	 * @return  a {@link String string}
	 *
	 * @since 1.0
	 */
	@Override
	public String toString()
	{
		return  getClass().getSimpleName()                           +"; "+
		       (title        != null ? title        : "(no title)"  )+"; "+
		       (getMessage() != null ? getMessage() : "(no message)")+"; "+
			   "Inherited cause: "+
		       (getCause()   != null ? getCause()   : "(none)"      );
	}


}

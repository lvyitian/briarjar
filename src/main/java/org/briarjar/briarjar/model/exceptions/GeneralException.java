package org.briarjar.briarjar.model.exceptions;


public class GeneralException extends Exception {

	private String title = "";
	private boolean mentionCause;



	public GeneralException(String message)
	{
		super(message);
	}


	public GeneralException(String message, String title)
	{
		super(message);
		this.title = title;
	}


	public GeneralException(String message, Throwable cause)
	{
		super(message, cause);
	}


	public GeneralException(String message, Throwable cause, boolean mentionCause)
	{
		this(message, null, cause, mentionCause);
	}


	public GeneralException(String message, String title, Throwable cause, boolean mentionCause)
	{
		super(message, cause);
		this.mentionCause = mentionCause;
		if ( title != null )
			this.title = title;
	}


	public GeneralException(Throwable cause, boolean simpleNameAsMsg)
	{
		this(cause, simpleNameAsMsg, null);
	}


	public GeneralException(Throwable cause, boolean simpleNameAsMsg, String title)
	{
		super(  ( !simpleNameAsMsg ? cause.getMessage() :

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








	public String getCauseSimpleName()
	{
		return getCause().getClass().getSimpleName();
	}


	@Override
	public String getMessage()
	{
		if ( mentionCause )
			return super.getMessage()+" ("+getCauseSimpleName()+")";
		else
			return super.getMessage();
	}


	public String getTitle()
	{
		return title;
	}


	@Override
	public String toString()
	{
		return  getClass().getSimpleName()                           +"; "+
		       (title        != null ? title        : "(no title)"  )+"; "+
		       (getMessage() != null ? getMessage() : "(no message)")+"; "+
			   "Preceding Cause: "+
		       (getCause()   != null ? getCause()   : "(none)"      );
	}


}

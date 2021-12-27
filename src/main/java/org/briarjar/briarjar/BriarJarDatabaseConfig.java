package org.briarjar.briarjar;

import org.briarproject.bramble.api.crypto.KeyStrengthener;
import org.briarproject.bramble.api.db.DatabaseConfig;
import org.briarproject.bramble.api.nullsafety.NotNullByDefault;

import java.io.File;

import javax.annotation.Nullable;

@NotNullByDefault
public class BriarJarDatabaseConfig implements DatabaseConfig {

	private final File dbDir;
	private final File keyDir;

	public BriarJarDatabaseConfig(File dbDir, File keyDir)
	{
		this.dbDir = dbDir;
		this.keyDir = keyDir;
	}

	@Override
	public File getDatabaseDirectory()
	{
		return dbDir;
	}

	@Override
	public File getDatabaseKeyDirectory()
	{
		return keyDir;
	}

	@Override
	@Nullable
	public KeyStrengthener getKeyStrengthener()
	{
		return null;
	}


}

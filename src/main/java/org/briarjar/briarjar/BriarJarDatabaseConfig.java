package org.briarjar.briarjar;

import org.briarproject.bramble.api.crypto.KeyStrengthener;
import org.briarproject.bramble.api.db.DatabaseConfig;

import java.io.File;

import javax.annotation.Nullable;

public class BriarJarDatabaseConfig implements DatabaseConfig
{
	private File dbDir;
	private File keyDir;

	public BriarJarDatabaseConfig(File dbDir, File keyDir)
	{
		this.dbDir = dbDir;
		this.keyDir = keyDir;
	}

	@Override
	public File getDatabaseDirectory() {
		return dbDir;
	}

	@Override
	public File getDatabaseKeyDirectory() {
		return keyDir;
	}

	@Override
	@Nullable
	public KeyStrengthener getKeyStrengthener() {
		return null;
	}


}

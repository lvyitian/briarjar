/*
 * BriarJar -- a GUI and TUI prototype for the messenger Briar.
 * Copyright (C) 2022 BriarJar Project Team
 * Copyright (C) 2021 The Briar Project
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

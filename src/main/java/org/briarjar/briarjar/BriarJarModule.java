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

import org.briarproject.bramble.account.AccountModule;
import org.briarproject.bramble.api.FeatureFlags;
import org.briarproject.bramble.api.db.DatabaseConfig;
import org.briarproject.bramble.api.nullsafety.NotNullByDefault;
import org.briarproject.bramble.api.plugin.PluginConfig;
import org.briarproject.bramble.api.plugin.TorDirectory;
import org.briarproject.bramble.api.plugin.TransportId;
import org.briarproject.bramble.api.plugin.duplex.DuplexPluginFactory;
import org.briarproject.bramble.api.plugin.simplex.SimplexPluginFactory;
import org.briarproject.bramble.battery.DefaultBatteryManagerModule;
import org.briarproject.bramble.event.DefaultEventExecutorModule;
import org.briarproject.bramble.network.JavaNetworkModule;
import org.briarproject.bramble.plugin.tor.CircumventionModule;
import org.briarproject.bramble.plugin.tor.UnixTorPluginFactory;
import org.briarproject.bramble.socks.SocksModule;
import org.briarproject.bramble.system.*;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
		includes = {
				AccountModule.class,
				CircumventionModule.class,
				ClockModule.class,
				DefaultBatteryManagerModule.class,
				DefaultEventExecutorModule.class,
				DefaultTaskSchedulerModule.class,
				DefaultWakefulIoExecutorModule.class,
				DesktopSecureRandomModule.class,
				JavaNetworkModule.class,
				JavaSystemModule.class,
				SocksModule.class
		})
public class BriarJarModule {

	private final File appDir = new File(System.getProperty("user.home") + "/.briar");

	@Provides
	@Singleton
	@TorDirectory
	public File provideTorDirectory()
	{
		return new File(appDir, "tor");
	}


	@NotNullByDefault
	@Provides
	public PluginConfig providePluginConfig(UnixTorPluginFactory tor)
	{
		List<DuplexPluginFactory> duplex = List.of(tor);
		return new PluginConfig() {

			@Override
			public Collection<DuplexPluginFactory> getDuplexFactories()
			{
				return duplex;
			}

			@Override
			public Collection<SimplexPluginFactory> getSimplexFactories()
			{
				return Collections.emptyList();
			}

			@Override
			public boolean shouldPoll()
			{
				return true;
			}

			@Override
			public Map<TransportId, List<TransportId>> getTransportPreferences()
			{
				return Collections.emptyMap();
			}
		};
	}


	@Provides
	@Singleton
	public DatabaseConfig provideDatabaseConfig()
	{
		File dbDir = new File(appDir, "db");
		File keyDir = new File(appDir, "key");
		return new BriarJarDatabaseConfig(dbDir, keyDir);
	}


	@Provides
	public FeatureFlags provideFeatureFlags() {
		return new FeatureFlags() {

			@Override
			public boolean shouldEnableImageAttachments() {
				return false;
			}

			@Override
			public boolean shouldEnableProfilePictures() {
				return false;
			}

			@Override
			public boolean shouldEnableDisappearingMessages() {
				return false;
			}

			@Override
			public boolean shouldEnableConnectViaBluetooth() {
				return false;
			}

			@Override
			public boolean shouldEnableTransferData() {
				return false;
			}
		};
	}
}

package org.briarjar.briarjar;

import org.briarproject.bramble.account.AccountModule;
import org.briarproject.bramble.api.FeatureFlags;
import org.briarproject.bramble.api.db.DatabaseConfig;
import org.briarproject.bramble.api.event.EventListener;
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


	// TODO provideObjectMapper ?!

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

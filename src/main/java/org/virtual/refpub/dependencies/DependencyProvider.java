package org.virtual.refpub.dependencies;

import java.io.InputStream;
import java.util.Properties;

import javax.inject.Singleton;

import org.sdmx.SdmxServiceFactory;
import org.sdmxsource.sdmx.api.manager.output.StructureWriterManager;
import org.virtual.refpub.Configuration;
import org.virtual.refpub.RefPubPlugin;

import dagger.Module;
import dagger.Provides;

/**
 * Dependencies provided to Dagger for injection.
 */
@Module(injects = RefPubPlugin.class )
public class DependencyProvider {
	public static final String PROPERTIES_PATH = "/refpub.properties";

	@Provides public @Singleton Configuration configuration() {
		try(InputStream stream = getClass().getResourceAsStream(PROPERTIES_PATH)) {
			if(stream == null)
				throw new IllegalStateException("missing configuration: configuration resource " + PROPERTIES_PATH + " not on classpath");
	
			Properties properties = new Properties();
	
			try {
				properties.load(stream);
			} catch (Exception e) {
				throw new IllegalStateException("cannot read configuration resources at " + PROPERTIES_PATH);
			}
	
			return new Configuration(properties);
		} catch(Throwable t) {
			throw new RuntimeException("Unable to read configuration from " + PROPERTIES_PATH, t);
		}
	}
	
	@Provides public @Singleton StructureWriterManager writer() {
		return SdmxServiceFactory.writer();
	}
}

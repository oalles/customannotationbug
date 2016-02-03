package com.customannotationbug;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import com.customannotationbug.annotation.EventRoot;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;

@Configuration
public class AppConfig extends AbstractMongoConfiguration {

	public static final String COLLECTION_NAME = "annotation-tests";

	@Override
	protected String getDatabaseName() {
		return COLLECTION_NAME;
	}

	@Override
	public Mongo mongo() throws Exception {
		MongoClientOptions options = MongoClientOptions.builder().readPreference(ReadPreference.primary())
				.writeConcern(WriteConcern.JOURNALED).connectionsPerHost(10).build();
		return new MongoClient(Arrays.asList(new ServerAddress("localhost")), options);
	}

	public String getMappingBasePackage() {
		return "com.customannotationbug";
	}

	public Set<Class<?>> getInitialEntitySet() throws ClassNotFoundException {

		String basePackage = getMappingBasePackage();
		Set<Class<?>> initialEntitySet = new HashSet<Class<?>>();

		if (StringUtils.hasText(basePackage)) {
			
			ClassPathScanningCandidateComponentProvider componentProvider = new ClassPathScanningCandidateComponentProvider(
					false);
			
			componentProvider.addIncludeFilter(new AnnotationTypeFilter(EventRoot.class));

			for (BeanDefinition candidate : componentProvider.findCandidateComponents(basePackage)) {
				initialEntitySet.add(ClassUtils.forName(candidate.getBeanClassName(),
						AbstractMongoConfiguration.class.getClassLoader()));
			}
		}

		return initialEntitySet;
	}

}

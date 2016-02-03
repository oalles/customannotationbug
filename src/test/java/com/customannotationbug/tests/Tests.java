package com.customannotationbug.tests;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.util.Assert;

import com.customannotationbug.AppConfig;
import com.customannotationbug.entities.UserCreatedEvent;
import com.customannotationbug.entities.UserEvent;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppConfig.class }, loader = AnnotationConfigContextLoader.class)
public class Tests extends AbstractJUnit4SpringContextTests {

	@Autowired
	private MongoMappingContext mongoMappingContext;

	@Test
	public void testCustomAnnotatedEntityWithCollectionNameOnDocumentViaAliasForAnnotation() throws Exception {

		String parentCollectionName = mongoMappingContext.getPersistentEntity(UserEvent.class).getCollection();
		String concreteCollectionName = mongoMappingContext.getPersistentEntity(UserCreatedEvent.class).getCollection();
		Assert.isTrue(parentCollectionName.equals(concreteCollectionName)
				&& parentCollectionName.equals(AppConfig.COLLECTION_NAME));
	}

}

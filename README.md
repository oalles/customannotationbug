### DESCRIPTION

**1. Custom annotation to mark an event hierarchy:**
[EventRoot](https://github.com/oalles/spring-data-custom-annotation-bug/blob/master/src/main/java/com/customannotationbug/annotation/EventRoot.java)
Notice that it is not mark as @Inherited. Remember that @Document is mark as @Inherited (yet?). 
Notice that @EventRoot attributes are mark as "AliasFor" attributes for @Document annotation attributes. 

So i would expect that using AnnotationUtils finders on @EventRoot annotated entities with a collection attribute set to a given value, could let me get a @Document annotation with its collection attribute set to the same value via a proxy. 
See that, that is what BasicMongoPersistentEntity constructor does. It is provided with a candidate entity, and try to find its @Document attributes. 

**2. Event hierarchy:**
[UserEvent](https://github.com/oalles/spring-data-custom-annotation-bug/blob/master/src/main/java/com/customannotationbug/entities/UserEvent.java)
[UserCreatedEvent](https://github.com/oalles/spring-data-custom-annotation-bug/blob/master/src/main/java/com/customannotationbug/entities/UserCreatedEvent.java)

Notice @EventRoot is declared locally on UserEvent, but not on UserCreatedEvent. Notice that it has a collection attribute, a collection name, set to a given value. 

**3. Config class definition.**
[AppConfig](https://github.com/oalles/spring-data-custom-annotation-bug/blob/master/src/main/java/com/customannotationbug/AppConfig.java)

Notice it is a concrete implementation for [AbstractMongoConfiguration](https://github.com/oalles/spring-data-mongodb/blob/master/spring-data-mongodb/src/main/java/org/springframework/data/mongodb/config/AbstractMongoConfiguration.java)

Notice a  custom implementation is provided for getInitialEntitySet() cause we are only interested in EventRoot annotated entities. 

**4. Test**
(Tests)[https://github.com/oalles/spring-data-custom-annotation-bug/blob/master/src/test/java/com/customannotationbug/tests/Tests.java]

*Expected behavior*. 
Cause spring data mongodb relies on AnnotationUtils, it should provide full support for custom annotations and should support Springs 4.2 AliasFor annotations. So both entities, when made persistententities, should have the same collection name provided. 

*Actual behaviour*. (whether this is a right config)
Neither, parent or child are getting the expected collection name. Both of them are set with the fallback name instead. 

[BasicMongoPersistentEntity.java](https://github.com/oalles/spring-data-mongodb/blob/master/spring-data-mongodb/src/main/java/org/springframework/data/mongodb/core/mapping/BasicMongoPersistentEntity.java)
```java
Document document = AnnotationUtils.findAnnotation(rawType, Document.class);
// document.collection() is empty and shouldnt
```

At first, i thought this was related to @Document being mark as @Inherited. And could it be? 
The parent entity has both annotations, a locally declared @EventRoot annotation and an inherited @Document annotation.
The child entity has an inherited @Document annotation and a @EventRoot annotation in its parent. 
When using AnnotationUtils.findAnnotation() would it be possible to synthesize AliasFor @Document attributes for any of these entities? 

I went a bit deeper with this, but it seems there is a problem with AnnotationUtils caches.
Annotation filters are used when looking for candidates entities in the classpath to be loaded. 

Just consider @EventRoot annotated resources.See (getInitialEntitySet())[https://github.com/oalles/spring-data-custom-annotation-bug/blob/master/src/main/java/com/customannotationbug/AppConfig.java]
 
Each resource in the classpath is loaded. A metadataReader is instantiated in order to access the class metadata. It relies on AnnotationUtils to process annotations and metaannotations. AnnotationUtils is filling its caches in the process. 

EntityRoot.java is in the queue as a resource to be loaded (before candidate entities??). AnnotationUtils detect it has a @Document annotation.
@Document is locally declared for this class, so Document is not going to be marked as an annotation to be synthesize when it should be. 
When it is time to BasicMongoPersistentEntity to do its job creating a persistententity for annotated types, @Document is stored in AnnotationUtils synthesizableCache as not synthesizable when it should be. 






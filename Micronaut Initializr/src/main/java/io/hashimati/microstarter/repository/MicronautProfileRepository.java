package io.hashimati.microstarter.repository;
/**
 * @author Ahmed Al Hashmi @hashimati
 */

import com.mongodb.client.model.IndexOptions;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
;
import io.hashimati.microstarter.entity.micronaut.FeaturesSkeleton;
import io.hashimati.microstarter.entity.micronaut.MicronautFeature;
import io.hashimati.microstarter.entity.micronaut.MicronautProfile;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import io.reactivex.Flowable;
import io.reactivex.Single;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonString;

import javax.inject.Inject;

//@CacheConfig(cacheNames={"MicronautProfile"})
public class MicronautProfileRepository extends MongoDBRepository<MicronautProfile>{

  @Inject
  private MongoClient mongoClient;
    public Single<MicronautProfile> findDistinctByName(String profile){

      BsonDocument query  = new BsonDocument();
      query.append("name", new BsonString(profile));
      return this.findAsSingle(query);
    }

  @Override
  public MongoCollection<MicronautProfile> getCollection() {
    return mongoClient
            .getDatabase("microstarter")
            .getCollection("micronaut_profiles", MicronautProfile.class);
  }
  public Single<MicronautProfile> save(MicronautProfile object)
  {
    System.out.println("Saving " + object.getName());

    Single<MicronautProfile> x = Single.fromPublisher(getCollection().insertOne(object))
            .map(success->
            {
              System.out.println("Succeed to Save " + object.getName());
              return object;
            })
            .onErrorReturn(fail->{
              System.out.println("Failed to Save " + object.getName());
              return object;
            });
    System.out.println(x.blockingGet().getName() + "HHH");


    return x;
  }


  @EventListener
  public void setIndex(StartupEvent event)
  {
    IndexOptions options = new IndexOptions();
    options.unique(true);
    Single.fromPublisher(mongoClient
            .getDatabase("microstarter")
            .getCollection("micronaut_profiles", FeaturesSkeleton.class)
            .createIndex(new BsonDocument().append("name", new BsonInt32(1)), options)).blockingGet();

  }
  public Flowable<MicronautProfile> findAllItems()
  {
    return Flowable.fromPublisher(getCollection().find());
  }

}

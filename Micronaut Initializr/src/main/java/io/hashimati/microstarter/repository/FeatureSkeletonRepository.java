package io.hashimati.microstarter.repository;
/**
 * @author Ahmed Al Hashmi @hashimati
 */

import com.mongodb.client.model.IndexOptions;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import io.hashimati.microstarter.entity.micronaut.FeaturesSkeleton;
import io.micronaut.cache.annotation.CacheConfig;
import io.micronaut.cache.annotation.Cacheable;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import io.reactivex.Flowable;
import io.reactivex.Single;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonString;

import javax.inject.Inject;
import javax.inject.Singleton;


@Singleton
public class  FeatureSkeletonRepository  extends MongoDBRepository<FeaturesSkeleton>{


    @Inject
    private MongoClient mongoClient;
    public FeaturesSkeleton findDistinctByName(String name){

        BsonDocument query  = new BsonDocument();
        query.append("name", new BsonString(name));
        return this.findAsSingle(query).blockingGet();

    }

    @Override
    public MongoCollection<FeaturesSkeleton> getCollection() {
        return mongoClient
                .getDatabase("microstarter")
                .getCollection("features_skeletons", FeaturesSkeleton.class);
    }
    public Single<FeaturesSkeleton> save(FeaturesSkeleton object)
    {

        System.out.println("Saving " + object.getName());

        Single<FeaturesSkeleton> x = Single.fromPublisher(getCollection().insertOne(object))
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
                .getCollection("features_skeletons", FeaturesSkeleton.class)
                .createIndex(new BsonDocument().append("name", new BsonInt32(1)), options)).blockingGet();

    }
    public Flowable<FeaturesSkeleton> findAllItems()
    {
        return Flowable.fromPublisher(getCollection().find());
    }
}

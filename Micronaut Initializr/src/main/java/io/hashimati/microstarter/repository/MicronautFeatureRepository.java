package io.hashimati.microstarter.repository;

/**
 * @author Ahmed Al Hashmi @hashimati
 */

import com.mongodb.client.model.IndexOptions;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import io.hashimati.microstarter.entity.micronaut.FeaturesSkeleton;
import io.hashimati.microstarter.entity.micronaut.MicronautFeature;
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
import java.util.ArrayList;
import java.util.List;

public class MicronautFeatureRepository extends MongoDBRepository<MicronautFeature>
{

    @Inject
    private MongoClient mongoClient;


    public Flowable<MicronautFeature> findAll(){


        return findAllItems();
    }

    public Flowable<MicronautFeature> findAllItems()
    {
        return Flowable.fromPublisher(getCollection().find());
    }
    public MicronautFeature findDistinctByName(String name){

        BsonDocument query  = new BsonDocument();
        query.append("name", new BsonString(name));
        return this.findAsSingle(query).blockingGet();
    }


    @Override
    public MongoCollection<MicronautFeature> getCollection() {
        return mongoClient
                .getDatabase("microstarter")
                .getCollection("micronaut_features", MicronautFeature.class);
    }
    public Single<MicronautFeature> save(MicronautFeature object)
    {
        System.out.println("Saving " + object.getName());

        Single<MicronautFeature> x = Single.fromPublisher(getCollection().insertOne(object))
                .map(success->
                {
                    System.out.println("Succeed to Save " + object.getName() );
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
                .getCollection("micronaut_features", MicronautFeature.class)
                .createIndex(new BsonDocument().append("name", new BsonInt32(1)),options )).blockingGet();

    }
}

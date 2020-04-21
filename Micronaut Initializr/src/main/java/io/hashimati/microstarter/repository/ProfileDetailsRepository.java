package io.hashimati.microstarter.repository;
/**
 * @author Ahmed Al Hashmi @hashimati
 */

import com.mongodb.client.model.IndexOptions;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import io.hashimati.microstarter.entity.micronaut.FeaturesSkeleton;
import io.hashimati.microstarter.entity.micronaut.MicronautProfile;
import io.hashimati.microstarter.entity.micronaut.profiles.ProfileDetails;
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

public class ProfileDetailsRepository extends MongoDBRepository<ProfileDetails>
{

    @Inject
    private MongoClient mongoClient;
    public ArrayList<ProfileDetails> findAll(){

        List<ProfileDetails> list = this.findAllItems().toList().blockingGet();

        return new ArrayList<ProfileDetails>(){{

            addAll(list);
        }};
    }

    public ProfileDetails findDistinctByProfileName(String profile){

        BsonDocument query  = new BsonDocument();
        query.append("name", new BsonString(profile));
        return this.findAsSingle(query).blockingGet();

    }


    @Override
    public MongoCollection<ProfileDetails> getCollection() {
        return mongoClient
                .getDatabase("microstarter")
                .getCollection("profile_details", ProfileDetails.class);
    }
    public Single<ProfileDetails> save(ProfileDetails object)
    {
        System.out.println("Saving " + object.getName());

        Single<ProfileDetails> x = Single.fromPublisher(getCollection().insertOne(object))
                .map(success->
                {
                    System.out.println("Succeed to save " + object.getName());
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
    { IndexOptions options = new IndexOptions();
        options.unique(true);
        Single.fromPublisher(mongoClient
                .getDatabase("microstarter")
                .getCollection("profile_details", FeaturesSkeleton.class)
                .createIndex(new BsonDocument().append("name", new BsonInt32(1)), options)).blockingGet();

    }
    public Flowable<ProfileDetails> findAllItems()
    {
        return Flowable.fromPublisher(getCollection().find());
    }

}


package io.hashimati.microstarter.repository;
/**
 * @author Ahmed Al Hashmi @hashimati
 */

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import io.reactivex.Flowable;
import io.reactivex.Single;
import org.bson.BsonDocument;
import reactor.core.publisher.Mono;

import javax.inject.Inject;

public abstract class MongoDBRepository<T>
{
    @Inject
    private MongoClient mongoClient;

    public abstract MongoCollection<T> getCollection();

    public Single<T> findAsSingle(BsonDocument query)
    {
        return Single
                .fromPublisher(getCollection()
                        .find(query));
    }

    public  Flowable<T> findAsFlowable(BsonDocument query)
    {
        return Flowable
                .fromPublisher(getCollection()
                        .find(query));
    }
    public abstract Single<T> save(T object);




}

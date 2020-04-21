package io.hashimati.microstarter.config;


import io.micronaut.cache.CacheManager;
import io.micronaut.scheduling.annotation.Scheduled;

import javax.inject.Inject;
import javax.inject.Singleton;

///**
// * CacheService..
// */
// @Singleton
//public class CacheService {
//    @Inject
//    private CacheManager cacheManager;               // autowire cache manager
//    @Scheduled(cron = "0 0/1 * * * ?")              // execure after every 60 min
//    public void clearCacheSchedule(){
//
//        for(String name :cacheManager.getCacheNames()){
//            cacheManager.getCache(name).invalidateAll();            // clear cache by name
//        }
//    }
//
//}
package io.hashimati.microstarter.entity;
/**
 * @author Ahmed Al Hashmi @hashimati
 */

import lombok.*;

import java.util.ArrayList;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder

public class Entity
{
    private String name, entityPackage,repoPackage, servicePackage,restPackage,clientPackage,

    //database type refer to the database
    //collectionname is refering to tht table name, entity name or collection name.
    databaseType, collectionName, databaseName; // SQL, Mongo, Cassandra, Neo4J;


    private ArrayList<EntityAttribute> attributes = new ArrayList<EntityAttribute>();

//
//    public String getName() {
//        return name;
//
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getEntityPackage() {
//        return entityPackage;
//    }
//
//    public void setEntityPackage(String entityPackage) {
//        this.entityPackage = entityPackage;
//    }
//
//    public String getDatabaseType() {
//        return databaseType;
//    }
//
//    public void setDatabaseType(String databaseType) {
//        this.databaseType = databaseType;
//    }
//
//    public String getRepoPackage() {
//        return repoPackage;
//    }
//
//    public void setRepoPackage(String repoPackage) {
//        this.repoPackage = repoPackage;
//    }
//
//    public String getServicePackage() {
//        return servicePackage;
//    }
//
//    public void setServicePackage(String servicePackage) {
//        this.servicePackage = servicePackage;
//    }
//
//    public String getRestPackage() {
//        return restPackage;
//    }
//
//    public void setRestPackage(String restPackage) {
//        this.restPackage = restPackage;
//    }
//
//
//    public String getCollectionName() {
//        return collectionName;
//    }
//
//    public void setCollectionName(String collectionName) {
//        this.collectionName = collectionName;
//    }
//
//    public ArrayList<EntityAttribute> getAttributes() {
//        return attributes;
//    }
//
//    public void setAttributes(ArrayList<EntityAttribute> attributes) {
//        this.attributes = attributes;
//    }
//
//    @Override
//    public String toString() {
//        return "Entity{" +
//                "name='" + name + '\'' +
//                ", entityPackage='" + entityPackage + '\'' +
//                ", repoPackage='" + repoPackage + '\'' +
//                ", servicePackage='" + servicePackage + '\'' +
//                ", restPackage='" + restPackage + '\'' +
//                ", databaseType='" + databaseType + '\'' +
//                ", collectionName='" + collectionName + '\'' +
//                ", attributes=" + attributes +
//                '}';
//    }
//
//    public String getDatabaseName() {
//        return databaseName;
//    }
//
//    public void setDatabaseName(String databaseName) {
//        this.databaseName = databaseName;
//    }
}
/*
EntityTemplate


package @entitypackage

import lombok.*;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class @className{

	@instances
}

================
Repository Template
=====================
Service Template
=====================
Controller Template
====================
 */
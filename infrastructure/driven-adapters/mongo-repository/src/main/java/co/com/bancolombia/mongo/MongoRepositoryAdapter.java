package co.com.bancolombia.mongo;

import co.com.bancolombia.model.Franchise;
import co.com.bancolombia.mongo.documents.FranchiseDocument;
import co.com.bancolombia.mongo.helper.AdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;

/*//@Repository
public class MongoRepositoryAdapter extends AdapterOperations<Franchise*//* change for domain model *//*, FranchiseDocument*//* change for adapter model *//*, String, MongoDBRepository>
// implements ModelRepository from domain
{

    public MongoRepositoryAdapter(MongoDBRepository repository, ObjectMapper mapper) {
        *//**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         *//*
        super(repository, mapper, d -> mapper.map(d, Franchise.class*//* change for domain model *//*));
    }
}*/

package co.com.bancolombia.mongo.mappers;

import co.com.bancolombia.model.Branch;
import co.com.bancolombia.model.Franchise;
import co.com.bancolombia.mongo.documents.BranchDocument;
import co.com.bancolombia.mongo.documents.FranchiseDocument;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class FranchiseMapper {

    public static FranchiseDocument toDocument(Franchise franchise) {
        List<BranchDocument> branches = new ArrayList<>();
        return new FranchiseDocument(franchise.getId(), franchise.getName(), branches);
    }

    public static Franchise toDomain(FranchiseDocument franchise) {
        log.debug("Converting Franchise Document to Franchise.");
        List<Branch> branches = new ArrayList<>();
        return new Franchise(franchise.getId(), franchise.getName(), branches);
    }
}

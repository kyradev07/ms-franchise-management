package co.com.bancolombia.mongo.documents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("franchises")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FranchiseDocument {

    @Id
    private String id;

    @Indexed(unique=true)
    private String name;

    private List<BranchDocument> branches;
}

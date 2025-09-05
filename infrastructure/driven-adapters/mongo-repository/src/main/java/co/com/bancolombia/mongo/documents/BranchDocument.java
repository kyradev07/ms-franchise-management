package co.com.bancolombia.mongo.documents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BranchDocument {
    private String id;
    private String name;
    private List<ProductDocument> products;

}

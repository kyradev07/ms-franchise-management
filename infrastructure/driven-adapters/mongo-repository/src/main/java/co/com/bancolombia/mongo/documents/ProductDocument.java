package co.com.bancolombia.mongo.documents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDocument {

    private String id;
    private String name;
    private Integer stock;

}

package company.plusminus.util.helpers;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TestEntity {

    @Id
    private Long id;
    private String myField;

}

package com.wini.leader_board_integration.data.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "gameplay")
public class GamePlay extends BaseDoc{

    private static final long serialVersionUID = 1L;
    @Id
    private String id;
    private String name;
    private Map<String, Object> platformData;
    private String keyData;

}

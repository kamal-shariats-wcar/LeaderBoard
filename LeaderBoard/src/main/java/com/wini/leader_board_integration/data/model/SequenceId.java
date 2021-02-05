package com.wini.leader_board_integration.data.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by kamal on 1/14/2019.
 */
@Data
@NoArgsConstructor
@Document(collection = "sequence")
public class SequenceId {
    private static final long serialVersionUID = 1L;
    @Id
    private String id;
    private Long seq;
}

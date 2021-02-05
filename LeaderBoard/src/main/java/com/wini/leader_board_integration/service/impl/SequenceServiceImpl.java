package com.wini.leader_board_integration.service.impl;

import com.wini.leader_board_integration.data.model.SequenceId;
import com.wini.leader_board_integration.repository.SequenceRepository;
import com.wini.leader_board_integration.service.SequenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * Created by kamal on 1/14/2019.
 */
@Service
public class SequenceServiceImpl implements SequenceService {
    @Value("${guest.sequence.key}")
    private String guestKey;
    private final String PHOTO_ID="photo";
    @Autowired
    SequenceRepository sequenceRepository;
    @Autowired
    private MongoOperations mongoOperations;

    @Override
    public long generateSequence(String seqName) {

        SequenceId counter = mongoOperations.findAndModify(query(where("_id").is(seqName)),
                new Update().inc("seq",1), options().returnNew(true).upsert(true),
                SequenceId.class);
        return !Objects.isNull(counter) ? counter.getSeq() : 1;

    }

    @Override
    public Long getNextSequence(String key) {
        Optional<SequenceId> sequenceId = sequenceRepository.findById(key);
        Long id = (sequenceId.get().getSeq()) + 1;
        sequenceId.get().setSeq(id);
        sequenceRepository.save(sequenceId.get());
        return id;
    }

    @Override
    public Long getNextGuestId() {
        return getNextSequence(guestKey);
    }

    @Override
    public Long getNextPhotoId() {
        return getNextSequence(PHOTO_ID);
    }

    @Override
    public SequenceId saveSequence(SequenceId sequenceId) {
        return sequenceRepository.save(sequenceId);
    }

    @Override
    public SequenceId findById(String id) {
        return sequenceRepository.findById(id).orElse(null);
    }
}

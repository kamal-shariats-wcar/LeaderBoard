package com.wini.leader_board_integration.service;


import com.wini.leader_board_integration.data.model.SequenceId;

/**
 * Created by kamal on 1/14/2019.
 */
public interface SequenceService {
    Long getNextSequence(String key) ;

    Long getNextGuestId();

    Long getNextPhotoId();

    SequenceId saveSequence(SequenceId sequenceId);
    SequenceId findById(String id);
    public long generateSequence(String seqName);
}

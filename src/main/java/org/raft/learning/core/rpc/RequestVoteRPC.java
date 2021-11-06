package org.raft.learning.core.rpc;

import org.raft.learning.core.model.VoteRequest;

public interface RequestVoteRPC {

   default void requestVote(
           String followerId,
           VoteRequest voteRequest
   ) {
   }
}

package org.raft.learning.core.election;

import org.raft.learning.core.model.VoteRequest;
import org.raft.learning.core.model.VoteResponse;

public interface Election {

   void initiate();

   void sendVoteRequests();

   VoteResponse vote(VoteRequest voteRequest);

   void processVote(VoteResponse response);
}

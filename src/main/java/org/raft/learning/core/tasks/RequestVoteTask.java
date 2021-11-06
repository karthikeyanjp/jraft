package org.raft.learning.core.tasks;

import org.raft.learning.core.model.VoteRequest;
import org.raft.learning.core.rpc.RequestVoteRPC;

public class RequestVoteTask implements Runnable, RequestVoteRPC {

   private final String followerId;
   private final VoteRequest voteRequest;

   public RequestVoteTask(String followerId, VoteRequest voteRequest) {
      this.followerId = followerId;
      this.voteRequest = voteRequest;
   }


   @Override
   public void run() {
      requestVote(this.followerId, this.voteRequest);
   }
}

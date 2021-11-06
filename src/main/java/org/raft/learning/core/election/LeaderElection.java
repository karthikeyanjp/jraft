package org.raft.learning.core.election;

import org.raft.learning.core.Node;
import org.raft.learning.core.NodeState;
import org.raft.learning.core.model.VoteRequest;
import org.raft.learning.core.model.VoteResponse;
import org.raft.learning.core.tasks.RequestVoteTask;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LeaderElection implements Election {

   private final ExecutorService requestExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
   private final Node node;
   /**
    * Election parameters
    */
   private Integer votesReceived;
   private String votedFor; // candidateId that received the vote in the current term

   public LeaderElection(Node node) {
      this.node = node;
      this.votesReceived = 0;
   }

   @Override
   public void initiate() {
      this.node.incrementTerm();
      this.node.setState(NodeState.CANDIDATE);
      this.votesReceived = 1; // candidate votes for itself
      this.sendVoteRequests();
   }

   @Override
   public void sendVoteRequests() {
      final VoteRequest voteRequest =
              new VoteRequest(this.node.getCurrentTerm(), this.node.getId(), this.node.getLastAppliedIndex(), this.node.getLastAppliedTerm());
      for (String followerId : this.node.getPeerIds()) {
         requestExecutor.submit(new RequestVoteTask(followerId, voteRequest));
      }
   }

   @Override
   public VoteResponse vote(VoteRequest voteRequest) {
      final VoteResponse response = new VoteResponse();
      response.setTerm(this.node.getCurrentTerm());
      /*
         Vote is not granted if
         1) already voted for another candidate
         2) the candidate's term is less than the current term.
         3) the candidate's log entry is not as updated as this server
       */
      response.setTerm(this.node.getCurrentTerm());
      if (
              (this.votedFor != null && !this.votedFor.equals(voteRequest.getCandidateId()))
                      || (voteRequest.getTerm() < this.node.getCurrentTerm())
                      || (voteRequest.getLastLogIndex() < this.node.getLastAppliedIndex())
      ) {
         response.setVoteGranted(false);
      } else {
         this.votedFor = voteRequest.getCandidateId();
         response.setVoteGranted(true);
      }
      return response;
   }

   @Override
   public void processVote(VoteResponse response) {
      if (response != null && response.isVoteGranted() && response.getTerm().equals(this.node.getCurrentTerm())) {
         this.votesReceived++;
         // check for majority vote for the same term
         if (isConsensusReached(this.votesReceived, this.node.getPeerIds().size())) {
            this.node.convertToLeader();
         }
      }
   }

   public boolean receivedHeartBeatWithinThreshold() {
      return !(this.node.getLastHeartBeatTimeinMillis() == 0 ||
              this.node.getTimeSinceLastHeartBeatInMillis() >
                      this.node.getRaftConfiguration().getElectionTimeoutInMillis());
   }

   private boolean isConsensusReached(int votesReceived, int maxPeers) {
      int threshold = maxPeers / 2;
      if (maxPeers % 2 == 1) {
         threshold++;
      }
      return votesReceived > threshold;
   }

   public Node getServer() {
      return node;
   }

   public Integer getVotesReceived() {
      return votesReceived;
   }

   public String getVotedFor() {
      return votedFor;
   }
}

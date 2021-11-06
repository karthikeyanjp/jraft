package org.raft.learning.core.tasks;


import org.raft.learning.core.NodeState;
import org.raft.learning.core.election.LeaderElection;

public class ElectionTask implements Runnable {

   private final LeaderElection election;

   public ElectionTask(LeaderElection election) {
      this.election = election;
   }

   @Override
   public void run() {
      if (this.election.getServer().getState() == NodeState.FOLLOWER) {
         if (!election.receivedHeartBeatWithinThreshold()) {
            election.initiate();
         }
      } else if (this.election.getServer().getState() == NodeState.CANDIDATE) {
         election.initiate();
      }
   }
}

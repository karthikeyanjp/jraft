package org.raft.learning.core;

import org.raft.learning.utils.Helpers;

public class RaftConfiguration {

   private final Integer electionTimeoutInMillis = Helpers.randIntBetween(150, 300);

   public Integer getElectionTimeoutInMillis() {
      return electionTimeoutInMillis;
   }

}

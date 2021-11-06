package org.raft.learning.core.model;

public class VoteResponse {

   /**
    * current term for the candidate to update itself
    */
   private Long term;

   /**
    * vote granted or not
    */
   private boolean voteGranted;

   public Long getTerm() {
      return term;
   }

   public void setTerm(Long term) {
      this.term = term;
   }

   public boolean isVoteGranted() {
      return voteGranted;
   }

   public void setVoteGranted(Boolean voteGranted) {
      this.voteGranted = voteGranted;
   }
}
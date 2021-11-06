package org.raft.learning.core.model;

public class AppendResponse {
   private Long term; // currentTerm, for the leader to update itself;
   private Boolean success; // if follower contained entry matching prevLogIndex and prevLogTerm;


   public AppendResponse() {
   }

   public AppendResponse(Long term, Boolean success) {
      this.term = term;
      this.success = success;
   }

   public Long getTerm() {
      return term;
   }

   public void setTerm(Long term) {
      this.term = term;
   }

   public Boolean getSuccess() {
      return success;
   }

   public void setSuccess(Boolean success) {
      this.success = success;
   }
}

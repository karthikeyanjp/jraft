package org.raft.learning.core.model;

public class VoteRequest {

   private Long term; // candidate's term
   private String candidateId; // candidate requesting vote
   private Long lastLogIndex; // index of the candidate's last log entry;
   private Long lastLogTerm; // term of the candidate's last log entry;

   public VoteRequest() {
   }

   public VoteRequest(Long term, String candidateId, Long lastLogIndex, Long lastLogTerm) {
      this.term = term;
      this.candidateId = candidateId;
      this.lastLogIndex = lastLogIndex;
      this.lastLogTerm = lastLogTerm;
   }

   public Long getTerm() {
      return term;
   }

   public void setTerm(Long term) {
      this.term = term;
   }

   public String getCandidateId() {
      return candidateId;
   }

   public void setCandidateId(String candidateId) {
      this.candidateId = candidateId;
   }

   public Long getLastLogIndex() {
      return lastLogIndex;
   }

   public void setLastLogIndex(Long lastLogIndex) {
      this.lastLogIndex = lastLogIndex;
   }

   public Long getLastLogTerm() {
      return lastLogTerm;
   }

   public void setLastLogTerm(Long lastLogTerm) {
      this.lastLogTerm = lastLogTerm;
   }
}

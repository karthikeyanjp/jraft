package org.raft.learning.core.model;

public class RPCResponse {
   private Long term;

   public Long getTerm() {
      return term;
   }

   public void setTerm(Long term) {
      this.term = term;
   }
}

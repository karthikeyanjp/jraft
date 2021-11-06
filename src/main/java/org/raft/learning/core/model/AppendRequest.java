package org.raft.learning.core.model;

import org.raft.learning.core.Log;

public class AppendRequest {
   private Long term; // leader's term
   private String leaderId; // leader's ID
   private Long prevLogIndex; // index of the log entry immediately preceding the new ones
   private Long prevLogTerm; // term of the prevLogIndex entry
   private Log[] entries; // entries to store; empty for heart beat
   private Long leaderCommit; // leader's commit index

   public AppendRequest() {
   }

   public AppendRequest(Builder builder) {
      this.term = builder.term;
      this.leaderCommit = builder.leaderCommit;
      this.leaderId = builder.leaderId;
      this.prevLogTerm = builder.prevLogTerm;
      this.prevLogIndex = builder.prevLogIndex;
      this.entries = builder.entries;
   }

   public Long getTerm() {
      return term;
   }

   public String getLeaderId() {
      return leaderId;
   }

   public Long getPrevLogIndex() {
      return prevLogIndex;
   }

   public Long getPrevLogTerm() {
      return prevLogTerm;
   }

   public Log[] getEntries() {
      return entries;
   }

   public Long getLeaderCommit() {
      return leaderCommit;
   }

   public static class Builder {
      private final Long term;
      private final String leaderId;
      private Long prevLogIndex;
      private Long prevLogTerm;
      private Log[] entries;
      private Long leaderCommit;

      public Builder(String leaderId, Long term) {
         this.term = term;
         this.leaderId = leaderId;
      }

      public Builder withPrevLogIndex(Long prevLogIndex) {
         this.prevLogIndex = prevLogIndex;
         return this;
      }

      public Builder withPrevLogTerm(Long prevLogTerm) {
         this.prevLogTerm = prevLogTerm;
         return this;
      }


      public Builder withLeaderCommit(Long leaderCommit) {
         this.leaderCommit = leaderCommit;
         return this;
      }


      public Builder heartbeat() {
         this.entries = new Log[0];
         return this;
      }

      public Builder entries(Log[] entries) {
         this.entries = entries;
         return this;
      }

      public AppendRequest build() {
         return new AppendRequest(this);
      }
   }
}

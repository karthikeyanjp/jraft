package org.raft.learning.core;

public class Log {

   private Long index; // Index of the entry. starts with 1
   private Long term; // term when the leader received this entry
   private Object entry; // entry

   public Long getIndex() {
      return index;
   }

   public void setIndex(Long index) {
      this.index = index;
   }

   public Long getTerm() {
      return term;
   }

   public void setTerm(Long term) {
      this.term = term;
   }

   public Object getEntry() {
      return entry;
   }

   public void setEntry(Object entry) {
      this.entry = entry;
   }
}

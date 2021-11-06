package org.raft.learning.core.tasks;

import org.raft.learning.core.model.AppendResponse;

public class SendAppendResponseTask implements Runnable {

   private final AppendResponse appendResponse;

   public SendAppendResponseTask(AppendResponse appendResponse) {
      this.appendResponse = appendResponse;
   }


   @Override
   public void run() {
      System.out.println("Sending Append Response " + appendResponse);
   }
}

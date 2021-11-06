package org.raft.learning.core.utils;

import org.raft.learning.core.Node;
import org.raft.learning.core.RaftConfiguration;

import java.util.HashSet;
import java.util.UUID;

public class TestUtils {

   public static Node node() {
      return new Node(new RaftConfiguration());

   }

   public static Node node(int totalPeers) {
      final Node node = new Node(new RaftConfiguration());
      node.setPeerIds(new HashSet<>());
      for (int i = 0; i < totalPeers; i++) {
         node.getPeerIds().add(UUID.randomUUID().toString());
      }
      return node;
   }
}

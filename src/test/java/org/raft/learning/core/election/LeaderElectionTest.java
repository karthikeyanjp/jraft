package org.raft.learning.core.election;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.raft.learning.core.Node;
import org.raft.learning.core.NodeState;
import org.raft.learning.core.model.AppendRequest;
import org.raft.learning.core.model.VoteResponse;
import org.raft.learning.core.tasks.ElectionTask;
import org.raft.learning.core.utils.TestUtils;

import java.util.UUID;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LeaderElectionTest {

   @Test
   @DisplayName("node should begin as follower")
   void shouldStartWithInitialState() {
      final Node node = TestUtils.node();
      assertEquals(NodeState.FOLLOWER, node.getState());
   }

   @Test
   @DisplayName("node should stay as follower as long as it receives heartbeat from Leader")
   void shouldStayAsFollower() throws InterruptedException {
      final Node node = TestUtils.node();
      node.afterPropertiesSet();
      assertEquals(NodeState.FOLLOWER, node.getState());

      // simulated delay
      sleep(75);

      // Received heartbeat
      AppendRequest heartBeat =
              new AppendRequest.Builder("leaderID", 0L)
                      .heartbeat()
                      .withPrevLogIndex(0L)
                      .withPrevLogTerm(0L)
                      .build();
      node.processAppend(heartBeat);

      // simulated delay (before next heart beat)
      sleep(75);
      node.processAppend(heartBeat);


      assertEquals(NodeState.FOLLOWER, node.getState());
   }

   @Test
   @DisplayName("node should become candidate when no heart beat is received before timeout")
   void shouldComeForwardAsCandidate() throws InterruptedException {
      final Node node = TestUtils.node();
      node.afterPropertiesSet();
      Long currentTerm = node.getCurrentTerm();
      assertEquals(NodeState.FOLLOWER, node.getState());

      // simulated delay
      sleep(500);

      final Long expectedTerm = ++currentTerm;
      assertEquals(NodeState.CANDIDATE, node.getState());
      sleep(500);
      assertTrue( node.getCurrentTerm() > currentTerm);
   }

   @Test
   @DisplayName("candidate should vote for itself and send VoteRequests to all peers")
   void candidacyChecks() throws InterruptedException {
      int totalPeers = 4; // 5 node cluster
      final Node node = TestUtils.node(totalPeers);
      assertEquals(4, node.getPeerIds().size());

      final LeaderElection election = new LeaderElection(node);
      final ElectionTask electionTask = new ElectionTask(election);
      electionTask.run();
      /*
         node should
         1. be in candidate state:
         2. Self vote
       */
      assertEquals(NodeState.CANDIDATE, node.getState());
      assertEquals(1, election.getVotesReceived());
      sleep(500);
   }

   @Test
   @DisplayName("candidate should win election if it receives maximum votes")
   void shouldWinElection() throws InterruptedException {
      int totalPeers = 4; // 5 node cluster
      final Node node = TestUtils.node(totalPeers);
      node.afterPropertiesSet();

      // simulated delay (Election will begin)
      sleep(500);
      final LeaderElection election = new LeaderElection(node);
      final ElectionTask electionTask = new ElectionTask(election);
      electionTask.run();

      assertEquals(NodeState.CANDIDATE, node.getState());
      final Long currentTerm = node.getCurrentTerm();

      // Receive max votes
      VoteResponse voteResponse = new VoteResponse();
      voteResponse.setVoteGranted(true);
      voteResponse.setTerm(currentTerm);

      election.processVote(voteResponse);
      election.processVote(voteResponse);
      election.processVote(voteResponse);

      assertEquals(NodeState.LEADER, node.getState());
   }


   @Test
   @DisplayName("candidate should forego election if it receives valid Append RPC from another node")
   void shouldBecomeFollower() throws InterruptedException {
      int totalPeers = 4; // 5 node cluster
      final Node node = TestUtils.node(totalPeers);
      node.afterPropertiesSet();

      // simulated delay (Election will begin)
      sleep(500);
      final LeaderElection election = new LeaderElection(node);
      final ElectionTask electionTask = new ElectionTask(election);
      electionTask.run();

      assertEquals(NodeState.CANDIDATE, node.getState());
      final Long currentTerm = node.getCurrentTerm();

      // Receive max votes
      VoteResponse voteResponse = new VoteResponse();
      voteResponse.setVoteGranted(true);
      voteResponse.setTerm(currentTerm);

      election.processVote(voteResponse);

      final AppendRequest request = new AppendRequest.Builder(UUID.randomUUID().toString(), 5L)
              .heartbeat()
              .build();

      node.processAppend(request);

      assertEquals(NodeState.FOLLOWER, node.getState());
   }


   @Test
   @DisplayName("candidate should remain as candidate if it receives valid Append RPC from another node with lesser term")
   void shouldRemainCandidate() throws InterruptedException {
      int totalPeers = 4; // 5 node cluster
      final Node node = TestUtils.node(totalPeers);
      node.afterPropertiesSet();

      // simulated delay (Election will begin)
      sleep(500);
      final LeaderElection election = new LeaderElection(node);
      final ElectionTask electionTask = new ElectionTask(election);
      electionTask.run();

      assertEquals(NodeState.CANDIDATE, node.getState());
      final Long currentTerm = node.getCurrentTerm();

      // Receive max votes
      VoteResponse voteResponse = new VoteResponse();
      voteResponse.setVoteGranted(true);
      voteResponse.setTerm(currentTerm);

      election.processVote(voteResponse);

      final AppendRequest request = new AppendRequest.Builder(UUID.randomUUID().toString(), 1L)
              .heartbeat()
              .build();

      node.processAppend(request);

      assertEquals(NodeState.CANDIDATE, node.getState());
   }


   @Test
   @DisplayName("candidate should start re-election on split vote")
   void shouldStartReElection() throws InterruptedException {
      int totalPeers = 4; // 5 node cluster
      final Node node = TestUtils.node(totalPeers);
      node.afterPropertiesSet();

      // simulated delay (Election will begin)
      sleep(500);
      final LeaderElection election = new LeaderElection(node);
      final ElectionTask electionTask = new ElectionTask(election);
      electionTask.run();

      assertEquals(NodeState.CANDIDATE, node.getState());
      final Long currentTerm = node.getCurrentTerm();

      // Receive max votes
      VoteResponse voteResponse = new VoteResponse();
      voteResponse.setVoteGranted(true);
      voteResponse.setTerm(currentTerm);

      election.processVote(voteResponse);

      sleep(500); // simulated timeout - no node is declared with majority within timeout
      assertEquals(NodeState.CANDIDATE, node.getState());
      assertTrue(node.getCurrentTerm() > currentTerm);
   }
}

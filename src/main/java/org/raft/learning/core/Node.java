package org.raft.learning.core;

import org.raft.learning.core.election.LeaderElection;
import org.raft.learning.core.model.AppendRequest;
import org.raft.learning.core.model.AppendResponse;
import org.raft.learning.core.model.RPCResponse;
import org.raft.learning.core.tasks.ElectionTask;
import org.raft.learning.utils.Helpers;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Server represents a component that provides the functionality of a Consensus Module in Raft
 */
public class Node {

   /**
    * Executors
    */
   private final Long lastAppliedTerm; // term of the highest log entry applied to the State machine
   private final Long lastAppliedIndex; // index of the highest log entry applied to the State machine
   /**
    * Persistent State on all node
    *
    * <p>
    * These should be persisted on stable storage before responding to any RPCs.
    * </p>
    */
   private Long currentTerm; // latest term this server has seen; increases monotonically
   private String id;
   private Set<String> peerIds;
   private String leaderId;
   private NodeState state;
   private RaftConfiguration raftConfiguration;
   private Log[] logs; // log entries
   /**
    * Volatile State on all servers
    */
   private Long commitIndex; // index of the highest log entry known to be committed
   /**
    * Volatile State for the Leader
    *
    * <p>
    * These should be re-initialized after election
    * </p>
    */

   /* index of the next log entry to be sent to each server; last log index + 1 */
   private Map<String, Long> nextIndex;
   /* index of the highest log entry known to be replicated on each server */
   private Map<String, Long> matchIndex;

   private Long timeSinceLastHeartBeatInMillis;
   private Long lastHeartBeatTimeinMillis;

   public Node(RaftConfiguration raftConfiguration) {
      this.raftConfiguration = raftConfiguration;
      this.state = NodeState.FOLLOWER;
      this.currentTerm = 0L;
      this.lastAppliedTerm = 0L;
      this.lastAppliedIndex = 0L;
      this.peerIds = new HashSet<>();
      this.lastHeartBeatTimeinMillis = 0L;
   }

   public void afterPropertiesSet() {
      initiateTasks();
   }

   private void initiateTasks() {
      ScheduledExecutorService electionExecutor = Executors.newSingleThreadScheduledExecutor();
      electionExecutor.scheduleWithFixedDelay(new ElectionTask(new LeaderElection(this)), 300, this.raftConfiguration.getElectionTimeoutInMillis(), TimeUnit.MILLISECONDS);
   }

   private Boolean isLogInCorrectPosition(Log entry, Long term) {
      if (entry == null) return false;
      return term.equals(entry.getTerm());
   }

   public AppendResponse replyToAppend(AppendRequest appendRequest) {
      final AppendResponse response = new AppendResponse();
      response.setTerm(this.currentTerm);

      /**
       * Reply false if
       * 1) term < current Term
       * 2) log doesn't contain an entry at prevLogIndex whose term matches prevLogTerm
       */
      final boolean loginCorrectPosition = isLogInCorrectPosition(logAtIndex(appendRequest.getPrevLogIndex()), appendRequest.getPrevLogTerm());
      if ((appendRequest != null && appendRequest.getTerm() < this.currentTerm) || !loginCorrectPosition) {
         response.setSuccess(false);
      }

      /**
       *  existing entry conflicts with new one (same index with different terms), delete all entries that follows it
       */
      if (!loginCorrectPosition) {
         deleteEntriesAfter(appendRequest.getPrevLogIndex());
      }

      /**
       * Append new entries not already in the log
       */
      appendEntries(appendRequest.getEntries());

      /**
       * if leaderCommit > commitIndex, set commitIndex = min(leaderCommit, index of last new entry)
       */
      if (appendRequest.getLeaderCommit() > this.commitIndex) {
         this.commitIndex = Math.min(appendRequest.getLeaderCommit(), this.lastAppliedIndex);
      }
      response.setSuccess(true);
      return response;
   }

   private void appendEntries(Log[] entries) {
      //TODO Append entries to log
   }

   private void deleteEntriesAfter(Long prevLogIndex) {

      if (this.state == NodeState.LEADER) return; // Leader is always append-only

      //TODO delete entries after
   }

   private void convertToFollower(AppendRequest request) {
      this.state = NodeState.FOLLOWER;
      this.leaderId = request.getLeaderId();
   }

   public void convertToLeader() {
      this.state = NodeState.LEADER;
      this.sendHeartBeats();
   }

   private Log logAtIndex(Long index) {
      //TODO lookup log by Index
      return new Log();
   }

   public RPCResponse applyLog(Log log) {
      //TODO Apply Log to State machine
      return new RPCResponse();
   }


   public void processAppend(AppendRequest request) {
      if (request.getEntries().length == 0) {
         // Heart beat
         this.processHeartBeat(request);
      }
      // TODO process entries
   }

   private void processHeartBeat(AppendRequest request) {
      if (request.getTerm() > this.currentTerm) {
         // Forego candidacy
         this.convertToFollower(request);
      }
      if (request.getTerm() < this.currentTerm) {
         // Reject the request
         return;
      }

      // Register last heartbeat
      this.timeSinceLastHeartBeatInMillis = Helpers.currentTimeinMillis() - lastHeartBeatTimeinMillis;
      this.lastHeartBeatTimeinMillis = Helpers.currentTimeinMillis();
   }

   public void sendHeartBeats() {
      //TODO Heart beats logic
   }


   public void incrementTerm() {
      this.currentTerm++;
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public Set<String> getPeerIds() {
      return peerIds;
   }

   public void setPeerIds(Set<String> peerIds) {
      this.peerIds = peerIds;
   }

   public NodeState getState() {
      return state;
   }


   public void setState(NodeState state) {
      this.state = state;
   }

   public RaftConfiguration getRaftConfiguration() {
      return raftConfiguration;
   }

   public void setRaftConfiguration(RaftConfiguration raftConfiguration) {
      this.raftConfiguration = raftConfiguration;
   }

   public Long getTimeSinceLastHeartBeatInMillis() {
      return timeSinceLastHeartBeatInMillis;
   }

   public void setTimeSinceLastHeartBeatInMillis(Long timeSinceLastHeartBeatInMillis) {
      this.timeSinceLastHeartBeatInMillis = timeSinceLastHeartBeatInMillis;
   }

   public Long getLastHeartBeatTimeinMillis() {
      return lastHeartBeatTimeinMillis;
   }

   public void setLastHeartBeatTimeinMillis(Long lastHeartBeatTimeinMillis) {
      this.lastHeartBeatTimeinMillis = lastHeartBeatTimeinMillis;
   }

   public Long getCurrentTerm() {
      return currentTerm;
   }

   public Long getLastAppliedTerm() {
      return lastAppliedTerm;
   }

   public String getLeaderId() {
      return leaderId;
   }

   public void setLeaderId(String leaderId) {
      this.leaderId = leaderId;
   }

   public Log[] getLogs() {
      return logs;
   }

   public void setLogs(Log[] logs) {
      this.logs = logs;
   }

   public Long getCommitIndex() {
      return commitIndex;
   }

   public Long getLastAppliedIndex() {
      return lastAppliedIndex;
   }

   public Map<String, Long> getNextIndex() {
      return nextIndex;
   }

   public Map<String, Long> getMatchIndex() {
      return matchIndex;
   }

}

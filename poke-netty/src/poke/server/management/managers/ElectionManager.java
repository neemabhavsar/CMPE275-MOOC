/*
 * copyright 2014, gash
 * 
 * Gash licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package poke.server.management.managers;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import poke.server.management.ManagementInitializer;

import eye.Comm.LeaderElection;
import eye.Comm.Management;
import eye.Comm.LeaderElection.VoteAction;

/**
 * The election manager is used to determine leadership within the network.
 * 
 * @author gash
 * 
 */
public class ElectionManager {
	protected static Logger logger = LoggerFactory.getLogger("management");
	protected static AtomicReference<ElectionManager> instance = new AtomicReference<ElectionManager>();

	private String nodeId;
	
	//********* PARTH ************//
	private String leaderName=null;
	private boolean isParticipant = false;
	private String destinationHost;
	private int destinationPort;
	private String destinationNodeId;
	private Channel channel;
// ********** PARTH ******** //
	/** @brief the number of votes this server can cast */
	private int votes = 1;

	public static ElectionManager getInstance(String id, int votes) {
		instance.compareAndSet(null, new ElectionManager(id, votes));
		return instance.get();
	}

	public static ElectionManager getInstance() {
		return instance.get();
	}

	/**
	 * initialize the manager for this server
	 * 
	 * @param nodeId
	 *            The server's (this) ID
	 */
	protected ElectionManager(String nodeId, int votes) {
		this.nodeId = nodeId;

		if (votes >= 0)
			this.votes = votes;
	}

	/**
	 * @param args
	 */
	
	public void processRequest(LeaderElection req) {
		if (req == null)
			return;

		// logger.info("Received an election request..");
		Management msg = null;
		if (req.hasExpires()) {
			long ct = System.currentTimeMillis();
			if (ct > req.getExpires()) {
				// election is over
				return;
			}
		}

		if (req.getVote().getNumber() == VoteAction.ELECTION_VALUE) {
			// an election is declared!
			logger.info("Election declared!");

		} else if (req.getVote().getNumber() == VoteAction.DECLAREVOID_VALUE) {
			// no one was elected, I am dropping into standby mode
		} else if (req.getVote().getNumber() == VoteAction.DECLAREWINNER_VALUE) {
			// some node declared themself the leader
			leaderName = req.getNodeId();
			logger.info("Winner declared! leader is :" + leaderName);
			
			
			if (!leaderName.equals(nodeId)) {
				msg = generateLE(LeaderElection.VoteAction.DECLAREWINNER,
						leaderName);
				send(msg);
			}

		} else if (req.getVote().getNumber() == VoteAction.ABSTAIN_VALUE) {
			// for some reason, I decline to vote
		} else if (req.getVote().getNumber() == VoteAction.NOMINATE_VALUE) {
			logger.info("Received a nomination!");

			// LCR

			int comparedToMe = req.getNodeId().compareTo(nodeId);
			if (comparedToMe < 0) {

				if (!isParticipant) {
					logger.info("My nodeId is higher..so nominating myself if I am not a participant yet!");
					msg = generateLE(LeaderElection.VoteAction.NOMINATE, nodeId);
					send(msg);
				}

			} else if (comparedToMe > 0) {

				logger.info("Forwarding the nomination!");
				msg = generateLE(LeaderElection.VoteAction.NOMINATE,
						req.getNodeId());
				send(msg);

			} else if (comparedToMe == 0) {
				logger.info("I am the leader..");
				msg = generateLE(LeaderElection.VoteAction.DECLAREWINNER,
						nodeId);
				send(msg);
				leaderName = nodeId;

			} else {
				logger.info("Received nodeid is :" + req.getNodeId());
				logger.info("my nodeid is :" + nodeId);
				logger.info("ComparedToMe is :" + comparedToMe);
			}
		}
	}
	
	public void addConnectToThisNode(String nodeId, String host, int mgmtport) {

		destinationHost = host;
		destinationPort = mgmtport;
		destinationNodeId = nodeId;

		logger.info("Election manager --> Host is: "
				+ destinationHost + " and destPort is: " + destinationPort);
	}
	
	public Channel connect() {
		// Start the connection attempt.
		ChannelFuture channelFuture = null;
		EventLoopGroup group = new NioEventLoopGroup();

		try {
			ManagementInitializer mi = new ManagementInitializer(false);
			Bootstrap b = new Bootstrap();

			b.group(group).channel(NioSocketChannel.class).handler(mi);
			b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);
			b.option(ChannelOption.TCP_NODELAY, true);
			b.option(ChannelOption.SO_KEEPALIVE, true);

			logger.info("destination host & port " + destinationHost + "& " + destinationPort);

			InetSocketAddress destination = new InetSocketAddress(destinationHost,
					destinationPort);
			logger.info("destination host & port " + destinationHost + "& " + destinationPort);
			
			channelFuture = b.connect(destination);
			channelFuture.awaitUninterruptibly(5000l);

			logger.info("connection successful");

		} catch (Exception ex) {
			logger.debug("Connection Failure:");

		}
		logger.info("The ChannelFuture.isdone boolean is "+channelFuture.isDone()+" and channelFutre.isSuccess" +channelFuture.isSuccess()+" .");

		if (channelFuture != null && channelFuture.isDone()
				&& channelFuture.isSuccess())
			return channelFuture.channel();
		else
			throw new RuntimeException(
					"Not able to establish connection to server");
	}

	private Management generateLE(VoteAction vote, String nodeId) {
		LeaderElection.Builder electionBuilder = LeaderElection.newBuilder();
		electionBuilder.setNodeId(nodeId);
		electionBuilder.setBallotId("0");
		electionBuilder.setDesc("election message");
		electionBuilder.setVote(vote);
		LeaderElection electionMsg = electionBuilder.build();

		Management.Builder mBuilder = Management.newBuilder();
		mBuilder.setElection(electionMsg);
		Management msg = mBuilder.build();

		return msg;
	}

	private void send(Management msg) {
		try {
			channel = connect();

			channel.writeAndFlush(msg);

			isParticipant = true;

			logger.info("Election message (" + nodeId + ") sent to "
					+ destinationNodeId + " at " + destinationHost);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Failed to send leader election message");
		}
	}

	public void initiateElection() {
		logger.info("starting Election manager");

		Management msg = null;

		if (leaderName == null && !isParticipant) {
			msg = generateLE(LeaderElection.VoteAction.NOMINATE, nodeId);
		}

		send(msg);
	}

	
	
	

	
}
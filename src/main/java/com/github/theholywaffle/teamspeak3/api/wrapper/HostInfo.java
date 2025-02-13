package com.github.theholywaffle.teamspeak3.api.wrapper;

/*
 * #%L
 * TeamSpeak 3 Java API
 * %%
 * Copyright (C) 2014 Bert De Geyter
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import com.github.theholywaffle.teamspeak3.api.ServerInstanceProperty;

import java.util.Date;
import java.util.Map;

public class HostInfo extends Wrapper {

	public HostInfo(Map<String, String> map) {
		super(map);
	}

	public long getUptime() {
		return getLong(ServerInstanceProperty.INSTANCE_UPTIME);
	}

	public Date getTimeStamp() {
		return new Date(getLong(ServerInstanceProperty.HOST_TIMESTAMP_UTC) * 1000);
	}

	public int getTotalRunningServers() {
		return getInt(ServerInstanceProperty.VIRTUALSERVERS_RUNNING_TOTAL);
	}

	public int getTotalMaxClients() {
		return getInt(ServerInstanceProperty.VIRTUALSERVERS_TOTAL_MAXCLIENTS);
	}

	public int getTotalClientsOnline() {
		return getInt(ServerInstanceProperty.VIRTUALSERVERS_TOTAL_CLIENTS_ONLINE);
	}

	public int getTotalChannels() {
		return getInt(ServerInstanceProperty.VIRTUALSERVERS_TOTAL_CHANNELS_ONLINE);
	}

	public long getFileTransferBandwidthSent() {
		return getLong(ServerInstanceProperty.CONNECTION_FILETRANSFER_BANDWIDTH_SENT);
	}

	public long getFileTransferBandwidthReceived() {
		return getLong(ServerInstanceProperty.CONNECTION_FILETRANSFER_BANDWIDTH_RECEIVED);
	}

	public long getFileTransferBytesSent() {
		return getLong(ServerInstanceProperty.CONNECTION_FILETRANSFER_BYTES_SENT_TOTAL);
	}

	public long getFileTransferBytesReceived() {
		return getLong(ServerInstanceProperty.CONNECTION_FILETRANSFER_BYTES_RECEIVED_TOTAL);
	}

	public long getPacketsSentTotal() {
		return getLong(ServerInstanceProperty.CONNECTION_PACKETS_SENT_TOTAL);
	}

	public long getBytesSentTotal() {
		return getLong(ServerInstanceProperty.CONNECTION_BYTES_SENT_TOTAL);
	}

	public long getPacketsReceivedTotal() {
		return getLong(ServerInstanceProperty.CONNECTION_PACKETS_RECEIVED_TOTAL);
	}

	public long getBytesReceivedTotal() {
		return getLong(ServerInstanceProperty.CONNECTION_BYTES_RECEIVED_TOTAL);
	}

	public long getBandwidthSentLastSecond() {
		return getLong(ServerInstanceProperty.CONNECTION_BANDWIDTH_SENT_LAST_SECOND_TOTAL);
	}

	public long getBandwidthSentLastMinute() {
		return getLong(ServerInstanceProperty.CONNECTION_BANDWIDTH_SENT_LAST_MINUTE_TOTAL);
	}

	public long getBandwidthReceivedLastSecond() {
		return getLong(ServerInstanceProperty.CONNECTION_BANDWIDTH_RECEIVED_LAST_SECOND_TOTAL);
	}

	public long getBandwidthReceivedLastMinute() {
		return getLong(ServerInstanceProperty.CONNECTION_BANDWIDTH_RECEIVED_LAST_MINUTE_TOTAL);
	}

}

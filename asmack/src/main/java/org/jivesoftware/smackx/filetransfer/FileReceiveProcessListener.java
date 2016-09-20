package org.jivesoftware.smackx.filetransfer;

public abstract class FileReceiveProcessListener {
	public abstract void processReceived(Long receivedByte, Long fileSize);
}

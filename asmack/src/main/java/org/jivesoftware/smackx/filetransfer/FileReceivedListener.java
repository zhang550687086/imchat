package org.jivesoftware.smackx.filetransfer;

import java.io.File;

/**
 *
 * */
public abstract class FileReceivedListener {

	public abstract void processFile(File file);

}

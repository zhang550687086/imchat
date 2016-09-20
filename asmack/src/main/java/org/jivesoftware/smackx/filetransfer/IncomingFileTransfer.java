/**
 * $RCSfile$
 * $Revision: $
 * $Date: $
 *
 * Copyright 2003-2006 Jive Software.
 *
 * All rights reserved. Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jivesoftware.smackx.filetransfer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jivesoftware.smack.XMPPException;

import android.util.Log;

/**
 * An incoming file transfer is created when the
 * {@link FileTransferManager#createIncomingFileTransfer(FileTransferRequest)}
 * method is invoked. It is a file being sent to the local user from another
 * user on the jabber network. There are two stages of the file transfer to be
 * concerned with and they can be handled in different ways depending upon the
 * method that is invoked on this class.
 * <p/>
 * The first way that a file is recieved is by calling the
 * {@link #recieveFile()} method. This method, negotiates the appropriate stream
 * method and then returns the <b><i>InputStream</b></i> to read the file
 * data from.
 * <p/>
 * The second way that a file can be recieved through this class is by invoking
 * the {@link #recieveFile(File)} method. This method returns immediatly and
 * takes as its parameter a file on the local file system where the file
 * recieved from the transfer will be put.
 *
 * @author Alexander Wenckus
 */
public class IncomingFileTransfer extends FileTransfer {

    private FileTransferRequest recieveRequest;

    private InputStream inputStream;
    private List<FileReceivedListener> fileReceivedListeners;
    
    protected IncomingFileTransfer(FileTransferRequest request,
            FileTransferNegotiator transferNegotiator) {
        super(request.getRequestor(), request.getStreamID(), transferNegotiator);
        this.recieveRequest = request;
    }

    /**
     * Negotiates the stream method to transfer the file over and then returns
     * the negotiated stream.
     *
     * @return The negotiated InputStream from which to read the data.
     * @throws XMPPException If there is an error in the negotiation process an exception
     *                       is thrown.
     */
    public InputStream recieveFile() throws XMPPException {
        if (inputStream != null) {
            throw new IllegalStateException("Transfer already negotiated!");
        }

        try {
            inputStream = negotiateStream();
        }
        catch (XMPPException e) {
            setException(e);
            throw e;
        }

        return inputStream;
    }

    /**
     * This method negotitates the stream and then transfer's the file over the
     * negotiated stream. The transfered file will be saved at the provided
     * location.
     * <p/>
     * This method will return immedialtly, file transfer progress can be
     * monitored through several methods:
     * <p/>
     * <UL>
     * <LI>{@link FileTransfer#getStatus()}
     * <LI>{@link FileTransfer#getProgress()}
     * <LI>{@link FileTransfer#isDone()}
     * </UL>
     *
     * @param file The location to save the file.
     * @throws XMPPException            when the file transfer fails
     * @throws IllegalArgumentException This exception is thrown when the the provided file is
     *                                  either null, or cannot be written to.
     */
    public void recieveFile(final File file) throws XMPPException {
        if (file != null) {
            if (!file.exists()) {
                try {
                    file.createNewFile();
                }
                catch (IOException e) {
                    throw new XMPPException(
                            "Could not create file to write too", e);
                }
            }
            if (!file.canWrite()) {
                throw new IllegalArgumentException("Cannot write to provided file");
            }
        }
        else {
            throw new IllegalArgumentException("File cannot be null");
        }

        Thread transferThread = new Thread(new Runnable() {
            public void run() {
                try {
                    inputStream = negotiateStream();
                }
                catch (XMPPException e) {
                    handleXMPPException(e);
                    return;
                }

                OutputStream outputStream = null;
                try {
                    outputStream = new FileOutputStream(file);
                    setStatus(Status.in_progress);
                    writeToStream(inputStream, outputStream);
                    fireReceivedFile(file);
                    Log.d("FileSend","Received Finished.Success!");
                }
                catch (XMPPException e) {
                    setStatus(Status.error);
                    setError(Error.stream);
                    setException(e);
                }
                catch (FileNotFoundException e) {
                    setStatus(Status.error);
                    setError(Error.bad_file);
                    setException(e);
                }

                if (getStatus().equals(Status.in_progress)) {
                    setStatus(Status.complete);
                }
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    }
                    catch (Throwable io) {
                        /* Ignore */
                    }
                }
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    }
                    catch (Throwable io) {
                        /* Ignore */
                    }
                }
            }
        }, "File Transfer " + streamID);
        transferThread.start();
    }

    private void handleXMPPException(XMPPException e) {
        setStatus(FileTransfer.Status.error);
        setException(e);
    }

    private InputStream negotiateStream() throws XMPPException {
        setStatus(Status.negotiating_transfer);
        final StreamNegotiator streamNegotiator = negotiator
                .selectStreamNegotiator(recieveRequest);
        setStatus(Status.negotiating_stream);
        FutureTask<InputStream> streamNegotiatorTask = new FutureTask<InputStream>(
                new Callable<InputStream>() {

                    public InputStream call() throws Exception {
                        return streamNegotiator
                                .createIncomingStream(recieveRequest.getStreamInitiation());
                    }
                });
        streamNegotiatorTask.run();
        InputStream inputStream;
        try {
            inputStream = streamNegotiatorTask.get(15, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            throw new XMPPException("Interruption while executing", e);
        }
        catch (ExecutionException e) {
            throw new XMPPException("Error in execution", e);
        }
        catch (TimeoutException e) {
            throw new XMPPException("Request timed out", e);
        }
        finally {
            streamNegotiatorTask.cancel(true);
        }
        setStatus(Status.negotiated);
        return inputStream;
    }

    public void cancel() {
        setStatus(Status.cancelled);
    }
    private void initFileReceivedListeners(){
    	fileReceivedListeners = new ArrayList<FileReceivedListener>();
    }
    public void addFileReceivedListener(FileReceivedListener fileReceivedListener){
    	if(fileReceivedListeners==null){
    		initFileReceivedListeners();
    	}
    	synchronized (this.fileReceivedListeners) {
    		fileReceivedListeners.add(fileReceivedListener);
		}
    }
    public void fireReceivedFile(File file){
    	if(fileReceivedListeners!=null){
	    	FileReceivedListener[] listeners = null;
			synchronized (this.fileReceivedListeners) {
				listeners = new FileReceivedListener[this.fileReceivedListeners.size()];
				this.fileReceivedListeners.toArray(listeners);
			}
	    	for (FileReceivedListener fileReceivedListener : fileReceivedListeners) {
				fileReceivedListener.processFile(file);
			}
    	}
    }
}

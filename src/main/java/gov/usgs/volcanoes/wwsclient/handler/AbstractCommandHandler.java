package gov.usgs.volcanoes.wwsclient.handler;

import java.io.IOException;
import java.util.concurrent.Semaphore;

/**
 * Receive and process response from winston.
 *
 * @author Tom Parker
 */
public abstract class AbstractCommandHandler {
	/**
	 * Semaphore indicating if a read has been completed. While a request is
	 * being processed, attempts to acquire the semaphore will block.
	 */
	protected final Semaphore sem;

	/**
	 * Constructor.
	 */
	public AbstractCommandHandler() {
		sem = new Semaphore(0);
	}
	
	/**
	 * Process response from winston.
	 * 
	 * @param msg received message
	 * @throws IOException when things go wrong
	 */
	public abstract void handle(Object msg) throws IOException;

	/**
	 * Block until the handler has received and processed server response.
	 * 
	 * @throws InterruptedException
	 *             when receives InterruptedException
	 */
	public void responseWait() throws InterruptedException {
		sem.acquire();
	}
}

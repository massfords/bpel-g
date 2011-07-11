package bpelg.jbi.exchange;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.jbi.messaging.DeliveryChannel;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.MessagingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import bpelg.jbi.BgContext;

/**
 * Receiver pulls messages from the NMS and submits them to ODE for further processing.
 * 
 * (essentially a copy of org.apache.ode.jbi.Receiver)
 */
public class BgReceiver implements Runnable {
    private static final Log sLog = LogFactory.getLog(BgReceiver.class);

    // default time to wait for MessageExchanges, in seconds
    private static final long ACCEPT_TIMEOUT = 1L;

    // default time to wait for the ExecutorService to shut down, in seconds
    private static final long THREADPOOL_SHUTDOWN_TIMEOUT = 10L;

    // default number of threads in the thread pool
    private static final int THREADPOOL_SIZE = 8;

    /** Receiver-Running Flag. */
    private AtomicBoolean mRunning = new AtomicBoolean(false);

    /** Receiver-Started Flag. */
    private AtomicBoolean mStarted = new AtomicBoolean(false);

    private Thread mThread;

    // thread pool for dispatching received messages
    private ExecutorService mExecutorService;

    /**
     * Constructor for creating instance of this class.
     * 
     * @param context
     *            for receiving environment parameters.
     */
    public BgReceiver() {
        mThread = new Thread(this);
        mExecutorService = Executors.newFixedThreadPool(THREADPOOL_SIZE);
    }

    /**
     * Start the receiver thread.
     */
    public void start() {
        if (mStarted.compareAndSet(false, true)) {
            mRunning.set(true);
            mThread.start();
        } else
            throw new IllegalStateException("Receiver cannot be restarted.");
    }

    /**
     * This is called to gracefully stop the Receiver thread. After shutting down the thread pool we wait for a maximum
     * of 10 seconds before forcefully canceling in-flight threads.
     */
    public void cease() {

        if (!mStarted.get())
            return;

        sLog.info("Receiver is ceasing.");

        if (mRunning.compareAndSet(true, false)) {
            try {
                // This should not take more ACCEPT_TIMEOUT seconds, we
                // give it three times as much time.
                mThread.join(3 * TimeUnit.SECONDS.toMillis(ACCEPT_TIMEOUT));

                // Odd, we should not be alive at this point.
                if (mThread.isAlive()) {
                    sLog.warn("Receiver thread is not dying gracefully; interrupting.");
                    mThread.interrupt();
                }

                // Try joining again.
                mThread.join(3 * TimeUnit.SECONDS.toMillis(ACCEPT_TIMEOUT));

                // If it's not dead yet, we got a problem we can't deal with.
                if (mThread.isAlive()) {
                    sLog.fatal("Receiver thread is not dying gracefully despite our insistence!.");
                }

                // In any case, next step is to shutdown the thread pool
                mExecutorService.shutdown();

                // make sure no outstanding threads are hanging around
                if (!mExecutorService.awaitTermination(THREADPOOL_SHUTDOWN_TIMEOUT, TimeUnit.SECONDS)) {
                    sLog.warn("Problem shutting down ExecutorService - trying harder.");
                    List<Runnable> outstanding = mExecutorService.shutdownNow();
                    if (outstanding != null && !outstanding.isEmpty()) {
                        sLog.warn("Cancelled " + outstanding.size() + " in-flight threads.");
                    }
                }
            } catch (InterruptedException ie) {
                sLog.warn("Interrupted during cease(): ", ie);
            }

            // just to be sure..
            mExecutorService.shutdown();
            sLog.info("Receiver ceased.");

            mExecutorService = null;
            mThread = null;
        }
    }

    /**
     * We periodically poll for input messages, blocking for 1 sec on the accept() call to receive messages. Depending
     * on runFlag status we either try to again poll again or exit.
     */
    public void run() {
        sLog.info("Receiver is executing.");

        DeliveryChannel channel = null;
        
        try {
            channel = BgContext.getInstance().getComponentContext().getDeliveryChannel();
            if (channel == null) {
                sLog.fatal("No Channel!");
                return;
            }
        } catch (MessagingException ex) {
            sLog.fatal("Error getting channel! ", ex);
            return;
        }

        while (mRunning.get()) {
            final MessageExchange messageExchange;
            try {
                messageExchange = channel.accept(TimeUnit.SECONDS.toMillis(ACCEPT_TIMEOUT));
                if (messageExchange != null) {
                    if (sLog.isTraceEnabled()) {
                        sLog.trace("Got JBI message for endpoint: " + messageExchange.getEndpoint().getEndpointName());
                    }

                    // Even if we got a message exchange, we only run it
                    // if we have not been told to cease.
                    if (mRunning.get()) {
                        if (sLog.isTraceEnabled()) {
                            sLog.trace("Scheduling execution of " + messageExchange.getExchangeId());
                        }
                        mExecutorService.submit(new Runnable() {
                            public void run() {
                                try {
                                    BgContext.getInstance().getMessageExchangeProcessor().onJbiMessageExchange(messageExchange);
                                } catch (Throwable t) {
                                    sLog.error("Error processing JBI message.", t);
                                }
                            }
                        });
                    } else {
                        sLog.warn("Skipping processing of message exchange " + messageExchange.getExchangeId()
                                + "; component no longer active.");
                    }
                }
            } catch (MessagingException mex) {
                if (mRunning.get())
                    sLog.warn("Receiver exiting due to MessagingException:", mex);
                else
                    sLog.info("Receiver finished.");
                break;
            } catch (Exception ex) {
                if (!mRunning.get()) {
                    sLog.info("Receiver finished.");
                    break;
                }
                sLog.warn("Caught unexpected Exception: ", ex);
                return;
            }
        }

        sLog.info("Receiver finished.");
    }
}

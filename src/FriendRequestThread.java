import java.util.concurrent.Semaphore;

/**
 * FriendRequestThread models the asynchronous sending of a friend request
 * between two {@link UniversityStudent} instances.
 * Implements {@code Runnable} for execution in a thread pool.
 */
public class FriendRequestThread implements Runnable {
    /** The student initiating the friend request */
    private UniversityStudent sender;
    /** The student receiving the friend request */
    private UniversityStudent receiver;
    /** Semaphore for thread-safe friend request output */
    private static final Semaphore sem = new Semaphore(1);

    /**
     * Constructs a friend request task between two students.
     *
     * @param sender   the student initiating the friend request
     * @param receiver the student receiving the friend request
     */
    public FriendRequestThread(UniversityStudent sender, UniversityStudent receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }

    /**
     * Performs the friend request operation.
     */
    @Override
    public void run() {
        try {
            sem.acquire();
            System.out.println("FriendRequest (Thread-safe): " + sender.name + " sent a friend request to " + receiver.name);
            receiver.addFriendRequest(sender.name);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("FriendRequest interrupted: " + e.getMessage());
        } finally {
            sem.release();
        }
    }
}

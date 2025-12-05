import java.util.concurrent.Semaphore;

/**
 * ChatThread models a chat interaction between two UniversityStudent instances.
 * Implements {@code Runnable} for execution in a thread pool.
 */
public class ChatThread implements Runnable {
    /** The student sending the chat message */
    private UniversityStudent sender;
    /** The student receiving the chat message */
    private UniversityStudent receiver;
    /** The content of the chat message */
    String message;
    /** Semaphore for thread-safe message output */
    private static final Semaphore sem = new Semaphore(1);
    /**
     * Constructs a ChatThread for a single chat message between two students.
     *
     * @param sender   the student sending the message
     * @param receiver the student receiving the message
     * @param message  the chat message text
     */
    public ChatThread(UniversityStudent sender, UniversityStudent receiver, String message) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
    }

    /**
     * Executes the chat action. 
     */
    @Override
    public void run() {
        try {
            sem.acquire();
            System.out.println("Chat (Thread-safe): " + sender.name + " to " + receiver.name + ": " + message);
            sender.addChatMessage("To " + receiver.name + ": " + message);
            receiver.addChatMessage("From " + sender.name + ": " + message);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Chat interrupted: " + e.getMessage());
        } finally {
            sem.release();
        }    }
}

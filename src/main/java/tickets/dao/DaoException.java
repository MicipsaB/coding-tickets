package tickets.dao;

public class DaoException extends Exception {

    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }

    // ✅ Constructeur simple sans cause
    public DaoException(String message) {
        super(message);
    }
}

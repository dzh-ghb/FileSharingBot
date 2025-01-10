package dzh.its.exceptions;

public class UploadFileException extends RuntimeException { //кастомное исключение с пробросом параметров в родительские конструкторы
    public UploadFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public UploadFileException(String message) {
        super(message);
    }

    public UploadFileException(Throwable cause) {
        super(cause);
    }
}
package pass.core.service;

/*
 * This is an unchecked exception thrown by most classes in the service layer.
 */
public class ServiceException extends RuntimeException
{

    private final ErrorCode errorCode;

    public ServiceException(ErrorCode errorCode)
    {
        super(errorCode.toString());
        this.errorCode = errorCode;
    }

    public ServiceException(ErrorCode errorCode, Throwable cause)
    {
        super(errorCode.toString(), cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode()
    {
        return errorCode;
    }
}

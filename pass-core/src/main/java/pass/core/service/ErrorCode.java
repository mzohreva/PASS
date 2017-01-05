package pass.core.service;

public enum ErrorCode
{
    NOT_FOUND(404, "Resource not found"),
    NOT_ALLOWED(1279, "Operation not allowed"),
    UNKNOWN_ERROR(2203, "Unkown error"),
    UNDER_MAINTENANCE(2281, "The website is currently under maintenance"),
    UNLISTED(3217, "Could not find the specified username / student id "
                   + "combination in class list. Have you entered the "
                   + "requested information correctly? Your information might "
                   + "be missing from the list, contact the webmaster if this "
                   + "is the case."),
    PASSWORD_TOO_SHORT(4253, "Password must be at least 6 characters long"),
    USER_ALREADY_EXISTS(4423, "User already exists"),
    USER_NOT_FOUND(9689, "User not found"),
    USER_NOT_SPECIFIED(9941, "User not specified"),
    NO_FILE(11213, "No file was uploaded"),
    DEADLINE_PASSED(19937, "Submission deadline for this project has passed"),
    TOO_MANY_FILES(21701, "Too many files uploaded"),
    FILES_TOO_LARGE(23209, "The total size of uploaded files exceeds the limit"),
    ANOTHER_EVALUATING(44497, "New submission is not allowed while a previous "
                              + "submission for the same project is being "
                              + "evaluated"),
    INVALID_VCODE(86243, "Invalid verification code"),
    DELETE_FAILED(110503, "Failed to delete object, most likely due to extant "
                          + "related objects");

    private final int code;
    private final String description;

    private ErrorCode(int code, String description)
    {
        this.code = code;
        this.description = description;
    }

    public int getCode()
    {
        return code;
    }

    public String getDescription()
    {
        return description;
    }
}

package pass.core.service;

import java.util.Date;

public class AuthenticatedUser
{

    public final String username;
    public final String firstName;
    public final String lastName;
    public final String studentId;
    private Date lastAccess;
    private String lastAccessedView;

    public AuthenticatedUser(String username,
                             String firstName,
                             String lastName,
                             String studentId)
    {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.studentId = studentId;
    }

    public boolean isAdmin()
    {
        return username.equals("admin");
    }

    public String getUsername()
    {
        return username;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public String getStudentId()
    {
        return studentId;
    }

    public Date getLastAccess()
    {
        return lastAccess;
    }

    public String getLastAccessedView()
    {
        return lastAccessedView;
    }

    public void updateLastAccess(String view)
    {
        this.lastAccess = new Date();
        this.lastAccessedView = view;
    }
}

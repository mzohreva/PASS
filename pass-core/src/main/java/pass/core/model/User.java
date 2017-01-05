package pass.core.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table (name = "users")
public class User implements Serializable
{

    @Id
    private String username;

    @Column
    private byte[] password;

    @Column
    private String salt;

    @Column (nullable = false)
    private String studentId;

    @Column (nullable = false)
    private String firstname;

    @Column (nullable = false)
    private String lastname;

    @Column (nullable = false)
    private String email;

    @Column
    private boolean verified;

    public User()
    {
    }

    public User(String username, byte[] password, String salt,
                String studentId, String firstname, String lastname,
                String email, boolean verified)
    {
        this.username = username;
        this.password = password;
        this.salt = salt;
        this.studentId = studentId;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.verified = verified;
    }

    public String getUsername()
    {
        return this.username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public byte[] getPassword()
    {
        return this.password;
    }

    public void setPassword(byte[] password)
    {
        this.password = password;
    }

    public String getSalt()
    {
        return this.salt;
    }

    public void setSalt(String salt)
    {
        this.salt = salt;
    }

    public String getStudentId()
    {
        return this.studentId;
    }

    public void setStudentId(String studentId)
    {
        this.studentId = studentId;
    }

    public String getFirstname()
    {
        return this.firstname;
    }

    public void setFirstname(String firstname)
    {
        this.firstname = firstname;
    }

    public String getLastname()
    {
        return this.lastname;
    }

    public void setLastname(String lastname)
    {
        this.lastname = lastname;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public boolean isVerified()
    {
        return verified;
    }

    public void setVerified(boolean verified)
    {
        this.verified = verified;
    }
}

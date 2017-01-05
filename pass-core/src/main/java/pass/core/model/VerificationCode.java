package pass.core.model;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import pass.core.common.Config;
import pass.core.common.Util;

@Entity
@Table (name = "verification_codes")
public class VerificationCode implements Serializable
{

    public enum Reason
    {
        ACCOUNT_CREATION,
        PASSWORD_RESET
    }

    @Id
    @Column (columnDefinition = "binary(16)")
    private UUID code;

    @Column
    private Reason reason;

    @ManyToOne
    private User user;

    @Column
    @Temporal (javax.persistence.TemporalType.TIMESTAMP)
    private Date creationDate;

    public VerificationCode()
    {
    }

    public static VerificationCode generate(Reason reason, User user)
    {
        VerificationCode vc = new VerificationCode();
        vc.code = UUID.randomUUID();
        vc.reason = reason;
        vc.user = user;
        vc.creationDate = new Date();
        return vc;
    }

    public UUID getCode()
    {
        return code;
    }

    public String getLink()
    {
        final String serverUrl = Config.getInstance().getServerUrl();
        return serverUrl + "verify.do?code=" + code.toString();
    }

    public void setCode(UUID code)
    {
        this.code = code;
    }

    public Reason getReason()
    {
        return reason;
    }

    public void setReason(Reason reason)
    {
        this.reason = reason;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public Date getCreationDate()
    {
        return creationDate;
    }

    public String getAge()
    {
        return Util.dateDifferenceHumanReadable(
                creationDate,
                new Date(),
                Util.DateDifferencePrecision.SECONDS,
                true);
    }

    public void setCreationDate(Date creationDate)
    {
        this.creationDate = creationDate;
    }

    @Override
    public String toString()
    {
        return "[" + this.code.toString() + ", "
               + (this.user != null ? this.user.getUsername() : "?") + ", "
               + this.reason + "]";
    }
}

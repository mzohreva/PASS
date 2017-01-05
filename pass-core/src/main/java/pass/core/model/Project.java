package pass.core.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;

@Entity
@Table (name = "projects")
public class Project implements Serializable
{

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String title;

    @Column
    @Temporal (javax.persistence.TemporalType.DATE)
    private Date assignDate;

    @Column
    @Temporal (javax.persistence.TemporalType.TIMESTAMP)
    private Date dueDate;

    @Column
    private Integer gracePeriodHours;

    @Column
    private boolean visible;

    @Lob
    private String submissionInstructions;

    @OneToMany (mappedBy = "project")
    private Set<Submission> submissions = new HashSet<>(0);

    public Project()
    {
    }

    public Project(String title, Date assignDate, Date dueDate,
                   Integer gracePeriodHours, boolean visible,
                   String submissionInstructions, Set<Submission> submissions)
    {
        this.title = title;
        this.assignDate = assignDate;
        this.dueDate = dueDate;
        this.gracePeriodHours = gracePeriodHours;
        this.visible = visible;
        this.submissionInstructions = submissionInstructions;
        this.submissions = submissions;
    }

    public Integer getId()
    {
        return this.id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getTitle()
    {
        return this.title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public Date getAssignDate()
    {
        return this.assignDate;
    }

    public void setAssignDate(Date assignDate)
    {
        this.assignDate = assignDate;
    }

    public Date getDueDate()
    {
        return this.dueDate;
    }

    public void setDueDate(Date dueDate)
    {
        this.dueDate = dueDate;
    }

    public Integer getGracePeriodHours()
    {
        return gracePeriodHours;
    }

    public String getGracePeriodHumanReadable()
    {
        int days = gracePeriodHours / 24;
        int hours = gracePeriodHours % 24;
        String r = "";
        if (days > 0) {
            r += days + " days";
        }
        if (days > 0 && hours > 0) {
            r += " and ";
        }
        if (hours > 0) {
            r += hours + " hours";
        }
        if (days == 0 && hours == 0) {
            r = "0";
        }
        return r;
    }

    public void setGracePeriodHours(Integer gracePeriodHours)
    {
        this.gracePeriodHours = gracePeriodHours;
    }

    public boolean isDeadlinePassed()
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dueDate);
        cal.add(Calendar.HOUR, gracePeriodHours);
        Date deadline = cal.getTime();
        Date now = new Date();
        return now.after(deadline);
    }

    public boolean isVisible()
    {
        return this.visible;
    }

    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }

    public String getSubmissionInstructions()
    {
        return (submissionInstructions == null ? "" : submissionInstructions);
    }

    public void setSubmissionInstructions(String submissionInstructions)
    {
        this.submissionInstructions = submissionInstructions;
    }

    public Set<Submission> getSubmissions()
    {
        return this.submissions;
    }

    public Set<Submission> getLastSubmissionsOfUsers()
    {
        Map<String, Submission> mapUserSub = new HashMap<>();
        submissions.forEach((sub) -> {
            String username = sub.getUser().getUsername();
            if (mapUserSub.containsKey(username)) {
                Submission other = mapUserSub.get(username);
                if (sub.getSubmissionDate().after(other.getSubmissionDate())) {
                    mapUserSub.put(username, sub);
                }
            }
            else {
                mapUserSub.put(username, sub);
            }
        });
        Set<Submission> result = new HashSet<>();
        mapUserSub.values().forEach((s) -> {
            result.add(s);
        });
        return result;
    }

    public int getNumberOfUsersWhoSubmitted()
    {
        Set<String> usernames = new HashSet<>();
        submissions.stream()
                .map((sub) -> sub.getUser().getUsername())
                .forEachOrdered((u) -> {
                    usernames.add(u);
                });
        return usernames.size();
    }

    public void setSubmissions(Set<Submission> submissions)
    {
        this.submissions = submissions;
    }
}

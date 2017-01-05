package pass.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import pass.core.common.Tools;
import pass.core.service.CompileOption;

@Entity
@Table (name = "submissions")
public class Submission implements Serializable
{

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Project project;

    @Column
    @Temporal (javax.persistence.TemporalType.TIMESTAMP)
    private Date submissionDate;

    @Column
    private boolean compileSuccessful;

    @Lob
    private String compileMessage;

    @Lob
    private String testResult;

    @Column
    private String compileOptions;

    public Submission()
    {
    }

    public Submission(User user, Project project, Date submissionDate)
    {
        this.user = user;
        this.project = project;
        this.submissionDate = submissionDate;
    }

    public Integer getId()
    {
        return this.id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public User getUser()
    {
        return this.user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public Project getProject()
    {
        return this.project;
    }

    public void setProject(Project project)
    {
        this.project = project;
    }

    public Date getSubmissionDate()
    {
        return this.submissionDate;
    }

    public boolean isLate()
    {
        return this.submissionDate.after(project.getDueDate());
    }

    public int getDaysLate()
    {
        if (this.submissionDate.before(project.getDueDate())) {
            return 0;
        }
        else {
            // Find difference in milliseconds
            long diff = this.submissionDate.getTime() - project.getDueDate().getTime();
            int days = (int) Math.ceil(diff * 1.0 / (1000 * 3600 * 24));
            return days;
        }
    }

    public String getDaysLateHumanReadable()
    {
        int daysLate = getDaysLate();
        switch (daysLate) {
            case 0:
                return "on time";
            case 1:
                return "1 day late";
            default:
                return daysLate + " days late";
        }
    }

    public void setSubmissionDate(Date submissionDate)
    {
        this.submissionDate = submissionDate;
    }

    public boolean isCompileSuccessful()
    {
        return compileSuccessful;
    }

    public String getCompileMessage()
    {
        return (this.compileMessage == null ? "" : this.compileMessage);
    }

    public String getCompileMessageHtmlEscaped()
    {
        return Tools.escapeHtml(getCompileMessage());
    }

    public void setCompileResults(String compileMessage, boolean compileSuccessful)
    {
        this.compileMessage = compileMessage;
        this.compileSuccessful = compileSuccessful;
    }

    public String getTestResult()
    {
        return (this.testResult == null ? " - " : this.testResult);
    }

    public void setTestResult(String testResult)
    {
        this.testResult = testResult;
    }

    public String getCompileOptions()
    {
        return compileOptions;
    }

    public List<CompileOption> getCompileOptionsList()
    {
        List<CompileOption> options = new ArrayList<>();
        for (String optionId : this.compileOptions.split(" ")) {
            CompileOption opt = CompileOption.getOptionById(optionId);
            if (opt != null) {
                options.add(opt);
            }
        }
        return options;
    }

    public String getCompileOptionsHumanReadable()
    {
        List<CompileOption> options = getCompileOptionsList();
        StringBuilder sb = new StringBuilder();
        options.forEach((opt) -> {
            sb.append(opt.getArgs()).append(" ");
        });
        return sb.toString();
    }

    public void setCompileOptions(String compileOptions)
    {
        this.compileOptions = compileOptions;
    }

    public void setCompileOptions(List<CompileOption> compileOptions)
    {
        StringBuilder sbOptions = new StringBuilder();
        compileOptions.forEach((opt) -> {
            sbOptions.append(opt.getId()).append(" ");
        });
        this.compileOptions = sbOptions.toString();
    }
}

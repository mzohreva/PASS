package pass.core.model;

import java.util.Date;

public class UserProject
{

    private String username;
    private Integer projectId;
    private String title;
    private Date assignDate;
    private Date dueDate;
    private boolean visible;
    private Long submissionCount;

    public UserProject(String username, int projectId, String title,
                       Date assignDate, Date dueDate, boolean visible,
                       long submissionCount)
    {
        this.username = username;
        this.projectId = projectId;
        this.title = title;
        this.assignDate = assignDate;
        this.dueDate = dueDate;
        this.visible = visible;
        this.submissionCount = submissionCount;
    }

    // <editor-fold defaultstate="collapsed" desc=" Getters & Setters ">
    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public Integer getProjectId()
    {
        return projectId;
    }

    public void setProjectId(Integer projectId)
    {
        this.projectId = projectId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public Date getAssignDate()
    {
        return assignDate;
    }

    public void setAssignDate(Date assignDate)
    {
        this.assignDate = assignDate;
    }

    public Date getDueDate()
    {
        return dueDate;
    }

    public void setDueDate(Date dueDate)
    {
        this.dueDate = dueDate;
    }

    public boolean isVisible()
    {
        return visible;
    }

    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }

    public Long getSubmissionCount()
    {
        return submissionCount;
    }

    public void setSubmissionCount(Long submissionCount)
    {
        this.submissionCount = submissionCount;
    }
    // </editor-fold>
}

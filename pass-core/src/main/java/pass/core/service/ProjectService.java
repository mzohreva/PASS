package pass.core.service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import pass.core.filesystem.ProjectFiles;
import pass.core.hibernate.HibernateSession;
import pass.core.hibernate.ProjectsRepository;
import pass.core.model.Project;

public class ProjectService
{

    private static final Logger LOGGER = Logger.getLogger(ProjectService.class.getName());

    public ProjectService()
    {
    }

    public boolean newProject(String title,
                              Date assigned,
                              Date due,
                              int gracePeriodHours,
                              boolean visible,
                              String submissionInstructions,
                              String compileScript,
                              String testScript,
                              UploadedFile gradingTests,
                              List<UploadedFile> attachments,
                              List<UploadedFile> auxiliaryFiles)
    {
        try (HibernateSession hs = new HibernateSession()) {
            ProjectsRepository repo = new ProjectsRepository(hs);
            int id = repo.addProject(title,
                                     assigned,
                                     due,
                                     gracePeriodHours,
                                     visible,
                                     submissionInstructions);
            ProjectFiles pfiles = new ProjectFiles(id);
            // Save grading tests
            if (!gradingTests.getSubmittedFileName().equals("")) {
                pfiles.saveGradingTests(gradingTests.getSubmittedFileName(),
                                        gradingTests.getInputStream());
            }
            // Save attachments
            for (UploadedFile f : attachments) {
                pfiles.saveAttachment(f.getSubmittedFileName(),
                                      f.getInputStream());
            }
            // Save auxiliary files
            for (UploadedFile f : auxiliaryFiles) {
                pfiles.saveAuxiliaryFile(f.getSubmittedFileName(),
                                         f.getInputStream());
            }
            // Set compile and test scripts
            pfiles.setCompileScript(compileScript);
            pfiles.setTestScript(testScript);
            return true;
        }
        catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean editProject(int projectId,
                               String title,
                               Date assigned,
                               Date due,
                               int gracePeriodHours,
                               boolean visible,
                               String submissionInstructions,
                               String compileScript,
                               String testScript,
                               UploadedFile gradingTests,
                               List<String> attachmentsToRemove,
                               List<UploadedFile> attachmentsToAdd,
                               List<String> auxiliaryFilesToRemove,
                               List<UploadedFile> auxiliaryFilesToAdd)
    {
        try (HibernateSession hs = new HibernateSession()) {
            ProjectsRepository repo = new ProjectsRepository(hs);
            Project project = repo.findById(projectId);
            project.setTitle(title);
            project.setAssignDate(assigned);
            project.setDueDate(due);
            project.setGracePeriodHours(gracePeriodHours);
            project.setVisible(visible);
            project.setSubmissionInstructions(submissionInstructions);
            repo.updateProject(project);
            ProjectFiles pfiles = new ProjectFiles(projectId);
            // Replace grading tests
            if (!gradingTests.getSubmittedFileName().equals("")) {
                pfiles.replaceGradingTests(gradingTests.getSubmittedFileName(),
                                           gradingTests.getInputStream());
            }
            // Remove attachments
            for (String a : attachmentsToRemove) {
                pfiles.removeAttachment(a);
            }
            // Add attachments
            for (UploadedFile p : attachmentsToAdd) {
                pfiles.saveAttachment(p.getSubmittedFileName(),
                                      p.getInputStream());
            }
            // Remove auxiliary files
            for (String a : auxiliaryFilesToRemove) {
                pfiles.removeAuxiliaryFile(a);
            }
            // Add auxiliary files
            for (UploadedFile p : auxiliaryFilesToAdd) {
                pfiles.saveAuxiliaryFile(p.getSubmittedFileName(),
                                         p.getInputStream());
            }
            // Set compile and test scripts
            pfiles.setCompileScript(compileScript);
            pfiles.setTestScript(testScript);
            return true;
        }
        catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public void deleteProject(int projectId) throws ServiceException
    {
        try (HibernateSession hs = new HibernateSession()) {
            ProjectsRepository repo = new ProjectsRepository(hs);
            Project project = repo.findById(projectId);
            if (project == null) {
                throw new ServiceException(ErrorCode.NOT_FOUND);
            }
            if (repo.deleteProject(project)) {
                ProjectFiles pfiles = new ProjectFiles(projectId);
                if (pfiles.deleteFiles()) {
                    LOGGER.log(Level.INFO,
                               "Project {0} deleted successfully",
                               projectId);
                }
                else {
                    throw new ServiceException(ErrorCode.DELETE_FAILED);
                }
            }
            else {
                throw new ServiceException(ErrorCode.DELETE_FAILED);
            }
        }
    }

    public void toggleVisiblity(int projectId) throws ServiceException
    {
        try (HibernateSession hs = new HibernateSession()) {
            ProjectsRepository repo = new ProjectsRepository(hs);
            Project project = repo.findById(projectId);
            if (project == null) {
                throw new ServiceException(ErrorCode.NOT_FOUND);
            }
            project.setVisible(!project.isVisible());
            repo.updateProject(project);
        }
    }

    public void addDaysToDueDate(int projectId, int days) throws ServiceException
    {
        try (HibernateSession hs = new HibernateSession()) {
            ProjectsRepository repo = new ProjectsRepository(hs);
            Project project = repo.findById(projectId);
            if (project == null) {
                throw new ServiceException(ErrorCode.NOT_FOUND);
            }
            long d = project.getDueDate().getTime()
                     + days * 24 * 3600 * 1000;
            project.setDueDate(new Date(d));
            repo.updateProject(project);
        }
    }
}

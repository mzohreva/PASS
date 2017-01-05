<#assign page_title="Assignments">

<#assign current_page="manage_projects">

<#include "/macros.ftl">

<#assign page_content>
<form name="manageProjects" method="POST">
    <input type="hidden" name="action" />
    <input type="hidden" name="project" />
    <input type="hidden" name="amount" />
</form>

<table class="table table-striped">
    <thead>
        <tr>
            <th class="text-center">Id</th>
            <th class="">Title</th>
            <th class="">Assigned</th>
            <th class="">Due</th>
            <th class="">Grace Period</th>
            <th class="text-center">Visible</th>
            <th class="text-center">Submissions</th>
            <th class="text-center">Action</th>
        </tr>
    </thead>
    <tbody>
    <#list projectsList as p>
        <tr>
            <td class="text-center">${p.id?c}</td>
            <td>
                <a href="${WebConfig.view("manage_submissions")}?project=${p.id?c}"
                   title="View student submissions">
                    ${p.title}
                </a>
            </td>
            <td>${p.assignDate}</td>
            <td class="clearfix">
                <div class="pull-left">${p.dueDate}</div>
                <div class="pull-right">
                <button type="button"
                        class="btn btn-default btn-xs"
                        onclick="AddDaysToDueDate(${p.id?c}, 1)">
                    +1 day
                </button>
                <button type="button"
                        class="btn btn-default btn-xs"
                        onclick="AddDaysToDueDate(${p.id?c}, -1)">
                    -1 day
                </button>
                </div>
            </td>
            <td>
                ${p.gracePeriodHumanReadable}
            </td>
            <td class="text-center">
                <button type="button"
                        class="btn btn-default btn-xs"
                        onclick="ToggleVisibility(${p.id?c})"
                        title="Toggle visibility">
                    <span class="glyphicon glyphicon-eye-${p.visible?then('open', 'close')}"></span>
                </button>
            </td>
            <td class="text-center">
                <a href="${WebConfig.view("manage_submissions")}?project=${p.id?c}&show_all=1">
                    ${p.submissions?size}
                </a>
                (<a href="${WebConfig.view("manage_submissions")}?project=${p.id?c}">
                    ${p.numberOfUsersWhoSubmitted} users
                 </a>)
            </td>
            <td class="text-center">
                <button type="button"
                        class="btn btn-default btn-xs"
                        onclick="DeleteProject(${p.id?c})"
                        title="Delete assignment">
                    <span class="glyphicon glyphicon-trash"
                          aria-hidden="true"></span>
                </button>
                <a href="${WebConfig.view("edit_project")}?project=${p.id?c}"
                   class="btn btn-default btn-xs"
                   title="Edit assignment">
                    <span class="glyphicon glyphicon-edit"></span>
                </a>
                <button type="button"
                        class="btn btn-default btn-xs"
                        onclick="ZipSubmissions(${p.id?c})"
                        title="Zip submissions">
                    <span class="glyphicon glyphicon-floppy-disk"
                          aria-hidden="true"></span>
                </button>
            </td>
        </tr>
    <#else>
        <tr>
            <td colspan="8">nothing yet</td>
        </tr>
    </#list>
    </tbody>
</table>

<@panel title="New assignment" class="success">
<form name="newProjectForm" method="POST"
      action="${WebConfig.view("new_project")}" enctype="multipart/form-data"
      class="form-horizontal" onsubmit="return ValidateForm();">

    <fieldset>
        <div class="form-group">
            <label class="control-label col-lg-2">Title</label>
            <div class="col-lg-9">
                <input type="text" size="30" name="title"
                       id="title" class="form-control"
                       placeholder="Assignment title" />
            </div>
        </div>
        <div class="form-group">
            <label class="control-label col-lg-2">Assigned</label>
            <div class="col-lg-9">
                <input type="text" size="12" name="assigned"
                       id="assigned" class="form-control"
                       placeholder="Assigned date" />
            </div>
        </div>
        <div class="form-group">
            <label class="control-label col-lg-2">Due</label>
            <div class="col-lg-9">
                <div class="row">
                    <div class="col-lg-8">
                    <input type="text" size="12" name="dueDate"
                           id="dueDate" class="form-control"
                           placeholder="Due date" />
                    </div>
                    <div class="col-lg-4">
                    <input type="text" size="4" maxlength="8" name="dueTime"
                           id="dueTime" value="23:59:59"
                           class="form-control" />
                    </div>
                </div>
            </div>
        </div>
        <div class="form-group">
            <label class="control-label col-lg-2">Grace period</label>
            <div class="col-lg-9">
                <div class="row">
                    <div class="col-sm-5">
                    <input type="number" min="0" name="gracePeriodDays"
                           id="gracePeriodDays" class="form-control"
                           value="5" />
                    </div>
                    <div class="col-sm-1">
                        <span class="help-block">days</span>
                    </div>
                    <div class="col-sm-5">
                    <input type="number" min="0" max="23" name="gracePeriodHours"
                           id="gracePeriodHours" class="form-control"
                           value="0" />
                    </div>
                    <div class="col-sm-1">
                        <span class="help-block">hours</span>
                    </div>
                </div>
            </div>
        </div>
        <div class="form-group">
            <div class="col-lg-offset-2 col-lg-9">
                <div class="checkbox">
                    <label>
                        <input type="checkbox"
                               name="visible"
                               id="visible"
                               value="true" /> Visible to students?
                    </label>
                </div>
            </div>
        </div>
        <div class="form-group">
            <label class="control-label col-lg-2">Attachments</label>
            <div class="col-lg-9">
                <input type="file"
                       name="attachments"
                       id="attachments" multiple />
                <span class="help-block">
                    Assignment material for students to download.
                </span>
            </div>
        </div>
        <div class="form-group">
            <label class="control-label col-lg-2">Auxiliary files</label>
            <div class="col-lg-9">
                <input type="file"
                       name="auxiliary"
                       id="auxiliary" multiple />
                <span class="help-block">
                    These files could be used by the compile script,
                    e.g. to link student's code against your code.
                </span>
            </div>
        </div>
        <div class="form-group">
            <label class="control-label col-lg-2">Grading tests</label>
            <div class="col-lg-9">
                <input type="file"
                       accept=".zip"
                       name="fileGradingTests"
                       id="fileGradingTests" />
                <span class="help-block">
                    A single zip file that will be unzipped on the server.
                    The test script can use these files.
                </span>
            </div>
        </div>
        <div class="form-group">
            <label class="control-label col-lg-2">Compile script</label>
            <div class="col-lg-9">
                <select name="selectCompileScript" class="form-control">
                    <#list allCompileScripts as s>
                        <option value="${s}">${s}</option>
                    </#list>
                </select>
            </div>
        </div>
        <div class="form-group">
            <label class="control-label col-lg-2">Test script</label>
            <div class="col-lg-9">
                <select name="selectTestScript" class="form-control">
                    <#list allTestScripts as s>
                        <option value="${s}">${s}</option>
                    </#list>
                </select>
            </div>
        </div>
        <div class="form-group">
            <label class="control-label col-lg-2">Instructions</label>
            <div class="col-lg-9">
                <textarea rows="10" class="form-control"
                          name="submissionInstructions"
                          id="submissionInstructions"
                          >${WebConfig.defaultSubmissionInstructions}</textarea>
            </div>
        </div>
        <div class="form-group">
            <div class="col-lg-offset-2 col-lg-9">
                <@error_list id="errorList" />
                <input type="submit" value="Add assignment"
                       class="btn btn-success" />
            </div>
        </div>
    </fieldset>
</form>
</@panel>
</#assign>

<#assign page_script>
<script type="text/javascript">
function submitManageProjectsForm(action, project, amount)
{
    document.forms["manageProjects"]["action"].value = action;
    document.forms["manageProjects"]["project"].value = project;
    document.forms["manageProjects"]["amount"].value = amount;
    document.forms["manageProjects"].submit();
}

function AddDaysToDueDate(project, amount)
{
    submitManageProjectsForm("add_due", project, amount);
}

function ToggleVisibility(project)
{
    submitManageProjectsForm("toggle_visible", project, 0);
}

function DeleteProject(project)
{
    if (confirm("Are you sure you want to delete this assignment?"))
    {
        submitManageProjectsForm("delete_project", project, 0);
    }
}

function ZipSubmissions(project)
{
    submitManageProjectsForm("zip_submissions", project, 0);
}

function ValidateForm()
{
    var title = document.forms["newProjectForm"]["title"].value;
    var assigned = document.forms["newProjectForm"]["assigned"].value;
    var dueDate = document.forms["newProjectForm"]["dueDate"].value;
    var dueTime = document.forms["newProjectForm"]["dueTime"].value;
    // Clear error messages
    clearErrors("errorList");
    // Now check for errors
    var errors = [];
    if (title.length < 3)
    {
        errors.push("Title must have at least 3 characters");
    }
    if (!isValidDate(assigned))
    {
        errors.push("Assigned date is not valid: " + assigned);
    }
    if (!isValidDate(dueDate))
    {
        errors.push("Due date is not valid: " + dueDate);
    }
    if (!isValidTime(dueTime))
    {
        errors.push("Due time is not valid: " + dueTime);
    }
    if (errors.length > 0)
        setErrors("errorList", errors);
    return errors.length === 0; // true if no error
}

$(function() {
    $("#assigned").datepicker();
    $("#assigned").datepicker("setDate", new Date());
    $("#dueDate").datepicker();
    $("#dueDate").datepicker("setDate", "+2w");
});
</script>
</#assign>

<#include "../base.ftl">

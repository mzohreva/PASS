<#assign page_title="Edit assignment">

<#assign current_page="edit_project_${project.id?c}">

<#include "/macros.ftl">

<#assign page_content>
<@panel title="Edit assignment <b>${project.title}</b>" class="danger">
<form name="editProjectForm" method="POST"
      action="${WebConfig.view("edit_project")}" enctype="multipart/form-data"
      class="form-horizontal" onsubmit="return ValidateForm();">
    <input type="hidden" name="projectId" id="projectId" value="${project.id?c}" />

    <fieldset>
        <div class="form-group">
            <label class="control-label col-lg-2">Title</label>
            <div class="col-lg-9">
                <input type="text" size="30" name="title"
                       id="title" class="form-control"
                       value="${project.title}" />
            </div>
        </div>
        <div class="form-group">
            <label class="control-label col-lg-2">Assigned</label>
            <div class="col-lg-9">
                <input type="text" size="12" name="assigned"
                       id="assigned" class="form-control" />
            </div>
        </div>
        <div class="form-group">
            <label class="control-label col-lg-2">Due</label>
            <div class="col-lg-9">
                <div class="row">
                    <div class="col-lg-8">
                        <input type="text" size="12" name="dueDate"
                               id="dueDate" class="form-control" />
                    </div>
                    <div class="col-lg-4">
                        <input type="text" size="4" maxlength="8"
                               name="dueTime" id="dueTime"
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
                           value="${(project.gracePeriodHours / 24)?int?c}" />
                    </div>
                    <div class="col-sm-1">
                        <span class="help-block">days</span>
                    </div>
                    <div class="col-sm-5">
                    <input type="number" min="0" max="23" name="gracePeriodHours"
                           id="gracePeriodHours" class="form-control"
                           value="${(project.gracePeriodHours % 24)?c}" />
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
                <#list attachments as f>
                <div class="checkbox">
                    <label>
                        <input type="checkbox"
                               name="attachmentsToRemove"
                               value="${f}"
                               title="Mark to remove attachment" />
                        <a href="${WebConfig.view("download")}?project=${project.id?c}&file=${f}">${f}</a>
                    </label>
                </div>
                <#else>
                    <span class="help-block">none</span>
                </#list>
            </div>
        </div>
        <div class="form-group">
            <label class="control-label col-lg-2">Attachments to add</label>
            <div class="col-lg-9">
                <input type="file"
                       name="attachmentsToAdd"
                       id="attachmentsToAdd" multiple />
            </div>
        </div>
        <div class="form-group">
            <label class="control-label col-lg-2">Auxiliary files</label>
            <div class="col-lg-9">
                <#list auxiliaryFiles as f>
                <div class="checkbox">
                    <label>
                        <input type="checkbox"
                               name="auxiliaryFilesToRemove"
                               value="${f}"
                               title="Mark to remove auxiliary file" />
                        ${f}
                    </label>
                </div>
                <#else>
                    <span class="help-block">none</span>
                </#list>
            </div>
        </div>
        <div class="form-group">
            <label class="control-label col-lg-2">Auxiliary files to add</label>
            <div class="col-lg-9">
                <input type="file"
                       name="auxiliaryFilesToAdd"
                       id="auxiliaryFilesToAdd" multiple />
            </div>
        </div>
        <div class="form-group">
            <label class="control-label col-lg-2">Grading tests</label>
            <div class="col-lg-9">
                <span class="help-block">${gradingTests?size?c} files</span>
            </div>
        </div>
        <div class="form-group">
            <label class="control-label col-lg-2">Replace grading tests</label>
            <div class="col-lg-9">
                <input type="file"
                       accept=".zip"
                       name="fileGradingTests"
                       id="fileGradingTests" />
            </div>
        </div>
        <div class="form-group">
            <label class="control-label col-lg-2">Compile script</label>
            <div class="col-lg-9">
                <select name="selectCompileScript" class="form-control">
                    <#list allCompileScripts as s>
                        <#if s == compileScript>
                    <option value="${s}" selected="selected">${s}</option>
                        <#else>
                    <option value="${s}">${s}</option>
                        </#if>
                    </#list>
                </select>
            </div>
        </div>
        <div class="form-group">
            <label class="control-label col-lg-2">Test script</label>
            <div class="col-lg-9">
                <select name="selectTestScript" class="form-control">
                    <#list allTestScripts as s>
                        <#if s == testScript>
                    <option value="${s}" selected="selected">${s}</option>
                        <#else>
                    <option value="${s}">${s}</option>
                        </#if>
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
                          >${project.submissionInstructions}</textarea>
            </div>
        </div>
        <div class="form-group">
            <div class="col-lg-offset-2 col-lg-9">
                <@error_list id="errorList" />
                <input type="submit" value="Update"
                       class="btn btn-danger" />
            </div>
        </div>
    </fieldset>
</form>
</@panel>
</#assign>

<#assign page_script>
<script type="text/javascript">
function ValidateForm()
{
    var title = document.forms["editProjectForm"]["title"].value;
    var assigned = document.forms["editProjectForm"]["assigned"].value;
    var dueDate = document.forms["editProjectForm"]["dueDate"].value;
    var dueTime = document.forms["editProjectForm"]["dueTime"].value;
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
    var assignDate = new Date("${project.assignDate}");
    var dueDate = new Date("${project.dueDate}");
    var dueTime = AddZeros(dueDate.getHours()) + ":" + AddZeros(dueDate.getMinutes()) + ":" + AddZeros(dueDate.getSeconds());
    $("#assigned").datepicker();
    $("#assigned").datepicker("setDate", assignDate);
    $("#dueDate").datepicker();
    $("#dueDate").datepicker("setDate", dueDate);
    $("#dueTime").prop('value', dueTime);
    $("#visible").prop('checked', ${project.visible?c});
});
</script>
</#assign>

<#include "../base.ftl">

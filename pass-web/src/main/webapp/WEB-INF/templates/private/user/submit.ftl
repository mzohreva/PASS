<#assign page_title="${project.title}">

<#assign current_page="test_${project.id?c}">

<#include "/macros.ftl">

<#assign page_content>
<p>
    Due on <strong>${project.dueDate}</strong>
    with a grace period of ${project.gracePeriodHumanReadable}.
</p>

<@panel title="Download assignment material">
    <ul class="download_list">
        <#list projectFiles as f>
            <li>
                <a href="${WebConfig.view("download")}?project=${project.id?c}&file=${f}">${f}</a>
            </li>
        <#else>
            <li>none</li>
        </#list>
    </ul>
</@panel>

<@panel title="Instructions" class="primary" body_class="bg-info">
    <div>
        ${submissionInstructions}
    </div>
</@panel>

<#if project.deadlinePassed>
  <p class="lead">
    You can no longer submit for this assignment. The deadline has passed.
  </p>
<#else>
  <@panel title="Submit your code" class="default">
    <form id="form1" name="form1" method="POST"
          action="${WebConfig.view("test")}" enctype="multipart/form-data"
          class="form-horizontal" onsubmit="return ValidateForm();">
        <input type="hidden" name="projectId" id="projectId" value="${project.id?c}" />

        <fieldset>
            <div class="form-group">
                <label class="control-label col-lg-2">Common Options</label>
                <div class="col-lg-9">
                    <dl>
                        <#list commonOptions as opt>
                        <dd>
                            <div class="checkbox"><label>
                                <input type="checkbox"
                                       name="${opt.id}"
                                       id="${opt.id}"
                                       value="true" />
                                ${opt.description} (<code>${opt.args}</code>)
                            </label></div>
                        </dd>
                        </#list>
                    </dl>
                </div>
            </div>
            <div class="form-group">
                <label class="control-label col-lg-2">C Dialect</label>
                <div class="col-lg-9">
                    <dl>
                        <dd><div class="radio">
                            <label><input type="radio"
                                          name="C_DIALECT"
                                          value="none" checked /> Unspecified
                             </label>
                         </div></dd>
                        <#list cDialects as opt>
                        <dd>
                            <div class="radio"><label>
                                <input type="radio"
                                       name="C_DIALECT"
                                       value="${opt.id}" />
                                ${opt.description} (<code>${opt.args}</code>)
                            </label></div>
                        </dd>
                        </#list>
                    </dl>
                </div>
            </div>
            <div class="form-group">
                <label class="control-label col-lg-2">C++ Dialect</label>
                <div class="col-lg-9">
                    <dl>
                        <dd><div class="radio">
                            <label><input type="radio"
                                          name="CPP_DIALECT"
                                          value="none" checked /> Unspecified
                             </label>
                         </div></dd>
                        <#list cppDialects as opt>
                        <dd>
                            <div class="radio"><label>
                                <input type="radio"
                                       name="CPP_DIALECT"
                                       value="${opt.id}" />
                                ${opt.description} (<code>${opt.args}</code>)
                            </label></div>
                        </dd>
                        </#list>
                    </dl>
                </div>
            </div>

            <div class="form-group">
                <label class="control-label col-lg-2">Files</label>
                <div class="col-lg-9">
                    <input type="file" aria-describedby="filesHelp"
                           name="files"
                           id="files" multiple />
                    <span id="filesHelp" class="help-block">
                        You can select multiple files by holding the Ctrl
                        or Shift key in the file dialog.
                    </span>
                </div>
            </div>
            <div class="form-group">
                <div class="col-lg-offset-2 col-lg-9">
                    <input type="submit" value="Submit for ${project.title}"
                           class="btn btn-primary" id="submitButton" />
                </div>
            </div>
        </fieldset>
    </form>
  </@panel>
</#if>
</#assign>

<#assign page_script>
<script type="text/javascript">
    function DisableSubmitButton()
    {
        $("#submitButton").prop("disabled", true);
        $("#submitButton").prop("value", "Please wait...");
    }

    function ValidateForm()
    {
        var control = document.getElementById("files");
        var files = control.files;
        if (files.length === 0)
        {
            alert("No files are selected for submission");
            control.focus();
            return false;
        }
        var maxCount = ${maxNumberOfFilesPerSubmission?c};
        if (files.length > maxCount)
        {
            alert("Too many files! You can submit " + maxCount + " files at most.");
            control.focus();
            return false;
        }
        var maxTotalSize = ${maxTotalFileSizePerSubmission?c};
        var totalSize = 0;
        for (var i = 0; i < files.length; i++)
        {
            totalSize += files[i].size;
        }
        if (totalSize > maxTotalSize)
        {
            alert("The total size of the selected files exceeds the limit (" + (maxTotalSize / 1024) + " KB).");
            control.focus();
            return false;
        }
        DisableSubmitButton();
        return true;
    }
</script>
</#assign>

<#include "../base.ftl">

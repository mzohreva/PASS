<#assign page_title="User: ${user.username}">

<#assign current_page="view_user">

<#include "/macros.ftl">

<#assign page_content>
    <form name="retestSubmission" method="POST"
          action="${WebConfig.view("retest")}">
        <input type="hidden" name="submissions" />
    </form>

    <p class="lead"><b>Username:</b> ${user.username}</p>
    <p class="lead"><b>First Name:</b> ${user.firstname}</p>
    <p class="lead"><b>Last Name:</b> ${user.lastname}</p>
    <p class="lead"><b>Stuent Id:</b> <code>${user.studentId}</code></p>
    <p class="lead"><b>Email:</b> <code>${user.email}</code></p>
    <p class="lead"><b>Verified:</b> ${user.verified?string("Yes", "No")}</p>
    <p class="lead"><b>Submissions:</b>
        <table id="tblSubmissions" class="table table-striped">
            <thead>
                <tr>
                    <th class="text-center">#&nbsp;</th>
                    <th>Assignment</th>
                    <th>Date</th>
                    <th class="text-center">Days Late</th>
                    <th class="text-center">Compile</th>
                    <th>Test Results</th>
                    <th class="no-sort">
                        <input type="checkbox"
                               id="cbAllNone" value="1"
                               onclick="SelectAllNone(this)" />
                    </th>
                </tr>
            </thead>
            <tbody>
            <#list submissions as sub>
                <tr>
                    <td class="text-center">
                        <a href="${WebConfig.view("view_submission_admin")}?id=${sub.id?c}"
                           title="View submission details">${sub.id?c}</a>
                    </td>
                    <td>${sub.project.title}</td>
                    <td data-sort="${sub.submissionDate?datetime?string.iso}">
                        ${sub.submissionDate?datetime}
                    </td>
                    <td class="text-center">${sub.daysLate?c}</td>
                    <td class="text-center">
                        <#if sub.compileSuccessful>
                            <span class="text-success">Successful</span>
                        <#else>
                            <span class="text-danger">Failed</span>
                        </#if>
                    </td>
                    <td>
                        ${sub.testResult}
                    </td>
                    <td>
                        <input type="checkbox"
                               name="selectedSubmissions" value="${sub.id?c}"
                               onclick="UpdateAllNone()" />
                    </td>
                </tr>
            <#else>
                <tr>
                    <td colspan="7">none</td>
                </tr>
            </#list>
            </tbody>
        </table>
        <div class="text-right">
            <button type="button"
                    class="btn btn-default"
                    title=" Re-evaluate selected submissions"
                    onclick="ReTestSubmissions()">
                <span class="glyphicon glyphicon-check"></span> Re-Evaluate
            </button>
        </div>
    </p>
</#assign>

<#assign page_script>
<script type="text/javascript">
function UpdateAllNone()
{
    var sameState = true;   // Assume all chkboxes have the same state
    var state = null;       // > that shared state
    $("input[name='selectedSubmissions'").each(function() {
        if (state === null)
            state = this.checked;
        else if (this.checked != state)
            sameState = false;
    });
    var cbAllNone = document.getElementById("cbAllNone");
    if (sameState && state != null)
    {
        cbAllNone.indeterminate = false;
        cbAllNone.checked = state;
    }
    else
    {
        cbAllNone.indeterminate = true;
        cbAllNone.checked = false;
    }
}

function SelectAllNone(cbAllNone)
{
    $("input[name='selectedSubmissions'").each(function() {
        this.checked = cbAllNone.checked;
    });
}

function ReTestSubmissions()
{
    var submissions = "";
    var count = 0;
    $("input[name='selectedSubmissions'").each(function() {
        if (this.checked)
        {
            count++;
            submissions += " " + this.value;
        }
    });
    submissions = submissions.trim();
    if (count === 0)
    {
        alert("No submission was selected!");
        return false;
    }
    document.forms["retestSubmission"]["submissions"].value = submissions;
    document.forms["retestSubmission"].submit();
}

<#if submissions?size != 0>
    new Tablesort(document.getElementById("tblSubmissions"), { descending: true });
</#if>
</script>
</#assign>

<#include "../base.ftl">

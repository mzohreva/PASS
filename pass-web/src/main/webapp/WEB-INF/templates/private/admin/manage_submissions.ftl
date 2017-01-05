<#assign page_title="${current_mode?capFirst} submissions for ${project.title}">

<#assign current_page="manage_submissions_${project.id?c}">

<#include "/macros.ftl">

<#assign page_content>
<form name="retestSubmission" method="POST"
      action="${WebConfig.view("retest")}">
    <input type="hidden" name="submissions" />
</form>

<div class="btn-toolbar">
    <#assign btnCls="btn btn-sm" />
    <button type="button"
            class="${btnCls} btn-success"
            onclick="return ExportCSV();">
        <span class="glyphicon glyphicon-list"></span> Export CSV
    </button>
    <#if current_mode == "all">
        <a class="${btnCls} btn-default"
           href="${latest_link}"
           role="button">
            <span class="glyphicon glyphicon-refresh"></span> Show Latest</a>
    <#else>
        <a class="${btnCls} btn-default"
           href="${all_link}"
           role="button">
            <span class="glyphicon glyphicon-refresh"></span> Show All</a>
    </#if>
    <button type="button"
            class="${btnCls} btn-default"
            title=" Re-evaluate selected submissions"
            onclick="ReTestSubmissions()">
        <span class="glyphicon glyphicon-check"></span> Re-Evaluate
    </button>
</div>

<table id="tblSubmissions" class="table table-striped">
    <thead>
        <tr>
            <th class="sort-default text-center">#&nbsp;</th>
            <th>Student Id</th>
            <th>First Name</th>
            <th>Last Name</th>
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
    <#list projectSubmissions as sub>
        <tr>
            <td>
                <a href="${WebConfig.view("view_submission_admin")}?id=${sub.id?c}"
                   title="View submission details">${sub.id?c}</a>
            </td>
            <td>${sub.user.studentId}</td>
            <td>${sub.user.firstname}</td>
            <td>${sub.user.lastname}</td>
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
            <td colspan="9">nothing yet</td>
        </tr>
    </#list>
    </tbody>
</table>
</#assign>

<#assign page_script>
<script type="text/javascript" src="${WebConfig.contextPath}/js/table2CSV.js"></script>

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

function ExportCSV()
{
    $("#tblSubmissions").table2CSV();
}

<#if projectSubmissions?size != 0>
    new Tablesort(document.getElementById("tblSubmissions"), { descending: true });
</#if>
</script>
</#assign>

<#include "../base.ftl">

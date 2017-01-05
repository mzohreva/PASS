<#assign page_title="Submission ${sub.id?c}">

<#assign current_page="">

<#include "/macros.ftl">

<#assign page_content>
    <p class="lead"><b>By:</b>
        ${sub.user.username}
        (${sub.user.firstname} ${sub.user.lastname})
        ${sub.user.studentId}
    </p>
    <p class="lead"><b>For:</b> ${sub.project.title}</p>
    <p class="lead"><b>Date:</b> <mark>${sub.submissionDate?datetime}</mark> (${sub.daysLateHumanReadable})</p>
    <p class="lead"><b>Compile Options:</b> <code>${sub.compileOptionsHumanReadable}</code></p>
    <p class="lead"><b>Task State:</b> <code>${state} ${statusMessage!""}</code></p>
    <p class="lead"><b>Files:</b> ${files?join(", ")}</p>
    <p class="lead"><b>Compilation:</b>
        <#if sub.compileSuccessful>
            <span class="text-success">Successful</span>
        <#else>
            <span class="text-danger">Failed</span>
        </#if>
        <pre class="black_box">${sub.compileMessageHtmlEscaped}</pre>
    </p>
    <p class="lead"><b>Test Result:</b>
        <p class="bg-info padded">${sub.testResult}</p>
    </p>

    <form name="retestSubmission" method="POST"
          action="${WebConfig.view("retest")}">
        <input type="hidden" name="submissions" value="${sub.id?c}" />
        <button type="submit"
                class="btn btn-default"
                title=" Re-evaluate this submission"
                onclick="return confirm('Are you sure you want to re-evaluate this submission?')">
            <span class="glyphicon glyphicon-check"></span> Re-Evaluate
        </button>
    </form>
</#assign>

<#assign page_script></#assign>

<#include "../base.ftl">

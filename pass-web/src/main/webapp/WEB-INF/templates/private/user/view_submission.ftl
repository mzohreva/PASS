<#assign page_title="Submission result">

<#assign current_page="submission_result">

<#include "/macros.ftl">

<#assign page_content>
<p class="lead">
    Submission #${submissionId?c} for <strong>${projectTitle}</strong>
    received on <mark>${submissionDate?datetime}</mark>
    (${daysLateHumanReadable}).
</p>

<#if state != "FINISHED">
    <p class="lead">
        ${statusMessage}<img src="${WebConfig.contextPath}/css/loading-dots.gif" width="150" />
    </p>
    <br/><br/>
    <small>This page will automatically refresh itself while your submission is being evaulated.</small>
<#else>
    <#if compileSuccessful>
        <h4>Test result:</h4>
        <p class="bg-info lead padded">${testResult}</p>
        <h4>Compiler output:</h4>
        <pre class="bg-success">${compilerOutput}</pre>
    <#else>
        <h4 class="text-danger">Compile failed:</h4>
        <pre class="bg-danger">${compilerOutput}</pre>
    </#if>
    <small>The compiler output might be truncated if too long.</small>
</#if>
</#assign>

<#assign page_script></#assign>

<#include "../base.ftl">

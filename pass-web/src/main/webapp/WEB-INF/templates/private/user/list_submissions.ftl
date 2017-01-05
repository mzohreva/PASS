<#assign page_title="Submissions for ${project.title}">

<#assign current_page="list_submissions">

<#include "/macros.ftl">

<#assign page_content>
<p class="lead">
    To see compiler output click on the submission id.
    Your most recent submission is <mark>on top of the list</mark>.
</p>
<table class="table table-striped">
    <thead>
        <tr>
            <th class="text-center">Id</th>
            <th>Date</th>
            <th class="text-center">Compile</th>
            <th>Results</th>
        </tr>
    </thead>
    <tbody>
    <#list submissionsList as sub>
        <tr>
            <td class="text-center">
                <a href="${WebConfig.view("view_submission")}?id=${sub.id?c}"
                   title="Click to view submission details">${sub.id?c}</a>
            </td>
            <td>
                ${sub.submissionDate?datetime} <b>(${sub.daysLateHumanReadable})</b>
            </td>
            <td class="text-center">
                <#if sub.compileSuccessful>
                    <span class="text-success">Successful</span>
                <#else>
                    <span class="text-danger">Failed</span>
                </#if>
            </td>
            <td><p>${sub.testResult}</p></td>
        </tr>
    <#else>
        <tr>
            <td colspan="4">none</td>
        </tr>
    </#list>
    </tbody>
</table>
</#assign>

<#assign page_script></#assign>

<#include "../base.ftl">

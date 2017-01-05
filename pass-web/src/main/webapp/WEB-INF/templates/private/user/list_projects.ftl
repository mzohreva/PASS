<#assign page_title="Programming assignments">

<#assign current_page="list_projects">

<#include "/macros.ftl">

<#assign page_content>

<table class="table table-striped">
    <thead>
        <tr>
            <th>Title</th>
            <th>Assigned</th>
            <th>Due</th>
            <th class="text-center">Submissions</th>
        </tr>
    </thead>
    <tbody>
    <#list userProjectsList as up>
        <tr>
            <td><a href="${WebConfig.view("test")}?project=${up.projectId?c}">${up.title}</a></td>
            <td>${up.assignDate}</td>
            <td><b>${up.dueDate}</b></td>
            <td class="text-center">
                <a href="${WebConfig.view("list_submissions")}?project=${up.projectId?c}">
                    ${up.submissionCount?c} (see list)
                </a>
            </td>
        </tr>
    <#else>
        <tr>
            <td colspan="4">nothing yet</td>
        </tr>
    </#list>
    </tbody>
</table>

<p>
    Click on the assignment title to download assignment
    material and submit your solution.<br/>

    Click on the number of submissions to
    see a list of your submissions for each assignment.
</p>

<#include "important_notes.ftl">

</#assign>

<#assign page_script></#assign>

<#include "../base.ftl">

<#assign page_title="Codes">

<#assign current_page="manage_codes">

<#include "/macros.ftl">

<#assign page_content>
<table id="tblCodes" class="table table-striped">
    <thead>
        <tr>
            <th>Code</th>
            <th>Reason</th>
            <th class="sort-default">Age</th>
            <th>User</th>
            <th>Email</th>
            <th class="text-center no-sort">Link</th>
        </tr>
    </thead>
    <tbody>
    <#list allCodes as vc>
        <tr>
            <td><code class="plain">${vc.code}</code></td>
            <td>${vc.reason}</td>
            <td>${vc.age}</td>
            <td>
                <b>${vc.user.username}</b>
                (${vc.user.firstname} ${vc.user.lastname})
                <#assign text=vc.user.verified?string("verified", "not verified")>
                <#assign icon=vc.user.verified?string("ok", "remove")>
                <#assign color=vc.user.verified?string("success", "danger")>
                <span title="Account ${text}"
                      class="glyphicon glyphicon-${icon} text-${color}"></span>
            </td>
            <td>${vc.user.email}</td>
            <td class="text-center">
                <a href="${vc.link}"
                   onclick="return false;"
                   title="You can copy this link"
                   class="btn btn-default btn-xs">
                    <span class="glyphicon glyphicon-link"></span>
                </a>
            </td>
        </tr>
    <#else>
        <tr>
            <td colspan="6">none</td>
        </tr>
    </#list>
    </tbody>
</table>
</#assign>

<#assign page_script>
<script type="text/javascript">

<#if allCodes?size != 0>
    new Tablesort(document.getElementById("tblCodes"));
</#if>
</script>
</#assign>

<#include "../base.ftl">

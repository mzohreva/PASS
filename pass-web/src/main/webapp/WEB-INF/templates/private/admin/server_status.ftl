<#assign page_title="Server status">

<#assign current_page="server_status">

<#include "/macros.ftl">

<#assign page_content>
<form name="maintenanceModeToggleForm" method="POST">
    <input type="hidden" name="action" value="toggleUnderMaintenace" />
</form>

<dl class="dl-horizontal">
    <dt>Maintenance Mode:</dt>
    <dd>
        <div class="btn-group btn-toggle" onclick="ToggleMaintenanceMode()">
            <#assign offcls=underMaintenance?string("btn-default", "btn-success active")>
            <#assign oncls=underMaintenance?string("btn-danger active", "btn-default")>
            <button class="btn btn-sm btn-off ${offcls}">OFF (normal operation)</button>
            <button class="btn btn-sm btn-on ${oncls}">ON (students cannot sign in)</button>
        </div>
        <br/><br/>
    </dd>
    <dt>Uptime:</dt>
    <dd><pre>${upTime}</pre></dd>
    <dt>Disk Usage:</dt>
    <dd><pre>${diskUsage}</pre></dd>
    </dl>
</#assign>

<#assign page_script>
<script type="text/javascript">
function ToggleMaintenanceMode()
{
    document.forms["maintenanceModeToggleForm"].submit();
}

$('.btn-toggle').click(function() {
    $(this).find('.btn').toggleClass('active');
    $(this).find('.btn-off').toggleClass('btn-success btn-default');
    $(this).find('.btn-on').toggleClass('btn-danger btn-default');
});
</script>
</#assign>

<#include "../base.ftl">

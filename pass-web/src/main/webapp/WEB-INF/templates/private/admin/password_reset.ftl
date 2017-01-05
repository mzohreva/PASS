<#assign page_title="Password reset">

<#assign current_page="">

<#include "/macros.ftl">

<#assign page_content>
    <#if success>
        <p class="text-success lead">
            Password for user <b>${username}</b> was successfully
            changed to <code>${new_password}</code>.
        </p>
    <#else>
        <p class="text-danger lead">
            Failed to update password for user <b>${username}</b>.
            Check the log for more details.
        </p>
    </#if>
</#assign>

<#assign page_script></#assign>

<#include "../base.ftl">

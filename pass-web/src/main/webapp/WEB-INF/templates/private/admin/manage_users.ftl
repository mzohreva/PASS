<#assign page_title="Users">

<#assign current_page="manage_users">

<#include "/macros.ftl">

<#assign page_content>
<form name="manageUsers" method="POST">
    <input type="hidden" name="action" />
    <input type="hidden" name="username" />
    <input type="hidden" name="sessionId" />
</form>

<#assign panel_title>
    Active <@badge num="${activeUsers?size}" />
</#assign>
<@panel title="${panel_title}">
    <table id="tblActiveUsers" class="table table-striped">
        <thead>
            <tr>
                <th>Username</th>
                <th>Student Id</th>
                <th>First Name</th>
                <th>Last Name</th>
                <th class="sort-default">Last Access</th>
                <th>Last Accessed View</th>
                <th class="text-center no-sort">Action</th>
            </tr>
        </thead>
        <tbody>
        <#list activeUsers as sid, u>
            <tr>
                <td>
                    <a href="${WebConfig.view("view_user")}?user=${u.username}">
                        ${u.username}</a>
                </td>
                <td>${u.studentId}</td>
                <td>${u.firstName}</td>
                <td>${u.lastName}</td>
                <td data-sort="${u.lastAccess?datetime?string.iso}">${u.lastAccess?datetime}</td>
                <td>${u.lastAccessedView}</td>
                <td class="text-center">
                    <button type="button"
                            class="btn btn-default btn-xs"
                            onclick="Kickout('${u.username}', '${sid}')"
                            title="Kick out">
                        <span class="glyphicon glyphicon-log-out"
                              aria-hidden="true"></span>
                    </button>
                </td>
            </tr>
        <#else>
            <tr>
                <td colspan="7">no one active</td>
            </tr>
        </#list>
        </tbody>
    </table>
</@panel>

<#assign panel_title>
    Registered <@badge num="${allUsers?size}" />
</#assign>
<@panel title="${panel_title}">
    <table id="tblRegisteredUsers" class="table table-striped">
        <thead>
            <tr>
                <th>Username</th>
                <th>Student Id</th>
                <th>First Name</th>
                <th class="sort-default">Last Name</th>
                <th>Email</th>
                <th class="text-center">Verified</th>
                <th class="text-center no-sort">Action</th>
            </tr>
        </thead>
        <tbody>
        <#list allUsers as u>
            <tr>
                <td>
                    <a href="${WebConfig.view("view_user")}?user=${u.username}">
                        ${u.username}</a>
                </td>
                <td>${u.studentId}</td>
                <td>${u.firstname}</td>
                <td>${u.lastname}</td>
                <td>${u.email}</td>
                <#assign text=u.verified?string("verified", "not verified")>
                <#assign icon=u.verified?string("ok", "remove")>
                <#assign color=u.verified?string("success", "danger")>
                <td class="text-center" data-sort="${text}">
                    <span title="Account ${text}"
                          class="glyphicon glyphicon-${icon} text-${color}"></span>
                </td>
                <td class="text-center">
                    <button type="button"
                            class="btn btn-default btn-xs"
                            onclick="ResetPassword('${u.username}')"
                            title="Reset password for ${u.username}">
                        <span class="glyphicon glyphicon-flash"
                              aria-hidden="true"></span>
                    </button>
                    <button type="button"
                            class="btn btn-default btn-xs"
                            onclick="DeleteUser('${u.username}')"
                            title="Delete ${u.username}">
                        <span class="glyphicon glyphicon-trash"
                              aria-hidden="true"></span>
                    </button>
                </td>
            </tr>
        <#else>
            <tr>
                <td colspan="7">no one yet</td>
            </tr>
        </#list>
        </tbody>
    </table>
</@panel>
</#assign>

<#assign page_script>
<script type="text/javascript">
function submitManageUsersForm(action, username, sessionId)
{
    document.forms["manageUsers"]["action"].value = action;
    document.forms["manageUsers"]["username"].value = username;
    document.forms["manageUsers"]["sessionId"].value = sessionId;
    document.forms["manageUsers"].submit();
}

function ResetPassword(username)
{
    if (confirm("Are you sure you want to RESET PASSWORD for user '" + username + "'?"))
    {
        submitManageUsersForm("reset_password", username, "");
    }
}

function DeleteUser(username)
{
    if (confirm("Are you sure you want to DELETE user '" + username + "'?"))
    {
        submitManageUsersForm("delete_user", username, "");
    }
}

function Kickout(username, sessionId)
{
    if (confirm("Are you sure you want to kick out '" + username + "'?"))
    {
        submitManageUsersForm("end_session", username, sessionId);
    }
}

<#if activeUsers?size != 0>
    new Tablesort(document.getElementById("tblActiveUsers"), { descending: true });
</#if>
<#if allUsers?size != 0>
    new Tablesort(document.getElementById("tblRegisteredUsers"));
</#if>
</script>
</#assign>

<#include "../base.ftl">

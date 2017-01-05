<#assign page_title="Archives">

<#assign current_page="manage_archives">

<#include "/macros.ftl">

<#assign page_content>
<form name="manageArchives" method="POST">
    <input type="hidden" name="action" />
    <input type="hidden" name="archive" />
</form>

<@list_tasks tasks=zipTasks title="In progress" table_id="tblInProgress" show_state=true />

<#assign panel_title>Available <@badge num="${archives?size}" /></#assign>
<@panel title="${panel_title}">
    <table id="tblArchives" class="table table-striped">
        <thead>
            <tr>
                <th>Name</th>
                <th>Size</th>
                <th class="sort-default">Created</th>
                <th class="text-center no-sort">Action</th>
            </tr>
        </thead>
        <tbody>
        <#list archives as a>
            <tr>
                <td><code class="plain">${a.name}</code></td>
                <td>${a.sizeHumanReadable}</td>
                <td data-sort="${a.created?datetime?string.iso}">
                    ${a.created?datetime}
                </td>
                <td class="text-center">
                    <button type="button"
                            class="btn btn-default btn-xs"
                            onclick="DownloadArchive('${a.name}')"
                            title="Download">
                        <span class="glyphicon glyphicon-download-alt"
                              aria-hidden="true"></span>
                    </button>
                    <button type="button"
                            class="btn btn-default btn-xs"
                            onclick="DeleteArchive('${a.name}')"
                            title="Delete">
                        <span class="glyphicon glyphicon-trash"
                              aria-hidden="true"></span>
                    </button>
                </td>
            </tr>
        <#else>
            <tr>
                <td colspan="4">none</td>
            </tr>
        </#list>
        </tbody>
    </table>
</@panel>

</#assign>

<#assign page_script>
<script type="text/javascript">
function submitManageArchivesForm(action, archive)
{
    document.forms["manageArchives"]["action"].value = action;
    document.forms["manageArchives"]["archive"].value = archive;
    document.forms["manageArchives"].submit();
}

function DownloadArchive(archive)
{
    submitManageArchivesForm("download_archive", archive);
}

function DeleteArchive(archive)
{
    submitManageArchivesForm("delete_archive", archive);
}

<#if archives?size != 0>
    new Tablesort(document.getElementById("tblArchives"), { descending: true });
</#if>
</script>
</#assign>

<#include "../base.ftl">

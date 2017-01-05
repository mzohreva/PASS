<#macro badge num>
    <span class="badge">${(num == "0")?string("", num)}</span>
</#macro>

<#macro panel title class="default" body_class="">
    <div class="panel panel-${class}">
      <div class="panel-heading">
        <h3 class="panel-title">${title}</h3>
      </div>
      <div class="panel-body ${body_class}">
          <#nested>
      </div>
    </div>
</#macro>

<#macro list_tasks tasks title table_id show_state=false>
    <#assign panel_title>
        ${title} <@badge num="${tasks?size}" />
    </#assign>
    <@panel title="${panel_title}">
        <table id="${table_id}" class="table table-striped">
            <thead>
                <tr>
                    <th>Id</th>
                    <th>Specification</th>
                    <th>Worker</th>
                    <#if show_state><th>State</th></#if>
                    <th>Message</th>
                    <th class="sort-default">Last Status Update</th>
                </tr>
            </thead>
            <tbody>
            <#list tasks as t>
                <tr>
                    <td><code>${t.taskId}</code></td>
                    <td>${t.taskSpec}</td>
                    <td>${t.worker}</td>
                    <#if show_state><td>${t.status.state}</td></#if>
                    <td>${t.status.message!""}</td>
                    <td data-sort="${t.status.time?datetime?string.iso}">
                        ${t.status.time?datetime}
                    </td>
                </tr>
            <#else>
                <tr>
                    <td colspan="${show_state?string("6", "5")}">none</td>
                </tr>
            </#list>
            </tbody>
        </table>
    </@panel>
    <#if tasks?size != 0>
        <script type="text/javascript">
            new Tablesort(document.getElementById("${table_id}"));
        </script>
    </#if>
</#macro>

<#macro error_list id>
    <div id="${id}" class="alert alert-danger" style="display: none;">
        <strong>Error:</strong>
        <div class="errorList"></div>
    </div>
</#macro>

<#assign page_title="Tasks">

<#assign current_page="tasks">

<#include "/macros.ftl">

<#assign page_content>

<@list_tasks tasks=runningTasks title="Running" table_id="tblRunning" />
<@list_tasks tasks=queuedTasks title="Queued" table_id="tblQueued" />

<p>
    Completed:
    <code>${completedEvaluations?c}</code> evaluations /
    <code>${completedCleanups?c}</code> cleanups /
    <code>${completedZips?c}</code> archives
</p>
</#assign>

<#assign page_script>
<script type="text/javascript"></script>
</#assign>

<#include "../base.ftl">

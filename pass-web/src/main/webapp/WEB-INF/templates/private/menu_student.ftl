
<ul class="nav nav-sidebar">
    <li class="${(current_page == "list_projects")?then('active','')}">
        <a href="${WebConfig.view("list_projects")}">
            <span class="glyphicon glyphicon-list"></span> Programming assignments</a>
        <ul class="nav small">
          <#list Projects as p>
            <li class="${(current_page == "test_${p.id?c}")?then('active','')}">
              <a href="${WebConfig.view("test")}?project=${p.id?c}">
                <span class="glyphicon glyphicon-chevron-right"></span>
                ${p.title}</a>
            </li>
          </#list>
        </ul>
    </li>
</ul>

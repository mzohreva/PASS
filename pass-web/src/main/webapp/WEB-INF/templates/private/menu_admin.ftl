
<ul class="nav nav-sidebar">
    <li class="sidebar-search">
        <form method="POST" action="${WebConfig.view("search")}">
            <div class="input-group">
                <input type="text" name="query"
                       class="form-control"
                       placeholder="Search..."
                       title="Enter a submission id or a username">
                <span class="input-group-btn">
                    <button class="btn btn-default" type="submit">
                        <span class="glyphicon glyphicon-search"></span>
                    </button>
                </span>
            </div>
        </form>
    </li>
    <li class="${(current_page == "manage_projects")?then('active','')}">
        <a href="${WebConfig.view("manage_projects")}">
            <span class="glyphicon glyphicon-education"></span> Assignments</a>
    </li>
    <li class="${(current_page == "manage_users")?then('active','')}">
        <a href="${WebConfig.view("manage_users")}">
            <span class="glyphicon glyphicon-user"></span> Users</a>
    </li>
    <li class="${(current_page == "manage_codes")?then('active','')}">
        <a href="${WebConfig.view("manage_codes")}">
            <span class="glyphicon glyphicon-barcode"></span> Codes</a>
    </li>
    <li class="${(current_page == "manage_archives")?then('active','')}">
        <a href="${WebConfig.view("manage_archives")}">
            <span class="glyphicon glyphicon-compressed"></span> Archives</a>
    </li>
    <li class="${(current_page == "server_status")?then('active','')}">
        <a href="${WebConfig.view("server_status")}">
            <span class="glyphicon glyphicon-wrench"></span> Server</a>
    </li>
    <li class="${(current_page == "tasks")?then('active','')}">
        <a href="${WebConfig.view("tasks")}">
            <span class="glyphicon glyphicon-tasks"></span> Tasks</a>
    </li>
</ul>

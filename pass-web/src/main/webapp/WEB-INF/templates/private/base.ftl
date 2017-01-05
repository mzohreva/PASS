<#assign template_body>
    <nav class="navbar navbar-inverse navbar-fixed-top">
        <div class="container-fluid">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle collapsed"
                        data-toggle="collapse" data-target="#navbar"
                        aria-expanded="false" aria-controls="navbar">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand"
                   href="${WebConfig.contextPath}">
                    <p><img src="${WebConfig.contextPath}/css/icon.png"
                            width="20"
                            height="20" /> ${WebConfig.siteTitle}</p>
                </a>
            </div>
            <div id="navbar" class="collapse navbar-collapse">
                <#if User??>
                    <p class="navbar-text navbar-right">
                        ${User.username}&nbsp;
                        <a class="navbar-link signout_link"
                           href="${WebConfig.view("signout")}"
                           title="Sign out">
                        <span class="glyphicon glyphicon-off"></span></a>
                        &nbsp;&nbsp;&nbsp;
                    </p>
                </#if>
            </div>
        </div>
    </nav>

    <div class="container-fluid">
        <div class="row">
            <div id="side-nav" class="col-sm-3 col-md-2 sidebar">
                <#if User??>
                    <#if User.isAdmin()>
                        <#include "menu_admin.ftl">
                        <p>Student's menu</p>
                        <#include "menu_student.ftl">
                    <#else>
                        <#include "menu_student.ftl">
                    </#if>
                </#if>
            </div>
            <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
                <h2 class="page-header">${page_title}</h2>
                ${page_content}
                <footer class="footer">
                    <small class="text-muted">
                        Powered by: <a href="https://github.com/mzohreva/PASS"
                                       target="_blank">PASS</a>
                        &nbsp; | &nbsp;
                        Retrieved: <em>${.now?datetime}</em>
                    </small>
                </footer>
            </div>
        </div>
    </div>
    ${page_script}
</#assign>

<#include "/html_skeleton.ftl">

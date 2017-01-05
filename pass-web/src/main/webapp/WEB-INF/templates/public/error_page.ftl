<#assign page_title="Error ${error_code?c}">

<#assign current_page="">

<#include "/macros.ftl">

<#assign page_content>
<div class="row">
    <div class="col-xs-4 col-md-3 col-lg-2 text-danger">
        <span class="glyphicon glyphicon-exclamation-sign"
              style="font-size: 9em"></span>
    </div>
    <div class="col-xs-8 col-md-9 col-lg-10">
        <p class="text-danger lead">
            ${error_description}
        </p>
        <a href="${home_page}">Home</a>
    </div>
</div>
</#assign>

<#assign page_script></#assign>

<#include "base.ftl">

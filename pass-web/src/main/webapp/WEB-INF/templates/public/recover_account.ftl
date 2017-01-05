<#assign page_title="Recover your account">

<#include "/macros.ftl">

<#assign page_content>
<p>
    Fill out this form to recover your account.
</p>
<div class="well bs-component">
<br/>
<form id="form1" name="form1" method="POST" class="form-horizontal"
      onsubmit="return ValidateForm();">
    <fieldset>
        <div class="form-group">
            <label class="control-label col-lg-2">Username</label>
            <div class="col-lg-9">
                <input type="text" size="20"
                       name="username" class="form-control"
                       placeholder="Enter your username" />
            </div>
        </div>
        <div class="form-group">
            <div class="col-lg-9 col-lg-offset-2">
                <@error_list id="errorList" />
                <input type="submit" value="Submit" class="btn btn-primary" />
                &nbsp;&nbsp;&nbsp;<a href="${WebConfig.view("signin")}">Back</a>
            </div>
        </div>
    </fieldset>
</form>
</div>
</#assign>

<#assign page_script>
<script type="text/javascript">
    function ValidateForm()
    {
        clearErrors("errorList");
        var username = document.forms["form1"]["username"].value;
        var msgs = [];
        if (username.length < 1)
        {
            msgs.push("You must enter your username");
        }
        if (msgs.length > 0)
            setErrors("errorList", msgs);
        return msgs.length === 0;
    }
</script>
</#assign>

<#include "base.ftl">

<#assign page_title="Pick a new password">

<#include "/macros.ftl">

<#assign page_content>
<div class="well bs-component">
<form id="form1" name="form1" class="form-horizontal"
      method="POST" action="${WebConfig.view("reset_password")}"
      onsubmit="return ValidateForm();">

    <input type="hidden" name="code" value="${verificationCode}" />

    <fieldset>
        <legend></legend>
        <div class="form-group">
            <label class="control-label col-lg-2">Password</label>
            <div class="col-lg-9">
                <input type="password" size="20"
                       name="password" class="form-control"
                       placeholder="Pick a password that is at least 6 characters long" />
            </div>
        </div>
        <div class="form-group">
            <label class="control-label col-lg-2">Retype Password</label>
            <div class="col-lg-9">
                <input type="password" size="20"
                       name="password_confirm" class="form-control"
                       placeholder="Enter the password again" />
            </div>
        </div>
        <div class="form-group">
            <div class="col-lg-9 col-lg-offset-2">
                <@error_list id="errorList" />
                <input type="submit"
                       value="Reset Password"
                       class="btn btn-primary" />
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
        var pass1 = document.forms["form1"]["password"].value;
        var pass2 = document.forms["form1"]["password_confirm"].value;
        // Clear error messages
        clearErrors("errorList");
        // Now check for errors
        var msgs = [];
        if (pass1.length < 6)
        {
            msgs.push("Password must be at least 6 characters long");
        }
        if (pass2 !== pass1)
        {
            msgs.push("Passwords do not match");
        }
        if (msgs.length > 0)
            setErrors("errorList", msgs);
        return msgs.length === 0; // true if no error
    }
</script>
</#assign>

<#include "base.ftl">

<#assign page_title="Sign up">

<#include "/macros.ftl">

<#assign page_content>
<p>
    Fill out the following form to sign up.
    Your username and student id will be checked
    against the list of students enrolled in the class.
</p>
<div class="well bs-component">
<br/>
<form id="form1" name="form1" method="POST"
      action="${WebConfig.view("register")}"
      class="form-horizontal" onsubmit="return ValidateForm();">
    <fieldset>
        <div class="form-group">
            <label class="control-label col-lg-2">Student ID</label>
            <div class="col-lg-9">
                <input type="text" size="20" maxlength="10"
                       name="studentId" class="form-control"
                       placeholder="Your 10 digit student id" />
            </div>
        </div>
        <div class="form-group">
            <label class="control-label col-lg-2">Username</label>
            <div class="col-lg-9">
                <input type="text" size="20"
                       name="username" class="form-control"
                       placeholder="Username you use to login to MyASU" />
            </div>
        </div>
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
                <input type="submit" value="Sign up" class="btn btn-primary" />
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
        var studentId = document.forms["form1"]["studentId"].value;
        var username = document.forms["form1"]["username"].value;
        var pass1 = document.forms["form1"]["password"].value;
        var pass2 = document.forms["form1"]["password_confirm"].value;
        clearErrors("errorList");
        var msgs = [];
        if (studentId.length < 9)
        {
            msgs.push("You must enter your student id");
        }
        if (username.length < 1)
        {
            msgs.push("You must enter your username");
        }
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
        return msgs.length === 0;
    }
</script>
</#assign>

<#include "base.ftl">

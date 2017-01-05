<#assign page_title="Sign in">

<#include "/macros.ftl">

<#assign page_content>
<div class="well bs-component">
    <br/>
    <form id="form1" name="form1" method="post"
          action="${WebConfig.view("signin")}"
          class="form-horizontal">
        <fieldset>
            <div class="form-group">
                <label class="control-label col-lg-2">Username</label>
                <div class="col-lg-9">
                    <input type="text" size="40" name="username" class="form-control"/>
                </div>
            </div>
            <div class="form-group">
                <label class="control-label col-lg-2">Password</label>
                <div class="col-lg-9">
                    <input type="password" size="40" name="password" class="form-control"/>
                </div>
            </div>
            <div class="form-group">
                <div class="col-lg-9 col-lg-offset-2">
                    <input type="submit" value="Sign in" class="btn btn-primary" />
                </div>
            </div>
        </fieldset>
    </form>
</div>
<@error_list id="errorList" />
<p>
    <a href="${WebConfig.view("recover_account")}">
        Forgot your password?
    </a>
    -
    <a href="${WebConfig.view("register")}">
        Sign up
    </a>
</p>
</#assign>

<#assign page_script>
<script type="text/javascript">
    var err = getParam("err");
    if (err === "1")
    {
        setErrors("errorList", ["Invalid username or password"]);
    }
    if (err === "2")
    {
        setErrors("errorList", ["Your account has not been verified yet. Check your email for verification link."]);
    }
</script>
</#assign>

<#include "base.ftl">

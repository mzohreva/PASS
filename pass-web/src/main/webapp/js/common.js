
function getParam(name)
{
    name = name.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");
    var regexS = "[\\?&]" + name + "=([^&#]*)";
    var regex = new RegExp(regexS);
    var results = regex.exec(window.location.search);
    if (results === null)
        return "";
    else
        return decodeURIComponent(results[1].replace(/\+/g, " "));
}

function isValidDate(date) /* Only supports "MM/dd/yyyy" format */
{
    var matches = /^(\d{2})[-\/](\d{2})[-\/](\d{4})$/.exec(date);
    if (matches === null)
        return false;
    var d = matches[2] - 0;
    var m = matches[1] - 1;
    var y = matches[3] - 0;
    var composedDate = new Date(y, m, d);
    return composedDate.getDate() === d &&
            composedDate.getMonth() === m &&
            composedDate.getFullYear() === y;
}

function isValidTime(time) /* Only supports "HH:mm:ss" format */
{
    var matches = /^(\d{2})[:](\d{2})[:](\d{2})$/.exec(time);
    if (matches === null)
        return false;
    var h = matches[1] - 0;
    var m = matches[2] - 0;
    var s = matches[3] - 0;
    return h >= 0 && h < 24 && m >= 0 && m < 60 && s >= 0 && s < 60;
}

function AddZeros(number, length)
{
    if (length === undefined)
        length = 2;
    var num = "" + number;
    while (num.length < length)
    {
        num = "0" + num;
    }
    return num;
}

function clearErrors(errorListId)
{
    var id = "#" + errorListId;
    $(id + " > .errorList").html("");
    $(id).css("display", "none");
}

function setErrors(errorListId, errors)
{
    var id = "#" + errorListId;
    var errList = "<ul>";
    for (var e in errors)
        errList += "<li>" + errors[e] + "</li>";
    errList += "</ul>";
    $(id + " > .errorList").html(errList);
    $(id).css("display", "");
}

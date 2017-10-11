/**
 * Created by mohit on 11/10/17.
 */
var editor = ace.edit("editor");
editor.setTheme("ace/theme/chaos");
editor.getSession().setMode("ace/mode/c_cpp");

$('#code_submit').click(function (){
    console.log("Code submitted.");
    $('#code').val(editor.getValue());
    $('form#problem_submit_form').submit();
});
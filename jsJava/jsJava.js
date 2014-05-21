var js_java_types = "String|Scanner|int|double|float|boolean|short|char";
var js_java_ret_types = js_java_types+"|void";
var js_java_out_nl = "System.out.println";
var js_java_out_cont = "System.out.print";
var js_java_input = "\w+\.(nextLine()|next()|nextDouble|nextInt())";
var js_java_access = "public|protected|private";
var js_java_final = "final";
var js_java_static = "static";
var js_java_params = "(\(( *("+js_java_types+" +(\w+)))+\))";
var js_function_decl = "("+js_java_access+" +)?("+js_java_ret_types+" +)"+js_java_params;

var js_java_types_re = new RegExp("("+js_java_types+") +","g");
var js_java_ret_types_re = new RegExp(js_java_ret_types,"g");
var js_java_out_nl_re = new RegExp(js_java_out_nl,"g");
var js_java_out_cont_re = new RegExp(js_java_out_cont,"");
var js_java_input_re = new RegExp(js_java_input,"g");

var js_java_output_div_id = "js_sys_output";

function print(arg){
    out_div = document.getElementById(js_java_output_div_id);
    if(out_div)
        out_div.innerHTML+=arg;
    else
        alert(arg);
}

var System = {
    out : {
        println:function(arg){ print(arg+"<br/>"); },
        print:function(arg){print(arg); }
    },
    in:"Dummy"
}

function Scanner(input){
    this.next=function(){return prompt("Enter your answer");};
    this.nextLine = function(){return prompt("Enter your answer");};
    this.nextInt = function(){return parseInt(prompt("Enter your answer"));};
    this.nextDouble = function(){return parseFloat(prompt("Enter your answer"));};
    
}


function js_translate(jsprog){
    jsprog = jsprog.replace(js_java_types_re,"var ");
   // jsprog = jsprog.replace(js_java_out_nl,"alert");
    return jsprog;
}

function js_run(jsprog){
    out = document.getElementById("js_output");
    code = js_translate(jsprog);
    console.log(code);
    out.innerHTML = code;
    eval(code);
}

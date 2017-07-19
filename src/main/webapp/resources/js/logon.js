//ajax检测用户名是否存在
function checkAccount(){
    var userName = $("#userName").val(); //获取用户名进行判断
    if(userName.length<4 && userName!=""){
        console.log("length<4---------------");
        //$("#buttonLogon").attr("disabled", true);
        $("#userNameMsg").text("用户名长度不能少于4");
        return true;
    }
    else if(userName!=""){
        $.ajax({
            type: "POST",
            contentType: "application/json",
            url: "/logonCheckUserName",    //这里的url为Servlet配置xml文件的路径
            dataType: "json",
            data: JSON.stringify({"userName":userName}),  //获取form表单所又内容
            success: function(ma){
                console.log(ma);
                if(ma.isExist == "true" || ma.isExist =="TRUE"){
                    console.log("isExist--------------------");
                   // $("#buttonLogon").attr("disabled", true);
                    $("#userNameMsg").text("用户名被占用");
                    return true;
                }else{
                    console.log("---------------------!isExist");
                   // $("#buttonLogon").attr("disabled", false);
                    $("#userNameMsg").text("用户名可以使用");
                    return false;
                }
            }
        });
    }else{
        $("#userNameMsg").text("请输入用户名");
        return true;
    }
}

function checkPassword(){
    var password1 = $("#password1").val(); //获取密码
    var password2 = $("#password2").val()
    if(password1!=password2){
        console.log("pass1!=pass2--------------------");
        //$("#buttonLogon").attr("disabled", true);
        $("#passwordMsg").text("两次密码不同");
        return true;
    }
    else{
        console.log("pass1==pass2--------------------");
        //$("#buttonLogon").attr("disabled", false);
        $("#passwordMsg").text("");
        return false;
    }
}

function validateUp(){
    if(!checkAccount() && !checkPassword()){
        $("#buttonLogon").attr("disabled", false);
        console.log("valid");
    }
    else{
        $("#buttonLogon").attr("disabled", true);
        console.log("invaliae");
    }
}

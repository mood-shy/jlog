/**
 * 放一些公共方法
 * Created by hanxu3 on 2016/1/12.
 */
function format(text){
    var json = JSONEditor.parse(text);
    text = JSON.stringify(json, null, 4);
    return text;
}
/**
 * 替换分隔符
 * @param str
 * @returns {string}
 */
function getSplitString(str) {
    var arr = str.split(",");

    var resources = "";
    for (var i = 0; i < arr.length; i++) {
        var arr1 = arr[i].split(/\s+/);

        for (var j = 0; j < arr1.length; j++) {
            if (jQuery.trim(arr1[j]) != "") {
                resources += jQuery.trim(arr1[j]) + ",";
            }
        }
    }

    return resources;
}
/* 格式化JSON源码(对象转换为JSON文本) */
function formatJson(txt,compress/*是否为压缩模式*/){
    var indentChar = '    ';
    if(/^\s*$/.test(txt)){
        alert('数据为空,无法格式化! ');
        return;
    }
    try{var data=eval('('+txt+')');}
    catch(e){
        console.log('数据源语法错误,格式化失败! 错误信息: '+e.description,'err');
        return txt;
    };
    var draw=[],last=false,This=this,line=compress?'':'\n',nodeCount=0,maxDepth=0;

    var notify=function(name,value,isLast,indent/*缩进*/,formObj){
        nodeCount++;/*节点计数*/
        for (var i=0,tab='';i<indent;i++ )tab+=indentChar;/* 缩进HTML */
        tab=compress?'':tab;/*压缩模式忽略缩进*/
        maxDepth=++indent;/*缩进递增并记录*/
        if(value&&value.constructor==Array){/*处理数组*/
            draw.push(tab+(formObj?('"'+name+'":'):'')+'['+line);/*缩进'[' 然后换行*/
            for (var i=0;i<value.length;i++)
                notify(i,value[i],i==value.length-1,indent,false);
            draw.push(tab+']'+(isLast?line:(','+line)));/*缩进']'换行,若非尾元素则添加逗号*/
        }else   if(value&&typeof value=='object'){/*处理对象*/
            draw.push(tab+(formObj?('"'+name+'":'):'')+'{'+line);/*缩进'{' 然后换行*/
            var len=0,i=0;
            for(var key in value)len++;
            for(var key in value)notify(key,value[key],++i==len,indent,true);
            draw.push(tab+'}'+(isLast?line:(','+line)));/*缩进'}'换行,若非尾元素则添加逗号*/
        }else{
            if(typeof value=='string')value='"'+value+'"';
            draw.push(tab+(formObj?('"'+name+'":'):'')+value+(isLast?'':',')+line);
        };
    };
    var isLast=true,indent=0;
    notify('',data,isLast,indent,false);
    return draw.join('');
}

/**
 * 关闭弹层
 */
function closePopDiv(div){
    div.fadeOut("slow");//淡入淡出效果 隐藏div
}
/**
 * 打开弹层
 */
function openPopDiv(div){
    div.fadeIn("slow");//淡入淡出效果 显示div
}
/**
 * 定时任务
 * @param func
 * @param wait
 */
function myInterval(func, wait){
    var timer;
    var interv = function(){
        func.call(null);
        timer = setTimeout(interv, wait);
    };
    timer = setTimeout(interv, wait);
    return timer;
}
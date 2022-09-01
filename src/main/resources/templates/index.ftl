<#-- @ftlvariable name="data" type="bid.yuanlu.mc.bot.web.IndexData" -->
<#setting number_format="#">
<html lang="zh-CN">
<head>

    <title>MC Bot</title>
    <#include "common.ftl">
    <link rel="stylesheet" href="css/index.css">
</head>
<body>
<div class="container">

    <div class="row">
        <#list data.plays as play>
            <div class="play-info col col-md-6 col-xl-4">
                <table>
                    <tr>
                        <td>玩家</td><td>${play.player}</td>
                    </tr>
                    <tr>
                        <td>服务器</td><td>${play.host}:${play.port}</td>
                    </tr>
                    <tr>
                        <td>状态</td><td class="online-${play.online?string("t","f")}">${play.online?string("在线","离线")}</td>
                    </tr>
                </table>
                <div class="id">${play.id}</div>
            </div>
        </#list>
    </div>
</div>
</body>
</html>
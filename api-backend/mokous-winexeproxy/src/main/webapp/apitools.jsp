<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<head>
<title>API测试工具</title>
<style type="text/css">
* {
	padding: 0;
	margin: 0
}

body, button, input, select, textarea {
	font: 12px/1.125 Arial, Helvetica, sans-serif;
	_font-family: "宋体";
}

html, body, fieldset, img, iframe, abbr {
	border: 0;
}

textarea {
	overflow: auto;
	resize: none;
}

.cf:before, .cf:after {
	content: "";
	display: table;
}

.cf:after {
	clear: both;
}

.cf {
	zoom: 1;
}

.fl {
	float: left;
}

.fr {
	float: right;
}

.wrap {
	width: 1024px;
	/* height: 550px; */
	margin: -20px auto;
	background: #FFF;
	position: relative;
	border: 1px solid #DFDFDF;
	padding: 12px;
	border-radius: 1px;
	box-shadow: 0px 1px 6px 1px #bbb;
}

.main {
	width: 364px;
}

.side {
	width: 660px;
}

.side #result {
	width: 658px;
	height: 600px;
	overflow-y: auto;
	border: 1px solid #bbb;
}

.addbox dd.inp {
	margin: 5px 0;
}

.addbox input {
	height: 24px;
}

.main select {
	min-width: 200px;
}

.main #serverName {
	width: 250px;
}

.main dt {
	margin-top: 15px;
	font-weight: bold;
	padding: 4px;
}

#getData {
	width: 120px;
	height: 30px;
	background: #4082E6;
	color: #FFF;
	border: 1px solid #38779B;
	border-radius: 3px;
	margin: 10px auto;
	cursor: pointer;
}

dd.inp .op_closes {
	display: inline-block;
	width: 18px;
	padding: 3px 0;
	margin-left: 5px;
	text-decoration: none;
	text-align: center;
	color: #A4A4A4;
	cursor: point;
}

.icon_op_add, .add-input {
	color: #065FE6;
	text-decoration: none;
}

#url {
	padding: 4px;
	color: gray;
	font-style: italic;
}

.header {
	width: 100%;
	height: 80px;
	background: #8B0000
}

.header h2 {
	font-size: 26px;
	color: #FFF;
	text-align: center;
	line-height: 70px;
	text-shadow: 1px 1px 1px #D1D1D1
}
</style>
</head>
<body>
	<div class="header">
		<h2>API测试工具</h2>
	</div>
	<div class="wrap cf">

		<div class="main fl">

			<dl class="s3 clearfix">
				<dt>Server：</dt>
				<dd>
					<input type="text" id="serverName"
						value="<%=request.getContextPath()%>" />
				</dd>
			</dl>
			<dl class="s3 clearfix">
				<dt>API分类：</dt>
				<dd>
					<select id="apitype">
					</select>
				</dd>
			</dl>
			<dl class="s3 clearfix">
				<dt>API名称：</dt>
				<dd>
					<select id="apiaction">

					</select>
				</dd>
			</dl>

			<dl class="s3 clearfix">
				<dt>API参数</dt>
				<dt class="addbox"></dt>
			</dl>

			<div>
				<span class="icon_op_add">+</span><a href="javascript:;"
					class="add-input">添加</a>
			</div>
			<input id="server" value="/1/rfq-coreapi" type="hidden" /> <input
				id="param" value="" type="hidden" />
			<button id="getData" onClick="go()">调用接口</button>

		</div>

		<div class="side fl">
			<div id="url"></div>
			<form id="form" method="post" target="resultFrame"></form>
			<iframe name="resultFrame"
				style="width: 650px; height: 540px; border: 1px solid #B2D3EE; border-radius: 3px"></iframe>
		</div>

	</div>
	<script type="text/javascript"
		src="http://lib.vipsinaapp.com/js/jquery/1.8.2/jquery.min.js "></script>
	<script type="text/javascript">
		//帐号相关
		var account_actions = new Array();
		account_actions["/account/authorizer/login"] = 'email=&password=';
		account_actions["/account/authorizer/request"] = "authJson=&guid=&appleId=";
		account_actions["/account/authorizer/pc"] = "appleId=&pwd=&osGuid=&osName=&kMachineIdA=&kMachineIdB=&ip=&port=&createSession";
		account_actions["/account/authorizer/pc/info"] = "";
		account_actions["/account/authorizer/pc/byexe"] = "appleId=&pwd=&osGuid=&osName=&kMachineIdA=&kMachineIdB=&ip=&port=&createSession";

		var actions = new Array();
		actions['苹果Auth(appleauth)'] = account_actions;

		function buildField() {
			var html = "";
			html += '<dd class="inp"><input type="text"  class="key wid1" name="key" placeholder="key">：';
			html += '<input type="text" class="val wid2" name="val"  placeholder="value">';
			html += '<span href="javascript:;" class="op_closes" action-type="delrow">X</span></dd>';
			$(".addbox").append(html);
		}

		function go() {
			var server = $("#serverName").val();
			var jsonStr = "";
			var paramInps = $(".addbox .inp");
			var postData = {};
			paramInps.each(function(i) {
				key = paramInps.eq(i).children(".key").val();
				if (key.trim() != "") {
					if (i > 0) {
						jsonStr += '&';
					}
					//jsonStr += key.trim();
					val = paramInps.eq(i).children(".val").val();
					postData[key.trim()] = val;
					//jsonStr += '=' + val;
				}
			});

			jsonStr = jsonStr.replace(/\n/gi, '&');
			var kk = encodeURIComponent('#');
			jsonStr = jsonStr.replace(/#/gi, kk);

			var actionName = $("#apiaction").val();

			if (actionName.indexOf("create") > 0
					|| actionName.indexOf("update") > 0
					|| actionName.indexOf("destroy") > 0
					|| actionName.indexOf("remove") > 0
					|| actionName.indexOf("dispose") > 0
					|| actionName.indexOf("add") > 0) {
				var r = confirm("如果是现网环境，请不要轻易做更改操作,要继续执行请确定!");
				if (r == false) {
					return;
				}
			}

			actionName = actionName + ".json";
			url = server + actionName;
			$("#url").text(url);

			//var form = $("#form");
			//form.innerHtml = '';
			var form = document.createElement("form");
			form.setAttribute("action", url);
			form.setAttribute("method", "post");
			form.setAttribute("target", "resultFrame");
			var innerHtml="";
			for (item in postData) {
				innerHtml+= "<input type='hidden' name = '" + item + "' value = '" + postData[item] + "' />";
			}

			form.innerHTML = innerHtml;
			form.submit();
		}

		function onApiActionSelChange() {
			apiKey = $("#apitype").val();
			actionKey = this.value;

			subActions = actions[apiKey];
			actionParam = subActions[actionKey];

			//build param div
			paramArray = actionParam.split("&");
			$(".addbox").html("");
			for (var i = 0; i < paramArray.length; i++) {
				buildField();
				$(".inp").eq(i).children(".key").val(
						paramArray[i].split("=")[0]).end().children(".val")
						.val(paramArray[i].split("=")[1]);
			}

		}

		function onApiTypeSelChange() {
			//get subaction
			key = this.value;
			subActions = actions[key];
			var apiactionSel = $("#apiaction");
			apiactionSel.html('');
			for (key in subActions) {
				var opt = "<option value='" + key + "'>" + key + "</option>";
				$(apiactionSel).append(opt);
			}

			//apiaction select
			$(apiactionSel).change(onApiActionSelChange).trigger('change');
		}

		$(function() {
			//init apitype select 
			var apitypeSel = $("#apitype");
			for (key in actions) {
				var value = actions[key];
				var opt = "<option value='" + key + "'>" + key + "</option>";
				$(apitypeSel).append(opt);
			}

			$(apitypeSel).change(onApiTypeSelChange).trigger('change');

			$(".op_closes").live("click", function() {
				$(this).parents(".inp").remove();
			});

			$(".add-input").click(buildField);

		});
	</script>
</body>
</html>
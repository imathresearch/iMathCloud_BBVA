var MSG_EXECUTE_CODE = "EXE";
var MSG_EXECUTE_CODE_R = "EXR";		// message to execute the sent code to R console
var MSG_DEFAULT_LANGUAGE = "LAN";
var MSG_DEFAULT_USER_ENVIRONMENT = "ENV";

function executeInConsole(str) {
	sendMessage(MSG_EXECUTE_CODE, str);
}

function executeInConsoleR(str) {
	sendMessage(MSG_EXECUTE_CODE_R, str);
}

function setDefaultLanguage(str) {
	sendMessage(MSG_DEFAULT_LANGUAGE, str);
}

function setEnvironmentVariable(str) {
	console.log("setEnvironmentVariable " + str);
	sendMessage(MSG_DEFAULT_USER_ENVIRONMENT, str + ";" + userName);
}

function sendMessage(msgType, content) {
	iframeObj = document.getElementById("interactive_math");
	var win = iframeObj.contentWindow;
	console.log("sendMessage" + msgType + content);
	console.log("sendMessage" + urlConsole);
	win.postMessage(msgType + content, urlConsole);
	divTabs = iframeObj.parentNode.parentNode;
	$("#" + divTabs.id + " a")[0].click(); // here we click to the first tab. We guess it is the console always.
}
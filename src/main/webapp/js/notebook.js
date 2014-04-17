var MSG_EXECUTE_CODE = "EXE";
var MSG_EXECUTE_CODE_R = "EXR";		// message to execute the sent code to R console
var MSG_DEFAULT_LANGUAGE = "LAN";

function executeInConsole(str) {
	sendMessage(MSG_EXECUTE_CODE, str);
}

function executeInConsoleR(str) {
	sendMessage(MSG_EXECUTE_CODE_R, str);
}

function setDefaultLanguage(str) {
	sendMessage(MSG_DEFAULT_LANGUAGE, str);
}

function sendMessage(msgType, content) {
	var win = document.getElementById("interactive_math").contentWindow;
	win.postMessage(msgType + content, urlConsole);
}
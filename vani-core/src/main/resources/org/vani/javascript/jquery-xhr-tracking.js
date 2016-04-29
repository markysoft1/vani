if(!window.vani){
	window.vani = {};
}

window.vani.xhrTracking = {
	startedRequests: [],
	hasRequestFor: function(url, start){
		var regex = new RegExp(url);
		var result = false;
		for(i in window.vani.xhrTracking.startedRequests){
			var xhr = window.vani.xhrTracking.startedRequests[i];
			if(xhr.timestamp > start && regex.test(xhr.settings.url)){
				result = true;
				break;
			}
		}
		return result;
	}
};

if(typeof jQuery !== 'undefined'){
	$(document).ajaxSend(function( event, jqxhr, settings ) {
		var timestamp = Date.now();
		window.vani.xhrTracking.startedRequests.push(
				{settings: settings, timestamp: timestamp}
		);
	});
}
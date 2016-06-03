function(ref,functionName,arguments){
	var target = $('html');
	if(ref && window[ref]){
		target = window[ref];
	}
	var result = target[functionName].apply(target,arguments);
	
	if(result instanceof jQuery){
		var resultRef = 'vani.jqueryCache.' + vani.uuid4();
		window[resultRef] = result;
		return resultRef;
	}
	return result;
}
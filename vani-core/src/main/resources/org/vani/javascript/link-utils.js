if(!window.vani){
	window.vani = {};
}

window.vani.linkUtils = {
	getApplicableUrls: function(patterns){
		var result = [];
		for(i in patterns){
			var pattern = patterns[i];
			var links = $('a:regex(prop:href,' + pattern + ')');
			links.each(function(index,element){
				result.push($(element).prop('href'));
			});
		}
		
		return result;
	}
};
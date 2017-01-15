var exec = require('cordova/exec');

module.exports = {
	videostream: function(cameraId, user, pass, onSuccess, onError){
		exec(onSuccess, onError, "Vstarcam", "videostream", [cameraId, user, pass]);
	}
};

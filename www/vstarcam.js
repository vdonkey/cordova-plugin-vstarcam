var exec = require('cordova/exec');

module.exports = {
	videostream: function(cameraId, user, pass, onSuccess, onError){
		console.log('videostream-params:'+cameraId+','+ user + ',' + pass);
		exec(onSuccess, onError, "Vstarcam", "videostream", [cameraId, user, pass]);
	}
};

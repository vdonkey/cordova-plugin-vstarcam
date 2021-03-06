package com.icloris.vstarcam;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONException;

import vstc2.nativecaller.NativeCaller;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.icloris.vstarcam.BridgeService.IpcamClientInterface;
import com.icloris.vstarcam.BridgeService.PlayInterface;

public class Vstarcam extends CordovaPlugin implements IpcamClientInterface,
		PlayInterface {
	public static Vstarcam instance = null;
	protected String cid = "";
	protected String usr = "";
	protected String pwd = "";
	protected CallbackContext vsCallback;

	@Override
	protected void pluginInitialize() {
		super.pluginInitialize();
		instance = this;
	}

	@Override
	public boolean execute(String action, CordovaArgs args,
			CallbackContext callbackContext) throws JSONException {
		callbackContext.success(200);
		Log.e("vstar", "execute: " + action);
		//webView.loadUrl("javascript:console.log('execute: " + action + "');");

		if (action.equals("videostream")) {
			return videostream(args, callbackContext);
		}

		return false;
	}

	protected boolean videostream(CordovaArgs args,
			final CallbackContext callbackContext) {
		try {
			cid = args.getString(0);
			usr = args.getString(1);
			pwd = args.getString(2);
			vsCallback = callbackContext;

			callbackContext.success(100);
			webView.loadUrl("javascript:console.log('cid,usr,pwd:" + cid + ","
					+ usr + "," + pwd + "');");
		} catch (JSONException e) {
			return false;
		}

		webView.loadUrl("javascript:console.log('starting service');");
		// 启动回调service
		Intent intent = new Intent();
		intent.setClass(webView.getContext(), BridgeService.class);
		webView.getContext().startService(intent);
		webView.loadUrl("javascript:console.log('started service');");
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					webView.loadUrl("javascript:console.log('initing native');");
					NativeCaller
							.PPPPInitialOther("ADCBBFAOPPJAHGJGBBGLFLAGDBJJHNJGGMBFBKHIBBNKOKLDHOBHCBOEHOKJJJKJBPMFLGCPPJMJAPDOIPNL");
					Thread.sleep(3000);
					webView.loadUrl("javascript:console.log('sending message');");
					Message msg = new Message();
					mHandler.sendMessage(msg);
					webView.loadUrl("javascript:console.log('sent message');");
				} catch (Exception e) {

				}
			}
		}).start();
		return true;
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			// 3.设定连接摄像头的回调
			webView.loadUrl("javascript:console.log('get message');");
			BridgeService.setIpcamClientInterface(Vstarcam.this);

			// 4.初始化NativeCaller
			NativeCaller.Init();
			webView.loadUrl("javascript:console.log('inited native');");

			new Thread(new Runnable() {
				@Override
				public void run() {
					// 5.发送摄像头连接请求
					webView.loadUrl("javascript:console.log('starting pppp:"
							+ cid + "');");
					NativeCaller.StartPPPP(cid, usr, pwd, 1, "");
				}
			}).start();
		}
	};

	private Handler PPPPMsgHandler = new Handler() {
		public void handleMessage(Message msg) {
			webView.loadUrl("javascript:console.log('got message pppp');");
			Bundle bd = msg.getData();
			int msgParam = bd.getInt("a");
			int msgType = msg.what;
			String did = bd.getString("b");
			switch (msgType) {
			case ContentCommon.PPPP_MSG_TYPE_PPPP_STATUS:
				switch (msgParam) {
				case ContentCommon.PPPP_STATUS_CONNECTING:// 0
					break;
				case ContentCommon.PPPP_STATUS_CONNECT_FAILED:// 3
					break;
				case ContentCommon.PPPP_STATUS_DISCONNECT:// 4
					break;
				case ContentCommon.PPPP_STATUS_INITIALING:// 1
					break;
				case ContentCommon.PPPP_STATUS_INVALID_ID:// 5
					break;
				case ContentCommon.PPPP_STATUS_ON_LINE:// 2 在线状态
					webView.loadUrl("javascript:console.log('online status');");
					// 7.连接摄像头在线，即可以对摄像头进行操作，设定打开摄像头视频回调
					BridgeService.setPlayInterface(Vstarcam.this);
					// 8.发送打开摄像头视频请求
					webView.loadUrl("javascript:console.log('starting live stream');");
					NativeCaller.StartPPPPLivestream(cid, 10, 1);
					break;
				case ContentCommon.PPPP_STATUS_DEVICE_NOT_ON_LINE:// 6
					break;
				case ContentCommon.PPPP_STATUS_CONNECT_TIMEOUT:// 7
					break;
				case ContentCommon.PPPP_STATUS_CONNECT_ERRER:// 8
					break;
				default:
				}

				if (msgParam == ContentCommon.PPPP_STATUS_INVALID_ID
						|| msgParam == ContentCommon.PPPP_STATUS_CONNECT_FAILED
						|| msgParam == ContentCommon.PPPP_STATUS_DEVICE_NOT_ON_LINE
						|| msgParam == ContentCommon.PPPP_STATUS_CONNECT_TIMEOUT
						|| msgParam == ContentCommon.PPPP_STATUS_CONNECT_ERRER) {
					NativeCaller.StopPPPP(did);
				}
				break;
			case ContentCommon.PPPP_MSG_TYPE_PPPP_MODE:
				break;
			}

		}
	};

	@Override
	public void BSMsgNotifyData(String did, int type, int param) {
		// 6.摄像头连接结果
		Bundle bd = new Bundle();
		Message msg = PPPPMsgHandler.obtainMessage();
		msg.what = type;
		bd.putInt("a", param);
		bd.putString("b", did);
		msg.setData(bd);
		PPPPMsgHandler.sendMessage(msg);
	}

	@Override
	public void BSSnapshotNotify(String did, byte[] bImage, int len) {
	}

	@Override
	public void callBackUserParams(String did, String user1, String pwd1,
			String user2, String pwd2, String user3, String pwd3) {
	}

	@Override
	public void CameraStatus(String did, int status) {
	}

	@Override
	public void callBackCameraParamNotify(String did, int resolution,
			int brightness, int contrast, int hue, int saturation, int flip,
			int mode) {
	}

	@Override
	public void callBackVideoData(byte[] videobuf, int h264Data, int len,
			int width, int height) {
		// 9.摄像头视频数据，在这儿可以对返回的h264Data进行解析，然后显示到画面上了
		// Log.e("weim", "----4");
		// Log.e("weim", "len"+len);
		// Log.e("weim", "width"+width+"height"+height);
		// Log.e("weim", "videobuf:"+videobuf);
		// Log.e("weim", "h264Data"+h264Data);
		webView.loadUrl("javascript:console.log('callback video:" + len + "');");
		vsCallback.success(len);
	}

	@Override
	public void callBackMessageNotify(String did, int msgType, int param) {
	}

	@Override
	public void callBackAudioData(byte[] pcm, int len) {
	}

	@Override
	public void callBackH264Data(byte[] h264, int type, int size) {
	}
}

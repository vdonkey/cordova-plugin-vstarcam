package com.icloris.vstarcam;

import vstc2.nativecaller.NativeCaller;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class BridgeService extends Service
{
	private String TAG = BridgeService.class.getSimpleName();
	private Notification mNotify2;
	private NotificationManager notifyManager ;

	@Override
	public IBinder onBind(Intent intent) {
		Log.d("tag", "BridgeService onBind()");
		return new ControllerBinder();
	}

	class ControllerBinder extends Binder {
		public BridgeService getBridgeService() {
			return BridgeService.this;
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d("tag", "BridgeService onCreate()");
		notifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		NativeCaller.PPPPSetCallbackContext(this);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
//		notifyManager.cancel(R.drawable.app);
	}

	/**
	 * 
	 * PlayActivity feedback method
	 * 
	 * jni
	 * 
	 * @param videobuf
	 * 
	 * @param h264Data
	 * 
	 * @param len
	 * 
	 * @param width
	 * 
	 * @param height
	 * 
	 */

	public void VideoData(String did, byte[] videobuf, int h264Data, int len,
			int width, int height, int timestamp, short milistamp, int sessid,
			int version, int originFrameLen) {
		Log.d(TAG, "BridgeService----Call VideoData 视频数据返回...h264Data: "
				+ h264Data + " len: " + len + " videobuf len: " + len
				+ "width: " + width + "height: " + height + ",did:" + did
				+ ",sessid:" + sessid + ",version:" + version);

		if (playInterface != null) {
			playInterface.callBackVideoData(videobuf, h264Data, len, width,height);
		}
	}

	public void CallBack_H264Data(String did, byte[] h264, int type, int size,
			int timestamp, short milistamp, int sessid, int version) {
		Log.w(TAG, "H264 数据返回:" + did + "," + h264.length + ",type:" + type
				+ ",size:" + size + ",time:" + timestamp + ",did:" + did
				+ ",sessid:" + sessid + ",version:" + version);
		if (playInterface != null) {
			playInterface.callBackH264Data(h264, type, size);
		}
	}

	@SuppressWarnings("unused")
	/**
	 * PlayActivity feedback method
	 * 
	 * PPPP
	 * @param did
	 * @param msgType
	 * @param param
	 */
	private void MessageNotify(String did, int type, int param) {
//		if (playInterface != null) {
//			playInterface.callBackMessageNotify(did, msgType, param);
//		}
		if (ipcamClientInterface != null) {
			ipcamClientInterface.BSMsgNotifyData(did, type, param);
		}
		if (wifiInterface != null) {
			wifiInterface.callBackPPPPMsgNotifyData(did, type, param);
		}

		if (userInterface != null) {
			userInterface.callBackPPPPMsgNotifyData(did, type, param);

		}
	}

	/**
	 * PlayActivity feedback method
	 * 
	 * AudioData
	 * 
	 * @param pcm
	 * @param len
	 */
	public void AudioData(byte[] pcm, int len) {
		Log.d(TAG, "AudioData: len :+ " + len);
		if (playInterface != null) {
			playInterface.callBackAudioData(pcm, len);
		}
	}

	/**
	 * IpcamClientActivity feedback method
	 * 
	 * p2p statu
	 * 
	 * @param msgtype
	 * @param param
	 */
	public void PPPPMsgNotify(String did, int type, int param) {
		Log.d(TAG, "PPPPMsgNotify  did:" + did + " type:" + type + " param:"
				+ param);
		if (ipcamClientInterface != null) {
			ipcamClientInterface.BSMsgNotifyData(did, type, param);
		}
		if (wifiInterface != null) {
			wifiInterface.callBackPPPPMsgNotifyData(did, type, param);
		}

		if (userInterface != null) {
			userInterface.callBackPPPPMsgNotifyData(did, type, param);

		}
	}

	/***
	 * SearchActivity feedback method
	 * 
	 * **/
	
	public void SearchResult(String sysVer, String appVer, String strMac,
			String strName, String strDeviceID, String strIpAddr, int port) {
		
		if (strDeviceID.length() == 0) {
			return;
		}
		if (addCameraInterface != null) {
			addCameraInterface.callBackSearchResultData(0, strMac,
					strName, strDeviceID, strIpAddr, port);
		}

	}

	// ======================callback==================================================
	/**
	 * 
	 * @param paramType
	 * @param result
	 *            0:fail 1sucess
	 */
	public void CallBack_SetSystemParamsResult(String did, int paramType,int result)
	{
		switch (paramType) {
		case ContentCommon.MSG_TYPE_SET_WIFI:
			if (wifiInterface != null) {
				wifiInterface.callBackSetSystemParamsResult(did, paramType,result);
			}
			break;
		case ContentCommon.MSG_TYPE_SET_USER:
			if (userInterface != null) {
				userInterface.callBackSetSystemParamsResult(did, paramType,result);
			}
			break;
		case ContentCommon.MSG_TYPE_SET_ALARM:
			if (alarmInterface != null) {
				// Log.d(TAG,"user result:"+result+" paramType:"+paramType);
				alarmInterface.callBackSetSystemParamsResult(did, paramType,result);
			}
			break;
		case ContentCommon.MSG_TYPE_SET_MAIL:
			if (mailInterface != null) 
			{
				mailInterface.callBackSetSystemParamsResult(did, paramType,
						result);
			}
			break;
		case ContentCommon.MSG_TYPE_SET_FTP:
			if (ftpInterface != null) {
				ftpInterface.callBackSetSystemParamsResult(did, paramType,result);
			}
			break;
		case ContentCommon.MSG_TYPE_SET_DATETIME:
			if (dateTimeInterface != null) {
				Log.d(TAG, "user result:" + result + " paramType:" + paramType);
				dateTimeInterface.callBackSetSystemParamsResult(did, paramType,result);
			}
			break;
		case ContentCommon.MSG_TYPE_SET_RECORD_SCH:
			if (sCardInterface != null) {
				sCardInterface.callBackSetSystemParamsResult(did, paramType,result);
			}
			break;
		default:
			break;
		}
	}
	
	public void CallBackTransCMDString(String did, String cgi_str)
	{
		
		Log.e("callback_CGI", cgi_str);
     
	}

	public void CallBack_CameraParams(String did, int resolution,int brightness, int contrast, int hue, int saturation, int flip,
			int fram, int mode)
	{
		Log.d("ddd", "CallBack_CameraParams");
		if (playInterface != null)
		{
			playInterface.callBackCameraParamNotify(did, resolution,brightness, contrast, hue, saturation, flip,mode);
		}
	}
	
	public void P2PRawDataNotify(String did, byte[] data, int datalen,
			int serialno) {

	}
	
	public void P2PRawDataSendStatusNotify(String did, int serialno, int len) {

	}

	public void CallBack_WifiParams(String did, int enable, String ssid,
			int channel, int mode, int authtype, int encryp, int keyformat,
			int defkey, String key1, String key2, String key3, String key4,
			int key1_bits, int key2_bits, int key3_bits, int key4_bits,
			String wpa_psk)
	{
		Log.d("ddd", "CallBack_WifiParams");
		if (wifiInterface != null) {
			wifiInterface.callBackWifiParams(did, enable, ssid, channel, mode,
					authtype, encryp, keyformat, defkey, key1, key2, key3,
					key4, key1_bits, key2_bits, key3_bits, key4_bits, wpa_psk);
		}
	}

	public void CallBack_UserParams(String did, String user1, String pwd1,
			String user2, String pwd2, String user3, String pwd3)
	{
		Log.d("ddd", "CallBack_UserParams");
		if (userInterface != null) {
			userInterface.callBackUserParams(did, user1, pwd1, user2, pwd2,
					user3, pwd3);
		}
		if (ipcamClientInterface != null) {
			ipcamClientInterface.callBackUserParams(did, user1, pwd1, user2,
					pwd2, user3, pwd3);
		}
	}

	public void CallBack_FtpParams(String did, String svr_ftp, String user,
			String pwd, String dir, int port, int mode, int upload_interval)
	{
		if (ftpInterface != null) {
			ftpInterface.callBackFtpParams(did, svr_ftp, user, pwd, dir, port,
					mode, upload_interval);
		}
	}

	public void CallBack_DDNSParams(String did, int service, String user,
			String pwd, String host, String proxy_svr, int ddns_mode,int proxy_port)
	{
		Log.d("ddd", "CallBack_DDNSParams");
	}

	public void CallBack_MailParams(String did, String svr, int port,
			String user, String pwd, int ssl, String sender, String receiver1,
			String receiver2, String receiver3, String receiver4)
	{
		if (mailInterface != null) {
			mailInterface.callBackMailParams(did, svr, port, user, pwd, ssl,
					sender, receiver1, receiver2, receiver3, receiver4);
		}
	}

	public void CallBack_DatetimeParams(String did, int now, int tz,
			int ntp_enable, String ntp_svr)
	{
		if (dateTimeInterface != null) {
			dateTimeInterface.callBackDatetimeParams(did, now, tz, ntp_enable,
					ntp_svr);
		}
	}

	/**
	 * IpcamClientActivity feedback method
	 * 
	 * snapshot result
	 * 
	 * @param did
	 * @param bImage
	 * @param len
	 */
	public void PPPPSnapshotNotify(String did, byte[] bImage, int len) {
		Log.d(TAG, "PPPPSnapshotNotify  did:" + did + " len:" + len);
		if (ipcamClientInterface != null) {
			ipcamClientInterface.BSSnapshotNotify(did, bImage, len);
		}
	}

	public void CallBack_Snapshot(String did, byte[] data, int len)
	{
		if (ipcamClientInterface != null) {
			ipcamClientInterface.BSSnapshotNotify(did, data, len);
		}

	}

	public void CallBack_NetworkParams(String did, String ipaddr,
			String netmask, String gateway, String dns1, String dns2, int dhcp,
			int port, int rtsport) {
		Log.d("ddd", "CallBack_NetworkParams");
	}

	public void CallBack_CameraStatusParams(String did, String sysver,
			String devname, String devid, String appver, String oemid,
			int alarmstatus, int sdcardstatus, int sdcardtotalsize,
			int sdcardremainsize) {

		if (ipcamClientInterface != null) {
			ipcamClientInterface.CameraStatus(did, alarmstatus);
		}
		if (updatefirmware != null) 
		{
			Log.i("info", "othersSettingActivity");
			updatefirmware.CallBack_UpdateFirmware(did,sysver, appver, oemid);
		}
	}

	public void CallBack_PTZParams(String did, int led_mod,
			int ptz_center_onstart, int ptz_run_times, int ptz_patrol_rate,
			int ptz_patrul_up_rate, int ptz_patrol_down_rate,
			int ptz_patrol_left_rate, int ptz_patrol_right_rate,
			int disable_preset) {
		Log.d("ddd", "CallBack_PTZParams");
	}

	public void CallBack_WifiScanResult(String did, String ssid, String mac,
			int security, int dbm0, int dbm1, int mode, int channel, int bEnd) {
		Log.d("tag", "CallBack_WifiScanResult");
		if (wifiInterface != null) {
			wifiInterface.callBackWifiScanResult(did, ssid, mac, security,
					dbm0, dbm1, mode, channel, bEnd);
		}
	}

	public void CallBack_AlarmParams(String did, int alarm_audio,
			int motion_armed, int motion_sensitivity, int input_armed,
			int ioin_level, int iolinkage, int ioout_level, int alarmpresetsit,
			int mail, int snapshot, int record, int upload_interval,
			int schedule_enable, int schedule_sun_0, int schedule_sun_1,
			int schedule_sun_2, int schedule_mon_0, int schedule_mon_1,
			int schedule_mon_2, int schedule_tue_0, int schedule_tue_1,
			int schedule_tue_2, int schedule_wed_0, int schedule_wed_1,
			int schedule_wed_2, int schedule_thu_0, int schedule_thu_1,
			int schedule_thu_2, int schedule_fri_0, int schedule_fri_1,
			int schedule_fri_2, int schedule_sat_0, int schedule_sat_1,
			int schedule_sat_2, int defense_plan1, int defense_plan2,
			int defense_plan3, int defense_plan4, int defense_plan5,
			int defense_plan6, int defense_plan7, int defense_plan8,
			int defense_plan9, int defense_plan10, int defense_plan11,
			int defense_plan12, int defense_plan13, int defense_plan14,
			int defense_plan15, int defense_plan16, int defense_plan17,
			int defense_plan18, int defense_plan19, int defense_plan20,
			int defense_plan21,int remind_rare) {

		if (alarmInterface  != null) {
			alarmInterface.callBackAlarmParams(did,alarm_audio ,motion_armed,
					motion_sensitivity, input_armed, ioin_level, iolinkage,
					ioout_level, alarmpresetsit, mail, snapshot, record,
					upload_interval, schedule_enable, schedule_sun_0,
					schedule_sun_1, schedule_sun_2, schedule_mon_0,
					schedule_mon_1, schedule_mon_2, schedule_tue_0,
					schedule_tue_1, schedule_tue_2, schedule_wed_0,
					schedule_wed_1, schedule_wed_2, schedule_thu_0,
					schedule_thu_1, schedule_thu_2, schedule_fri_0,
					schedule_fri_1, schedule_fri_2, schedule_sat_0,
					schedule_sat_1, schedule_sat_2);
		}
	}
	/*
	 * @param alarmtype==0x14(20) 为可视门铃按钮动作
	 *
	 */

	public void CallBack_AlarmNotify(String did, int alarmtype) {
		Log.d("tag", "callBack_AlarmNotify did:" + did + " alarmtype:"
				+ alarmtype);
		switch (alarmtype) {
		case ContentCommon.MOTION_ALARM:// 移动侦测报警
//			String strMotionAlarm = getResources().getString(
//					R.string.alerm_motion_alarm);
//			getNotification(strMotionAlarm, did, true);
			break;
		case ContentCommon.GPIO_ALARM:
//			String strGpioAlarm = getResources().getString(
//					R.string.alerm_gpio_alarm);
//			getNotification(strGpioAlarm, did, true);
			break;
		case ContentCommon.ALARM_DOORBELL:
			//此处编写按下门铃需要执行的动作
			getNotification("门铃来了", did, false);
			break;
		case ContentCommon.HIGHTEMP_ALARM://高温报警
		case ContentCommon.LOWTEMP_ALARM://低温报警
		case ContentCommon.LOWPOWER_ALARM://低电报警
		case ContentCommon.CRY_ALARM://哭声报警
				break;			
		default:
			break;
		}

	}

	public void CallBack_RecordFileSearchResult(String did, String filename,
			int size, int recordcount, int pagecount, int pageindex,
			int pagesize, int bEnd) {
		Log.d("info", "CallBack_RecordFileSearchResult did: " + did
				+ " filename: " + filename + " size: " + size);
		if (playBackTFInterface != null) {
			playBackTFInterface.callBackRecordFileSearchResult(did, filename,
					size, recordcount, pagecount, pageindex, pagesize, bEnd);
		}
	}

	public void CallBack_PlaybackVideoData(String did, byte[] videobuf,
			int h264Data, int len, int width, int height, int time,
			int streamid, int FrameType, int originFrameLen) {
		Log.d(TAG, "CallBack_PlaybackVideoData  len:" + len + " width:" + width
				+ " height:" + height);
		if (playBackInterface != null) {
			playBackInterface.callBackPlaybackVideoData(videobuf, h264Data,
					len, width, height,time,FrameType,originFrameLen);
		}
	}

	/*
	 * 录像回放参数回调
	 */
	public void CallBack_RecordSchParams(String did, int record_cover_enable,
			int record_timer, int record_size, int record_time_enable,int record_chnl,
			int record_schedule_sun_0, int record_schedule_sun_1,
			int record_schedule_sun_2, int record_schedule_mon_0,
			int record_schedule_mon_1, int record_schedule_mon_2,
			int record_schedule_tue_0, int record_schedule_tue_1,
			int record_schedule_tue_2, int record_schedule_wed_0,
			int record_schedule_wed_1, int record_schedule_wed_2,
			int record_schedule_thu_0, int record_schedule_thu_1,
			int record_schedule_thu_2, int record_schedule_fri_0,
			int record_schedule_fri_1, int record_schedule_fri_2,
			int record_schedule_sat_0, int record_schedule_sat_1,
			int record_schedule_sat_2, int record_sd_status, int sdtotal,
			int sdfree,int enable_audio) {
		if (sCardInterface != null) {
			sCardInterface.callBackRecordSchParams(did, record_cover_enable,
					record_timer, record_size, record_time_enable,
					record_schedule_sun_0, record_schedule_sun_1,
					record_schedule_sun_2, record_schedule_mon_0,
					record_schedule_mon_1, record_schedule_mon_2,
					record_schedule_tue_0, record_schedule_tue_1,
					record_schedule_tue_2, record_schedule_wed_0,
					record_schedule_wed_1, record_schedule_wed_2,
					record_schedule_thu_0, record_schedule_thu_1,
					record_schedule_thu_2, record_schedule_fri_0,
					record_schedule_fri_1, record_schedule_fri_2,
					record_schedule_sat_0, record_schedule_sat_1,
					record_schedule_sat_2, record_sd_status, sdtotal, sdfree,enable_audio);
		}
		Log.e(TAG, "录像计划:record_schedule_sun_0=" + record_schedule_sun_0
				+ ",record_schedule_sun_1=" + record_schedule_sun_1
				+ ",record_schedule_sun_2=" + record_schedule_sun_2
				+ ",record_schedule_mon_0=" + record_schedule_mon_0
				+ ",record_schedule_mon_1=" + record_schedule_mon_1
				+ ",record_schedule_mon_2=" + record_schedule_mon_2);
	}
	
//	public void setUpdateFirmware(FirmwareUpdateActiviy  activity,String did) 
//	{
//		this.othersSettingActivity=activity;
//		NativeCaller.PPPPGetSystemParams(did, ContentCommon.MSG_TYPE_GET_STATUS);//获取版本的本地方法
//	}

	//固件更新接口
	private static Firmware updatefirmware;
	public static void setFirmware(Firmware firmware)
	{
		updatefirmware=firmware;
	}
	public interface Firmware
	{
		void CallBack_UpdateFirmware(String did,String sysver, String appver,String oemid);
	}
	
	//通知
	@SuppressWarnings("deprecation")
	private Notification getNotification(String content, String did,boolean isAlarm)
	{
//		mNotify2 = new Notification(R.drawable.app,content, System.currentTimeMillis());
//		mNotify2.defaults |= Notification.DEFAULT_SOUND;//声音
//		mNotify2.setLatestEventInfo(BridgeService.this, "This is content title",
//                "This is content text", null);
		
		notifyManager.notify(1, mNotify2);
		return mNotify2;
	}

	private static IpcamClientInterface ipcamClientInterface;
	public static void setIpcamClientInterface(IpcamClientInterface ipcInterface)
	{
		ipcamClientInterface = ipcInterface;
	}
	public interface IpcamClientInterface {
		void BSMsgNotifyData(String did, int type, int param);

		void BSSnapshotNotify(String did, byte[] bImage, int len);

		void callBackUserParams(String did, String user1, String pwd1,
				String user2, String pwd2, String user3, String pwd3);

		void CameraStatus(String did, int status);
	}

	
	private static PictureInterface pictureInterface;
	public static void setPictureInterface(PictureInterface pi) {
		pictureInterface = pi;
	}

	public interface PictureInterface {
		void BSMsgNotifyData(String did, int type, int param);
	}

	private static VideoInterface videoInterface;

	public static void setVideoInterface(VideoInterface vi)
	{
		videoInterface = vi;
	}

	public interface VideoInterface {
		void BSMsgNotifyData(String did, int type, int param);
	}

	private static WifiInterface wifiInterface;

	public static void setWifiInterface(WifiInterface wi) {
		wifiInterface = wi;
	}

	public interface WifiInterface {
		void callBackWifiParams(String did, int enable, String ssid,
				int channel, int mode, int authtype, int encryp, int keyformat,
				int defkey, String key1, String key2, String key3, String key4,
				int key1_bits, int key2_bits, int key3_bits, int key4_bits,
				String wpa_psk);

		void callBackWifiScanResult(String did, String ssid, String mac,
				int security, int dbm0, int dbm1, int mode, int channel,
				int bEnd);

		void callBackSetSystemParamsResult(String did, int paramType, int result);

		void callBackPPPPMsgNotifyData(String did, int type, int param);
	}

	private static UserInterface userInterface;

	public static void setUserInterface(UserInterface ui) {
		userInterface = ui;
	}

	public interface UserInterface {
		void callBackUserParams(String did, String user1, String pwd1,
				String user2, String pwd2, String user3, String pwd3);

		void callBackSetSystemParamsResult(String did, int paramType, int result);

		void callBackPPPPMsgNotifyData(String did, int type, int param);
	}

	private static AlarmInterface alarmInterface;

	public static void setAlarmInterface(AlarmInterface ai) {
		alarmInterface = ai;
	}

	public interface AlarmInterface {
		void callBackAlarmParams(String did, int motion_armed,
				int motion_sensitivity, int input_armed, int ioin_level,
				int iolinkage, int ioout_level, int alermpresetsit, int mail,
				int snapshot, int record, int upload_interval,
				int schedule_enable, int schedule_sun_0, int schedule_sun_1,
				int schedule_sun_2, int schedule_mon_0, int schedule_mon_1,
				int schedule_mon_2, int schedule_tue_0, int schedule_tue_1,
				int schedule_tue_2, int schedule_wed_0, int schedule_wed_1,
				int schedule_wed_2, int schedule_thu_0, int schedule_thu_1,
				int schedule_thu_2, int schedule_fri_0, int schedule_fri_1,
				int schedule_fri_2, int schedule_sat_0, int schedule_sat_1,
				int schedule_sat_2, int schedule_sat_22);

		void callBackSetSystemParamsResult(String did, int paramType, int result);
	}

	private static DateTimeInterface dateTimeInterface;

	public static void setDateTimeInterface(DateTimeInterface di) {
		dateTimeInterface = di;
	}

	public interface DateTimeInterface {
		void callBackDatetimeParams(String did, int now, int tz,
				int ntp_enable, String ntp_svr);

		void callBackSetSystemParamsResult(String did, int paramType, int result);
	}

	private static MailInterface mailInterface;

	public static void setMailInterface(MailInterface mi) {
		mailInterface = mi;
	}

	public interface MailInterface {
		void callBackMailParams(String did, String svr, int port, String user,
				String pwd, int ssl, String sender, String receiver1,
				String receiver2, String receiver3, String receiver4);

		void callBackSetSystemParamsResult(String did, int paramType, int result);
	}

	private static FtpInterface ftpInterface;

	public static void setFtpInterface(FtpInterface fi) {
		ftpInterface = fi;
	}

	public interface FtpInterface {
		void callBackFtpParams(String did, String svr_ftp, String user,
				String pwd, String dir, int port, int mode, int upload_interval);

		void callBackSetSystemParamsResult(String did, int paramType, int result);
	}

	private static SDCardInterface sCardInterface;

	public static void setSDCardInterface(SDCardInterface si) {
		sCardInterface = si;
	}

	public interface SDCardInterface {
		void callBackRecordSchParams(String did, int record_cover_enable,
				int record_timer, int record_size, int record_time_enable,
				int record_schedule_sun_0, int record_schedule_sun_1,
				int record_schedule_sun_2, int record_schedule_mon_0,
				int record_schedule_mon_1, int record_schedule_mon_2,
				int record_schedule_tue_0, int record_schedule_tue_1,
				int record_schedule_tue_2, int record_schedule_wed_0,
				int record_schedule_wed_1, int record_schedule_wed_2,
				int record_schedule_thu_0, int record_schedule_thu_1,
				int record_schedule_thu_2, int record_schedule_fri_0,
				int record_schedule_fri_1, int record_schedule_fri_2,
				int record_schedule_sat_0, int record_schedule_sat_1,
				int record_schedule_sat_2, int record_sd_status, int sdtotal,
				int sdfree,int enable_audio);

		void callBackSetSystemParamsResult(String did, int paramType, int result);;
	}

	private static PlayInterface playInterface;

	public static void setPlayInterface(PlayInterface pi) {
		playInterface = pi;
	}

	public interface PlayInterface {
		void callBackCameraParamNotify(String did, int resolution,
				int brightness, int contrast, int hue, int saturation, int flip,int mode);

		void callBackVideoData(byte[] videobuf, int h264Data, int len,
				int width, int height);

		void callBackMessageNotify(String did, int msgType, int param);

		void callBackAudioData(byte[] pcm, int len);

		void callBackH264Data(byte[] h264, int type, int size);
	}

	public static void getPlayBackVideo(PlayBackInterface face) {
		playBackInterface = face;
	}

	private static PlayBackTFInterface playBackTFInterface;

	public static void setPlayBackTFInterface(PlayBackTFInterface pbtfi) {
		playBackTFInterface = pbtfi;
	}

	public interface PlayBackTFInterface {
		void callBackRecordFileSearchResult(String did, String filename,
				int size, int recordcount, int pagecount, int pageindex,
				int pagesize, int bEnd);
	}

	private static PlayBackInterface playBackInterface;

	public static void setPlayBackInterface(PlayBackInterface pbi) {
		playBackInterface = pbi;
	}

	public interface PlayBackInterface {
		void callBackPlaybackVideoData(byte[] videobuf, int h264Data, int len,
				int width, int height,int time,int frameType,int originGrameLen);
	}

	private static AddCameraInterface addCameraInterface;

	public static void setAddCameraInterface(AddCameraInterface aci) {
		addCameraInterface = aci;
	}

	public interface AddCameraInterface {
		void callBackSearchResultData(int cameraType, String strMac,
				String strName, String strDeviceID, String strIpAddr, int port);
	}
	
	private static SensorListActivityAllDataInterface sensorListInterfece;
	public interface SensorListActivityAllDataInterface 
	{
		void CallBackMessage(String did, String resultPbuf, int cmd,
				int sensorid1, int sensorid2, int sensorid3, int sensortype,
				int sensorstatus, int presetid, int id);
	}
	public static void setSensorListInterface(SensorListActivityAllDataInterface sensor) 
	{
		sensorListInterfece = sensor;
	}
	
	private static EditSensorListActivityInterface setEditSensor;
	public interface EditSensorListActivityInterface {
		void CallBackMessages(String did, String resultPbuf, int cmd);
	}
	public static void setSensornameInterface(EditSensorListActivityInterface sensor) {
		setEditSensor = sensor;
	}


	private static CallBackMessageInterface messageInterface;

	public static void setCallBackMessage(CallBackMessageInterface message) {
		messageInterface = message;
	}

	public interface CallBackMessageInterface {
		void CallBackGetStatus(String did, String resultPbuf, int cmd);
	}

	//
	public void CallBackTransferMessage(String did, String resultPbuf, int cmd,
			int sensorid1, int sensorid2, int sensorid3, int sensortype,
			int sensorstatus, int presetid, int index) {
		Log.e("info", "Service CallBackTransferMessage---resultPbuf:"
				+ resultPbuf + "--did:" + did + "---cmd:" + cmd + ",id1="
				+ sensorid1 + ",id2=" + sensorid2 + ",id3=" + sensorid3
				+ ",sensortype=" + sensortype + ",sensortatus=" + sensorstatus
				+ ",presetid=" + presetid + ",index:" + index);
		if (cmd == ContentCommon.CGI_GET_SENSOR_STATUS)
		{// 获取布撤防状态返回
			
		}
		if (cmd == ContentCommon.CGI_SET_SENSOR_NAME && setEditSensor != null)
		{// 编辑传感器信息返回
			setEditSensor.CallBackMessages(did, resultPbuf, cmd);
		}
		if (cmd == ContentCommon.CGI_DEL_SENSOR && setEditSensor != null)
		{// 删除传感器返回
			setEditSensor.CallBackMessages(did, resultPbuf, cmd);
		}
		if (cmd == ContentCommon.CGI_SET_SENSOR_PRESET)
		{// 设置传感器预制返回
			setEditSensor.CallBackMessages(did, resultPbuf, cmd);
		}
		if (cmd == ContentCommon.CGI_SENSOR_GETPRESET)
		{// 获取联动摄像机绑定的看守位返回

		}
		if (cmd == ContentCommon.CGI_IEGET_STATUS)
		{// 获取摄像机相关参数返回
			if (messageInterface != null) {
				messageInterface.CallBackGetStatus(did, resultPbuf, cmd);
			}
		}
		if (cmd == ContentCommon.CGI_GET_SENSOR_STATUS)
		{// 获取联动摄像机相关参数返回

		}
		//获取某一个设备已经添加的传感器列表
		if (sensorListInterfece != null
				&& cmd != ContentCommon.CGI_SET_SENSOR_PRESET
				&& cmd != ContentCommon.CGI_DEL_SENSOR
				&& cmd != ContentCommon.CGI_SET_SENSOR_PRESET
				&& cmd != ContentCommon.CGI_SET_SENSOR_STATUS
				&& cmd != ContentCommon.CGI_IEGET_STATUS
				&& cmd != ContentCommon.CGI_SENSOR_GETPRESET
				&& cmd != ContentCommon.CGI_SET_SENSOR_NAME
				&& cmd != ContentCommon.CGI_GET_SENSOR_STATUS)
		{
			sensorListInterfece.CallBackMessage(did, resultPbuf, cmd,sensorid1, sensorid2, sensorid3, sensortype, sensorstatus,presetid, index);
		}

	}

	/**
	 * 
	 * @param did
	 * @param name
	 * @param headcmd
	 * @param selfcmd
	 * @param linkpreset
	 * @param sensortype
	 * @param sensoraction
	 *            sensoraction == ContentCommon.SENSOR_ALARM_ACTION_GARRISON//
	 *            联动摄像机布防返回 sensoraction ==
	 *            ContentCommon.SENSOR_ALARM_ACTION_CANCELGARRISON// 联动摄像机撤防返回
	 *            sensoraction == ContentCommon.SENSOR_ALARM_ACTION_ALARM ||
	 *            sensoraction == ContentCommon.SENSOR_ALARM_ACTION_SOS//
	 *            联动摄像机报警 sensoraction ==
	 *            ContentCommon.SENSOR_ALARM_ACTION_LOWBATT// 联动摄像机低电
	 *            sensoraction ==
	 *            ContentCommon.SENSOR_ALARM_ACTION_CANCELALARM// 联动摄像机取消报警
	 * @param channel
	 * @param sensorid1
	 * @param sensorid2
	 * @param sensorid3
	 *            请求摄像机绑定传感器时 如果sensorid1 sensorid2 sensorid3 同时为255或者0 为无效传感器
	 */
	public void CallBackAlermMessage(String did, String name, int headcmd,
			int selfcmd, int linkpreset, int sensortype, int sensoraction,
			int channel, int sensorid1, int sensorid2, int sensorid3) {
		Log.e("info", "CallBackAlermMessage=====shix name:" + name
				+ "  headcmd:" + headcmd + "  selfcmd:" + selfcmd
				+ "  linkpreset:" + linkpreset + "  sensortype:" + sensortype
				+ "   sensoraction:" + sensoraction + "   channel:" + channel
				+ "  sensorid1:" + sensorid1 + "  sensorid2" + sensorid2
				+ "  sensorid3:" + sensorid3);

		if (sensoraction == ContentCommon.SENSOR_ALARM_ACTION_GARRISON)// 联动摄像机布防返回
		{

		}
		// 联动摄像机撤防返回
		if (sensoraction == ContentCommon.SENSOR_ALARM_ACTION_CANCELGARRISON)
		{

		}
		// 联动摄像机报警
		if (sensoraction == ContentCommon.SENSOR_ALARM_ACTION_ALARM|| sensoraction == ContentCommon.SENSOR_ALARM_ACTION_SOS)
		{

		}
		// 联动摄像机低电
		if (sensoraction == ContentCommon.SENSOR_ALARM_ACTION_LOWBATT)
		{

		}
		// 联动摄像机取消报警
		if (sensoraction == ContentCommon.SENSOR_ALARM_ACTION_CANCELALARM)
		{

		}

		if (selfcmd == ContentCommon.SENSOR_ALARM_ACTION_ALARM&& sensoraction == 8)
		{
			// 对码返回
			setCodeInterface.CallBackReCodeMessage(did, name, headcmd, selfcmd,linkpreset, sensortype,
					sensoraction, channel, sensorid1,sensorid2, sensorid3);
		}

	}
	
	//对码接口定义
	public static void setCodeInterface(SensorSetCodeInterface sensor) {
		setCodeInterface = sensor;
	}

	private static SensorSetCodeInterface setCodeInterface;

	public interface SensorSetCodeInterface
	{
		void CallBackReCodeMessage(String did, String name, int headcmd,
				int selfcmd, int linkpreset, int sensortype, int sensoraction,
				int channel, int sensorid1, int sensorid2, int sensorid3);
	}

	public void CallBackAlermLogList(String did, String alarmdvsname, int cmd,int armtype
			, int dvstype, int actiontype, int time, int nowCount,int nCount)
	
	{

	}

	public void CallBackTransferCamList(String did, String camName, int camNum,
			int bEnd, String camDid, String camUser, String camPwd) {

	}

	public void CallBackTransJson(String json) {

	}
	
	public void CallBackTransJson(String did, String json) {

	}

	public void CallBackOriFramLen(String uid, int originFrameLen) {

	}
	
	public void onTimeOut(Dialog dialog) {

	}

	public void onTransCMDString(String did, byte[] data, int lenght) {

	}

}

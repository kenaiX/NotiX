package cc.kenai.projectX.wechatreply;

import java.util.ArrayList;
import java.util.List;

public class StaticData {
	//默认自动回复的消息内容
	static String message = "您好本人在忙待会回您请稍等  【自动回复】";
	//微信6.3.18相关组件的id，微信版本更新后随之修改即可
	static String qunId = "com.tencent.mm:id/ei";
	static String editId = "com.tencent.mm:id/a71";
	static String sendId = "com.tencent.mm:id/yw";
	//是否指定好友
	static boolean isfriend = true;
	//默认指定的好友昵称
	static String friend = "在此指定一位好友";
	//是否开启自动回复
	static boolean auto = false;
	//锁屏界面是否显示消息详细内容
	static boolean showall = true;
	//是否来电或正在通话，用于是否显示锁屏界面
	static boolean iscalling = false;
	//消息总数
	static int total = 0;
	//已自动回复的消息总数
	static int replaied = 0;
	//收到的微信消息列表
	static List<String> data = new ArrayList<String>();
}

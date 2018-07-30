package cc.kenai.gamemode;

import org.junit.Test;

import java.util.ArrayList;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {


    String mWhiteList = " com.tencent.mm\n" +
            " com.tencent.mobileqq\n" +
            " com.smile.gifmaker\n" +
            " com.eg.android.AlipayGphone\n" +
            " com.taobao.taobao\n" +
            " com.ss.android.article.news\n" +
            "com.sina.weibo\n" +
            " com.tencent.qqlive\n" +
            " com.UCMobile\n" +
            " com.baidu.searchbox\n" +
            " com.ss.android.ugc.aweme\n" +
            " com.tencent.mtt\n" +
            " com.qiyi.video\n" +
            " com.kugou.android\n" +
            " com.meizu.flyme.weather\n" +
            " com.google.android.youtube\n" +
            " com.tencent.karaoke\n" +
            " com.tencent.qqmusic\n" +
            " com.meizu.media.video\n" +
            " com.android.chrome\n" +
            " com.meizu.mstore\n" +
            " com.xunmeng.pinduoduo\n" +
            " com.meizu.media.music\n" +
            " com.ss.android.article.video\n" +
            " tv.danmaku.bili\n" +
            " com.youku.phone\n" +
            " com.ss.android.ugc.live\n" +
            " com.meizu.media.reader\n" +
            " com.ss.android.article.lite\n" +
            " com.jingdong.app.mall\n" +
            " com.jifen.qukan\n" +
            " com.meizu.media.ebook\n" +
            " com.immomo.momo\n" +
            " tv.yixia.bobo\n" +
            " com.tencent.tim\n" +
            " com.tencent.news\n" +
            " com.meizu.flyme.gamecenter\n" +
            " com.alibaba.android.rimet\n" +
            " com.yandex.browser\n" +
            " com.sankuai.meituan\n" +
            " com.cashtoutiao\n" +
            " com.duowan.kiwi\n" +
            " com.sdu.didi.psnger\n" +
            " com.tencent.android.qqdownloader\n" +
            " com.baidu.netdisk\n" +
            " com.baidu.tieba\n" +
            " com.taobao.idlefish\n" +
            " air.tv.douyu.android\n" +
            " com.hexin.plat.android\n" +
            " com.tencent.qt.qtl\n" +
            " com.sdu.didi.gsui\n" +
            " com.zhihu.android\n" +
            " com.qq.reader\n" +
            " com.p1.mobile.putong\n" +
            " com.ximalaya.ting.android\n" +
            " com.campmobile.snowcamera\n" +
            " com.ushaqi.zhuishushenqi\n" +
            " com.chaozh.iReaderFree\n" +
            " com.flyme.videoclips\n" +
            " com.tencent.reading\n" +
            " com.android.email\n" +
            " cn.wps.moffice_eng\n" +
            " cn.kuwo.player\n" +
            " com.sinovatech.unicom.ui\n" +
            " cn.xiaochuankeji.tieba\n" +
            " com.xunlei.downloadprovider\n" +
            " com.shuqi.controller\n" +
            " com.baidu.searchbox.lite\n" +
            " com.lemon.faceu\n" +
            " com.taobao.qianniu\n" +
            " com.mt.mtxx.mtxx\n" +
            " com.netease.newsreader.activity\n" +
            " com.wuba\n" +
            " com.tmall.wireless\n" +
            " com.hunantv.imgo.activity\n" +
            " com.sankuai.meituan.dispatch.homebrew\n" +
            " com.kingpoint.gmcchh\n" +
            " com.tencent.gamehelper.smoba\n" +
            " cn.soulapp.android\n" +
            " com.tencent.qqsports\n" +
            " com.wuba.zhuanzhuan\n" +
            " com.mxtech.videoplayer.ad\n" +
            " sogou.mobile.explorer\n" +
            " com.soft.blued\n" +
            " com.songheng.eastnews\n" +
            " com.ss.android.article.interesting\n" +
            " com.qzone\n" +
            " com.google.android.gm\n" +
            " com.flyme.meizu.store\n" +
            " com.meitu.meiyancamera\n" +
            " com.imo.android.imoim\n" +
            " com.jx.cmcc.ict.ibelieve\n" +
            " com.sankuai.meituan.dispatch.crowdsource\n" +
            " com.benqu.wuta\n" +
            " jp.naver.line.android\n" +
            " com.hupu.games\n" +
            " com.le123.ysdq\n" +
            " com.pingan.papd\n" +
            " me.ele\n" +
            " com.coohua.xinwenzhuan\n" +
            " com.sogou.activity.src\n" +
            " com.sankuai.meituan.takeoutnew\n" +
            " com.achievo.vipshop\n" +
            " android.zhibo8\n" +
            " com.twitter.android\n" +
            " com.alibaba.aliexpresshd\n" +
            " com.kuaikan.comic\n" +
            " com.sankuai.meituan.meituanwaimaibusiness\n" +
            " com.qihoo.browser\n" +
            " com.tencent.androidqqmail\n" +
            " com.ifreetalk.ftalk\n" +
            " com.xfplay.play\n" +
            " com.eastmoney.android.berlin\n" +
            " com.qidian.QDReader\n" +
            " com.panda.videoliveplatform\n" +
            " com.qihoo.video\n" +
            " com.tencent.wework\n" +
            " com.weico.international\n" +
            " com.MobileTicket\n" +
            " com.qq.ac.android\n" +
            " com.cubic.autohome\n" +
            " com.greenpoint.android.mc10086.activity\n" +
            " com.lightsky.video\n" +
            " com.tencent.qqlite\n" +
            " fm.xiami.main\n" +
            " bubei.tingshu\n" +
            " tv.pps.mobile\n" +
            " com.badoo.mobile\n" +
            " com.tmri.app.main\n" +
            " com.xinhang.mobileclient\n" +
            " com.ygkj.chelaile.standard\n" +
            " com.zhiliaoapp.musically\n" +
            " com.duowan.mobile\n" +
            " com.quark.browser\n" +
            " com.qihoo.haosou.subscribe.vertical.book\n" +
            " so.ofo.labofo\n" +
            " com.ct.client\n" +
            " com.mobike.mobikeapp\n" +
            " com.cmcc.cmvideo\n" +
            " com.baidu.haokan\n" +
            " com.esbook.reader\n" +
            " com.chaoxing.mobile\n" +
            " com.pplive.androidphone\n" +
            " cc.quanben.novel\n" +
            " com.lianzainovel\n" +
            " com.videogo\n" +
            " com.bet007.mobile.score\n" +
            " me.ele.napos\n" +
            " com.mianfeizs.book\n" +
            " com.tencent.ttpic\n" +
            " com.baidu.browser.apps\n" +
            " com.sunrise.scmbhc\n" +
            " com.sitech.ac\n" +
            " com.cainiao.wireless\n" +
            " com.meizu.flyme.mall\n" +
            " com.opera.browser\n" +
            " com.tencent.zebra\n" +
            " ctrip.android.view\n" +
            " cn.youth.news\n" +
            " com.alimama.moon\n" +
            " fm.qingting.qtradio\n" +
            " com.mygolbs.mybus\n" +
            " com.android.dazhihui\n" +
            " com.alibaba.wireless\n" +
            " com.xwtec.sd.mobileclient\n" +
            " com.sohu.infonews\n" +
            " com.xingin.xhs\n" +
            " com.dongqiudi.news\n" +
            " com.meizu.flyme.flymebbs\n" +
            " com.jingyao.easybike\n" +
            " com.jiongji.andriod.card\n" +
            " com.sogou.novel\n" +
            " com.ifeng.news2\n" +
            " com.zenmen.palmchat\n" +
            " com.wbxm.icartoon\n" +
            " com.mobikeeper.sjgj\n" +
            " com.nd.android.pandareader\n" +
            " com.tencent.weread\n" +
            " com.m4399.gamecenter\n" +
            " com.quanben.novel\n" +
            " com.mianfeia.book\n" +
            " com.dwd.rider\n" +
            " com.gf.client\n" +
            " com.remennovel\n" +
            " com.meitu.meipaimv\n" +
            " org.mozilla.firefox\n" +
            " com.taobao.etao\n" +
            " com.dianping.v1\n" +
            " com.storm.smart\n" +
            " com.xiaomi.hm.health\n" +
            " com.duoduo.child.story\n" +
            " com.freereader.kankan\n" +
            " com.ophone.reader.ui\n" +
            " com.lphtsccft\n" +
            " com.changba\n" +
            " com.hpbr.bosszhipin\n" +
            " com.xunmeng.merchant\n" +
            " com.baidu.video\n" +
            " com.hyww.wisdomtree\n" +
            " com.jd.jrapp\n" +
            " com.sdu.didi.beatles\n" +
            " com.wifi.reader\n" +
            " com.douban.frodo\n" +
            " com.dewmobile.kuaiya\n" +
            " com.sohu.sohuvideo\n" +
            "com.alibaba.mobileim\n";


    @Test
    public void addition_isCorrect() {
        String[] split = mWhiteList.split("\n");
        ArrayList<String> list = new ArrayList();
        for (String s : split) {
            System.out.printf("sWhiteList.add(\""+s.replace(" ","")+"\");");
        }


    }
}
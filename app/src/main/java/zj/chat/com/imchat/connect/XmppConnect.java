package zj.chat.com.imchat.connect;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.GroupChatInvitation;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider;
import org.jivesoftware.smackx.packet.ChatStateExtension;
import org.jivesoftware.smackx.packet.LastActivity;
import org.jivesoftware.smackx.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.provider.DataFormProvider;
import org.jivesoftware.smackx.provider.DelayInformationProvider;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.provider.MUCAdminProvider;
import org.jivesoftware.smackx.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.provider.MUCUserProvider;
import org.jivesoftware.smackx.provider.MessageEventProvider;
import org.jivesoftware.smackx.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.provider.RosterExchangeProvider;
import org.jivesoftware.smackx.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.provider.VCardProvider;
import org.jivesoftware.smackx.provider.XHTMLExtensionProvider;
import org.jivesoftware.smackx.search.UserSearch;

/**
 * 作者：zj
 * 邮箱：550687086@qq.com
 * XMPP连接类
 */
public class XmppConnect {
    private static XMPPConnection mXMPPConnection = null;//xmpp连接类
    private static String LOG = "XmppConnect";

    public interface OnXmppConnectListener {
        void onConnectionSuccess();

        void onConnectionError(Exception e);
    }

    public static XMPPConnection getXmppConnectInstance() {
        if (mXMPPConnection == null) {
            synchronized (XmppConnect.class) {
                if (mXMPPConnection == null) {
                    openXmppConnect(null);
                }
            }
        }
        return mXMPPConnection;
    }

    /**
     * 连接XMPP
     *
     * @return
     */
    private static void openXmppConnect(OnXmppConnectListener mOnXmppConnectListener) {

        ConnectionConfiguration connConfig = new ConnectionConfiguration(XmppUtils.serverIp, XmppUtils.port);
        connConfig.setSASLAuthenticationEnabled(false);
        // 允许自动连接
        // connConfig.setReconnectionAllowed(true);
        // connConfig.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        mXMPPConnection = new XMPPConnection(connConfig);
        ProviderManager pm = ProviderManager.getInstance();
        configureConnection(pm);
        try {
            mXMPPConnection.connect();
            if (mOnXmppConnectListener != null) {
                mOnXmppConnectListener.onConnectionSuccess();
            }
        } catch (Exception e) {
            if (mOnXmppConnectListener != null) {
                mOnXmppConnectListener.onConnectionError(e);
            }

        }


    }

    // 是否连接成功
    public static boolean isConnected() {
        if (mXMPPConnection != null && mXMPPConnection.isConnected()) {
            return true;
        }
        return false;
    }

    // 是否登录成功
    public static boolean isAuthenticated() {
        if (isConnected() && mXMPPConnection.isAuthenticated()) {
            return true;
        }
        return false;
    }

    /**
     * 关闭连接
     */
    public static void closeXmppConnect() {
        if (mXMPPConnection != null) {
            mXMPPConnection.disconnect();
            mXMPPConnection = null;

        }
    }

    /**
     * XMPP配置
     *
     * @param pm : 提供管理者
     */
    private static void configureConnection(ProviderManager pm) {
        // Private Data Storage
        pm.addIQProvider("query", "jabber:iq:private",
                new PrivateDataManager.PrivateDataIQProvider());

        // Time
        try {
            pm.addIQProvider("query", "jabber:iq:time",
                    Class.forName("org.jivesoftware.smackx.packet.Time"));
        } catch (ClassNotFoundException e) {
        }

        // XHTML
        pm.addExtensionProvider("html", "http://jabber.org/protocol/xhtml-im",
                new XHTMLExtensionProvider());

        // Roster Exchange
        pm.addExtensionProvider("x", "jabber:x:roster",
                new RosterExchangeProvider());
        // Message Events
        pm.addExtensionProvider("x", "jabber:x:event",
                new MessageEventProvider());
        // Chat State
        pm.addExtensionProvider("active",
                "http://jabber.org/protocol/chatstates",
                new ChatStateExtension.Provider());
        pm.addExtensionProvider("composing",
                "http://jabber.org/protocol/chatstates",
                new ChatStateExtension.Provider());
        pm.addExtensionProvider("paused",
                "http://jabber.org/protocol/chatstates",
                new ChatStateExtension.Provider());
        pm.addExtensionProvider("inactive",
                "http://jabber.org/protocol/chatstates",
                new ChatStateExtension.Provider());
        pm.addExtensionProvider("gone",
                "http://jabber.org/protocol/chatstates",
                new ChatStateExtension.Provider());

        // FileTransfer
        pm.addIQProvider("si", "http://jabber.org/protocol/si",
                new StreamInitiationProvider());
        pm.addIQProvider("query", "http://jabber.org/protocol/bytestreams", new BytestreamsProvider());
        // Group Chat Invitations
        pm.addExtensionProvider("x", "jabber:x:conference",
                new GroupChatInvitation.Provider());
        // Service Discovery # Items
        pm.addIQProvider("query", "http://jabber.org/protocol/disco#items",
                new DiscoverItemsProvider());
        // Service Discovery # Info
        pm.addIQProvider("query", "http://jabber.org/protocol/disco#info",
                new DiscoverInfoProvider());
        // Data Forms
        pm.addExtensionProvider("x", "jabber:x:data", new DataFormProvider());
        // MUC User
        pm.addExtensionProvider("x", "http://jabber.org/protocol/muc#user",
                new MUCUserProvider());
        // MUC Admin
        pm.addIQProvider("query", "http://jabber.org/protocol/muc#admin",
                new MUCAdminProvider());
        // MUC Owner
        pm.addIQProvider("query", "http://jabber.org/protocol/muc#owner",
                new MUCOwnerProvider());
        // Delayed Delivery
        pm.addExtensionProvider("x", "jabber:x:delay",
                new DelayInformationProvider());
        // Version
        try {
            pm.addIQProvider("query", "jabber:iq:version",
                    Class.forName("org.jivesoftware.smackx.packet.Version"));
        } catch (ClassNotFoundException e) {
        }
        // VCard
        pm.addIQProvider("vCard", "vcard-temp", new VCardProvider());
        // Offline Message Requests
        pm.addIQProvider("offline", "http://jabber.org/protocol/offline",
                new OfflineMessageRequest.Provider());
        // Offline Message Indicator
        pm.addExtensionProvider("offline",
                "http://jabber.org/protocol/offline",
                new OfflineMessageInfo.Provider());
        // Last Activity
        pm.addIQProvider("query", "jabber:iq:last", new LastActivity.Provider());
        // User Search
        pm.addIQProvider("query", "jabber:iq:search", new UserSearch.Provider());
        // SharedGroupsInfo
        pm.addIQProvider("sharedgroup",
                "http://www.jivesoftware.org/protocol/sharedgroup",
                new SharedGroupsInfo.Provider());
        // JEP-33: Extended Stanza Addressing
        pm.addExtensionProvider("addresses",
                "http://jabber.org/protocol/address",
                new MultipleAddressesProvider());

    }
}

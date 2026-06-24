/*
 * Source recovered from CFR output plus javap bytecode for refreshFriendStatuses.
 */
package com.rs2.model.player;

import com.rs2.model.World;
import com.rs2.model.player.Player;
import com.rs2.util.ChatTextCodec;
import com.rs2.util.TextUtil;

public final class SocialManager {
    private final Player player;
    private int privateMessageCounter = 1;

    public SocialManager(Player player) {
        this.player = player;
    }

    public final void initializePrivateMessaging() {
        Player player = this.player;
        player.packetSender.sendPrivateMessagingStatus(2);
        this.refreshFriendStatuses(false);
    }

    public final void refreshFriendStatuses(boolean forceOffline) {
        for (long friendHash : this.player.getFriendsList()) {
            if (friendHash == 0L) {
                continue;
            }
            this.player.packetSender.sendFriendStatus(friendHash, this.getFriendOnlineStatus(friendHash));
        }

        long playerNameHash = this.player.getNameHash();
        for (Player otherPlayer : World.getPlayers()) {
            if (otherPlayer == null || !containsNameHash(otherPlayer.getFriendsList(), playerNameHash)) {
                continue;
            }
            int status = this.isVisibleTo(otherPlayer, forceOffline) ? 1 : 0;
            otherPlayer.packetSender.sendFriendStatus(playerNameHash, status);
        }
    }

    private int getFriendOnlineStatus(long friendHash) {
        long playerNameHash = this.player.getNameHash();
        for (Player friend : World.getPlayers()) {
            if (friend == null || friend.getNameHash() == playerNameHash || friend.getNameHash() != friendHash) {
                continue;
            }
            return isVisibleToPlayer(friend, playerNameHash) ? 1 : 0;
        }
        return 0;
    }

    private boolean isVisibleTo(Player viewer, boolean forceOffline) {
        if (forceOffline || this.player.getPrivateChatMode() == 2) {
            return false;
        }
        if (this.player.getPrivateChatMode() == 1) {
            return containsNameHash(this.player.getFriendsList(), viewer.getNameHash());
        }
        return true;
    }

    private static boolean isVisibleToPlayer(Player subject, long viewerNameHash) {
        if (subject.getPrivateChatMode() == 0) {
            return true;
        }
        if (subject.getPrivateChatMode() == 1) {
            subject.getSocialManager();
            return containsNameHash(subject.getFriendsList(), viewerNameHash);
        }
        return false;
    }

    public final void addFriend(long l) {
        if (SocialManager.countEntries(this.player.getFriendsList()) >= 200) {
            Player player = this.player;
            player.packetSender.sendGameMessage("Your friends list is full.");
            return;
        }
        if (SocialManager.containsNameHash(this.player.getFriendsList(), l)) {
            Player player = this.player;
            player.packetSender.sendGameMessage(TextUtil.decodeNameHash(l) + " is already on your friends list.");
            return;
        }
        int n = SocialManager.findFreeSlot(this.player.getFriendsList());
        this.player.getFriendsList()[n] = l;
        this.refreshFriendStatuses(false);
    }

    public final void addIgnore(long l) {
        if (SocialManager.countEntries(this.player.getIgnoreList()) >= 100) {
            Player player = this.player;
            player.packetSender.sendGameMessage("Your ignores list is full.");
            return;
        }
        if (SocialManager.containsNameHash(this.player.getIgnoreList(), l)) {
            Player player = this.player;
            player.packetSender.sendGameMessage(TextUtil.decodeNameHash(l) + " is already on your ignores list.");
            return;
        }
        int n = SocialManager.findFreeSlot(this.player.getIgnoreList());
        this.player.getIgnoreList()[n] = l;
    }

    public final void sendPrivateMessage(Player player, long l, byte[] byArray, int n) {
        Player[] playerArray = World.getPlayers();
        int n2 = playerArray.length;
        int n3 = 0;
        while (n3 < n2) {
            Player player2 = playerArray[n3];
            if (player2 != null && player2.getNameHash() == l) {
                boolean bl;
                block5: {
                    long l2 = this.player.getNameHash();
                    SocialManager socialManager = player2.getSocialManager();
                    long[] lArray = socialManager.player.getIgnoreList();
                    int n4 = lArray.length;
                    int n5 = 0;
                    while (n5 < n4) {
                        long l3 = lArray[n5];
                        if (l3 == l2) {
                            bl = true;
                            break block5;
                        }
                        ++n5;
                    }
                    bl = false;
                }
                if (!bl) {
                    int n6 = player.getPlayerRights();
                    int n7 = player.gameMode;
                    player2.packetSender.sendPrivateMessage(player.getNameHash(), n6, 0, n7, byArray, n);
                    ChatTextCodec.decode(byArray, n);
                    return;
                }
            }
            ++n3;
        }
    }

    public final void removeFromList(long[] lArray, long l) {
        int n = 0;
        while (n < lArray.length) {
            if (lArray[n] == l) {
                lArray[n] = 0L;
                break;
            }
            ++n;
        }
        this.refreshFriendStatuses(false);
    }

    private static boolean containsNameHash(long[] lArray, long l) {
        int n = 0;
        while (n < lArray.length) {
            if (lArray[n] == l) {
                return true;
            }
            ++n;
        }
        return false;
    }

    private static int countEntries(long[] lArray) {
        int n = 0;
        long[] lArray2 = lArray;
        int n2 = lArray.length;
        int n3 = 0;
        while (n3 < n2) {
            long l = lArray2[n3];
            if (l > 0L) {
                ++n;
            }
            ++n3;
        }
        return n;
    }

    private static int findFreeSlot(long[] lArray) {
        int n = 0;
        while (n < lArray.length) {
            if (lArray[n] == 0L) {
                return n;
            }
            ++n;
        }
        return -1;
    }

    public final int nextPrivateMessageId() {
        return this.privateMessageCounter++;
    }
}

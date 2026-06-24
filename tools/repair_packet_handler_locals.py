#!/usr/bin/env python3
"""Repair small packet handlers where CFR reused the packet local."""

from __future__ import annotations

import sys
from pathlib import Path


REPAIRS = {
    "CommandPacketHandler.java": (
        """    @Override
    public final void handle(Player player, IncomingPacket object) {
        switch (((IncomingPacket)object).getOpcode()) {
            case 103: {
                try {
                    object = ((IncomingPacket)object).getReader().readString();
                    String[] stringArray = ((String)object).split(" ");
                    String string = stringArray[0].toLowerCase();
                    player.handleCommand(string, Arrays.copyOfRange(stringArray, 1, stringArray.length), ((String)object).substring(((String)object).indexOf(" ") + 1));
                    return;
                }
                catch (Exception exception) {}
            }
        }
    }
""",
        """    @Override
    public final void handle(Player player, IncomingPacket incomingPacket) {
        switch (incomingPacket.getOpcode()) {
            case 103: {
                try {
                    String rawCommand = incomingPacket.getReader().readString();
                    String[] stringArray = rawCommand.split(" ");
                    String command = stringArray[0].toLowerCase();
                    player.handleCommand(command, Arrays.copyOfRange(stringArray, 1, stringArray.length), rawCommand.substring(rawCommand.indexOf(" ") + 1));
                    return;
                }
                catch (Exception exception) {}
            }
        }
    }
""",
    ),
    "IdlePacketHandler.java": (
        """            object = player;
            ((Player)object).packetSender.sendLogout();
""",
        """            player.packetSender.sendLogout();
""",
    ),
    "SkillMenuPacketHandler.java": (
        """        object = InterfaceDefinition.forId(n);
        int n2 = ((InterfaceDefinition)object).getParentInterfaceId();
""",
        """        InterfaceDefinition interfaceDefinition = InterfaceDefinition.forId(n);
        int n2 = interfaceDefinition.getParentInterfaceId();
""",
    ),
    "ReportAbusePacketHandler.java": (
        """    @Override
    public final void handle(Player player, IncomingPacket incomingPacket) {
        Player player2;
        Player player3;
        Object object;
        int n;
        boolean bl;
        byte by;
        long l;
        block7: {
            l = incomingPacket.getReader().readLong();
            by = (byte)incomingPacket.getReader().readUnsignedByte(false);
            bl = (byte)incomingPacket.getReader().readUnsignedByte(false) == 1;
            long l2 = l;
            Player[] playerArray = World.getPlayers();
            n = playerArray.length;
            int n2 = 0;
            while (n2 < n) {
                object = playerArray[n2];
                if (object != null && ((Player)object).getNameHash() == l2) {
                    player3 = object;
                    break block7;
                }
                ++n2;
            }
            player3 = player2 = null;
        }
        if (player3 == null) {
            Player player4 = player;
            player4.packetSender.sendGameMessage("You cannot report offline players.");
            return;
        }
        if (player.getNameHash() == l) {
            Player player5 = player;
            player5.packetSender.sendGameMessage("You cannot report yourself!");
            return;
        }
        Player player6 = player;
        if (System.currentTimeMillis() - player6.dR < 60000L) {
            Player player7 = player;
            player7.packetSender.sendGameMessage("You can only report a player once every 60 seconds.");
            return;
        }
        Player player8 = player;
        player8.packetSender.sendGameMessage("Thank-you, your abuse report has been received.");
        if (bl && player.getPlayerRights() > 0 && player2.getPlayerRights() == 0) {
            if (player2.isMuted()) {
                return;
            }
            n = 48;
            long l3 = System.currentTimeMillis();
            object = new DateTime(l3);
            object = ((DateTime)object).plusHours(48);
            player2.setMuteExpires(((BaseDateTime)object).getMillis());
        }
        long l4 = System.currentTimeMillis();
        Player player9 = player;
        player.dR = l4;
        GameplayHelper.appendLogLine(String.valueOf(System.currentTimeMillis()) + "\\u00a7" + player.getUsername() + "\\u00a7" + player.getPosition().getX() + "\\u00a7" + player.getPosition().getY() + "\\u00a7" + player.getPosition().getPlane() + "\\u00a7" + player2.getUsername() + "\\u00a7" + player2.getPosition().getX() + "\\u00a7" + player2.getPosition().getY() + "\\u00a7" + player2.getPosition().getPlane() + "\\u00a7" + by + "\\u00a7" + (bl ? 1 : 0), "reports");
    }
""",
        """    @Override
    public final void handle(Player player, IncomingPacket incomingPacket) {
        long reportedNameHash = incomingPacket.getReader().readLong();
        int reportType = incomingPacket.getReader().readUnsignedByte(false);
        boolean mutePlayer = incomingPacket.getReader().readUnsignedByte(false) == 1;
        Player reportedPlayer = null;
        Player[] playerArray = World.getPlayers();
        int count = playerArray.length;
        int index = 0;
        while (index < count) {
            Player candidate = playerArray[index];
            if (candidate != null && candidate.getNameHash() == reportedNameHash) {
                reportedPlayer = candidate;
                break;
            }
            ++index;
        }
        if (reportedPlayer == null) {
            player.packetSender.sendGameMessage("You cannot report offline players.");
            return;
        }
        if (player.getNameHash() == reportedNameHash) {
            player.packetSender.sendGameMessage("You cannot report yourself!");
            return;
        }
        if (System.currentTimeMillis() - player.dR < 60000L) {
            player.packetSender.sendGameMessage("You can only report a player once every 60 seconds.");
            return;
        }
        player.packetSender.sendGameMessage("Thank-you, your abuse report has been received.");
        if (mutePlayer && player.getPlayerRights() > 0 && reportedPlayer.getPlayerRights() == 0) {
            if (reportedPlayer.isMuted()) {
                return;
            }
            DateTime muteExpires = new DateTime(System.currentTimeMillis()).plusHours(48);
            reportedPlayer.setMuteExpires(muteExpires.getMillis());
        }
        player.dR = System.currentTimeMillis();
        GameplayHelper.appendLogLine(String.valueOf(System.currentTimeMillis()) + "\\u00a7" + player.getUsername() + "\\u00a7" + player.getPosition().getX() + "\\u00a7" + player.getPosition().getY() + "\\u00a7" + player.getPosition().getPlane() + "\\u00a7" + reportedPlayer.getUsername() + "\\u00a7" + reportedPlayer.getPosition().getX() + "\\u00a7" + reportedPlayer.getPosition().getY() + "\\u00a7" + reportedPlayer.getPosition().getPlane() + "\\u00a7" + reportType + "\\u00a7" + (mutePlayer ? 1 : 0), "reports");
    }
""",
    ),
    "PublicChatPacketHandler.java": (
        """    @Override
    public final void handle(Player player, IncomingPacket object) {
        int n = ((IncomingPacket)object).getReader().readByte(false, ByteTransform.SUBTRACT);
        int n2 = ((IncomingPacket)object).getReader().readByte(false, ByteTransform.SUBTRACT);
        int n3 = ((IncomingPacket)object).getLength() - 2;
        byte[] byArray = ((IncomingPacket)object).getReader().readBytesReverse(n3, ByteTransform.ADD);
        object = byArray;
        String string = ChatTextCodec.decode(byArray, n3);
        if (player.isMuted()) {
            object = player;
            ((Player)object).packetSender.sendGameMessage("You are muted and cannot talk. Mute expires in: " + (GameplayHelper.getHoursBetween(System.currentTimeMillis(), player.getMuteExpires()) + 1) + " hours.");
            return;
        }
        if (player.getQuestState(0) != 1) {
            return;
        }
        player.setPublicChatEffects(n);
        player.setPublicChatColor(n2);
        player.setPublicChatPayload((byte[])object);
        player.flagAppearanceUpdate(true);
        if (!player.isBot) {
            player.ey = object = string.replaceAll("\\\\s+$", "");
            if (player.getMovementTarget() != null) {
                if (!player.getMovementTarget().isPlayer()) {
                    return;
                }
                Player player2 = (Player)player.getMovementTarget();
                if (!player2.isBot) {
                    return;
                }
                player2.botPvpChatMessage = object;
                player2.botPvpChatSource = player;
                return;
            }
            if (player.botPvpTeamRequesters.size() > 0) {
                for (Player player3 : player.botPvpTeamRequesters) {
                    ((Player)var4_7.next()).botPvpChatMessage = object;
                    player3.botPvpChatSource = player;
                }
            }
        }
    }
""",
        """    @Override
    public final void handle(Player player, IncomingPacket incomingPacket) {
        int effects = incomingPacket.getReader().readByte(false, ByteTransform.SUBTRACT);
        int color = incomingPacket.getReader().readByte(false, ByteTransform.SUBTRACT);
        int payloadLength = incomingPacket.getLength() - 2;
        byte[] payload = incomingPacket.getReader().readBytesReverse(payloadLength, ByteTransform.ADD);
        String message = ChatTextCodec.decode(payload, payloadLength);
        if (player.isMuted()) {
            player.packetSender.sendGameMessage("You are muted and cannot talk. Mute expires in: " + (GameplayHelper.getHoursBetween(System.currentTimeMillis(), player.getMuteExpires()) + 1) + " hours.");
            return;
        }
        if (player.getQuestState(0) != 1) {
            return;
        }
        player.setPublicChatEffects(effects);
        player.setPublicChatColor(color);
        player.setPublicChatPayload(payload);
        player.flagAppearanceUpdate(true);
        if (!player.isBot) {
            String trimmedMessage = message.replaceAll("\\\\s+$", "");
            player.ey = trimmedMessage;
            if (player.getMovementTarget() != null) {
                if (!player.getMovementTarget().isPlayer()) {
                    return;
                }
                Player player2 = (Player)player.getMovementTarget();
                if (!player2.isBot) {
                    return;
                }
                player2.botPvpChatMessage = trimmedMessage;
                player2.botPvpChatSource = player;
                return;
            }
            if (player.botPvpTeamRequesters.size() > 0) {
                for (Object requesterObject : player.botPvpTeamRequesters) {
                    Player player3 = (Player)requesterObject;
                    player3.botPvpChatMessage = trimmedMessage;
                    player3.botPvpChatSource = player;
                }
            }
        }
    }
""",
    ),
}


def replace_exact(text: str, old: str, new: str, path: Path) -> tuple[str, int]:
    count = text.count(old)
    if count == 0:
        preview = old.strip().splitlines()[0][:120] if old.strip() else "<empty>"
        raise SystemExit(f"{path}: expected snippet not found: {preview!r}")
    return text.replace(old, new), count


def main() -> int:
    if len(sys.argv) != 2:
        print("usage: repair_packet_handler_locals.py <source_root>", file=sys.stderr)
        return 2
    source_root = Path(sys.argv[1]).resolve()
    handler_root = source_root / "com" / "rs2" / "net" / "packet" / "handler"
    repairs = 0
    for filename, (old, new) in REPAIRS.items():
        path = handler_root / filename
        text = path.read_text(encoding="utf-8")
        text, count = replace_exact(text, old, new, path)
        path.write_text(text, encoding="utf-8")
        repairs += count
    print(f"repaired packet handler locals: {repairs}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())

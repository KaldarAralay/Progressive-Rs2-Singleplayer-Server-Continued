/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.bot.trade;

import com.rs2.bot.BotTradeAdvertManager;
import com.rs2.model.GameplayHelper;
import com.rs2.model.item.ItemDefinition;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.player.TradeState;
import com.rs2.model.task.TickTask;
import com.rs2.net.packet.handler.PlayerInteractionPacketHandler;
import com.rs2.util.GameUtil;

public final class BotTradeOfferTickTask
extends TickTask {
    private final /* synthetic */ Player player;

    public BotTradeOfferTickTask(int n, Player player) {
        super(3);
        this.player = player;
    }

    @Override
    public final void execute() {
        if (this.player.isDead() || !this.player.isRegistered() || this.player.botMode != 2) {
            this.stop();
            return;
        }
        if (!this.player.botTaskState.equals("do task")) {
            return;
        }
        if (GameUtil.randomInt(3) == 0) {
            this.player.queuePublicChatMessage(this.player.botPublicChatMessage, this.player.botPublicChatColor, this.player.botPublicChatEffect);
        }
        if (this.player.getOpenInterfaceId() == 0) {
            if (GameplayHelper.shouldReturnToBankForBotTask(this.player)) {
                this.player.botTaskState = "wait for new task";
                GameplayHelper.startNextBotTask(this.player);
                return;
            }
            if (this.player.pendingTradeTarget != null) {
                PlayerInteractionPacketHandler.dispatchDeferredTradeRequest(this.player, this.player.pendingTradeTarget);
                return;
            }
        } else if (this.player.getOpenInterfaceId() == 3323 || this.player.getOpenInterfaceId() == 3443) {
            Player player = (Player)this.player.getTradePartner();
            if (player == null) {
                GameplayHelper.declineTrade(this.player);
                return;
            }
            if (this.player.getTradeOfferContainer().getUsedSlots() == 0) {
                this.player.tradeAdvertLastOfferAmount = -1;
            }
            if (this.player.tradeAdvertMode == 0) {
                if (this.player.getTradeOfferContainer().getUsedSlots() == 0 && this.player.botAdvertItemId != 0 && !this.player.tradeAdvertInitialOfferPlaced) {
                    GameplayHelper.addTradeOfferItem(this.player, this.player.getInventoryManager().getContainer().indexOfItem(this.player.botAdvertItemId), this.player.botAdvertItemId, this.player.tradeAdvertQuantityRemaining);
                    this.player.tradeAdvertInitialOfferPlaced = true;
                }
                if (player.getTradeOfferContainer().getUsedSlots() > 0) {
                    ItemStack itemStack = player.getTradeOfferContainer().getItemAt(0);
                    int n = itemStack.getId();
                    int n2 = itemStack.getAmount();
                    boolean bl = false;
                    if (this.player.botAdvertItemId == 0 && this.player.tradeAdvertUnitPrice == 1) {
                        bl = true;
                    }
                    if (n != 995 && !bl) {
                        GameplayHelper.declineTrade(this.player);
                        return;
                    }
                    boolean bl2 = this.player.tradeAdvertQuantityRemaining <= 10 && this.player.tradeAdvertQuantityRemaining > 1 || this.player.tradeAdvertVariableQuantity;
                    int n3 = 0;
                    int n4 = this.player.tradeAdvertUnitPrice * this.player.tradeAdvertQuantityRemaining;
                    GameplayHelper gameplayHelper = (GameplayHelper)BotTradeAdvertManager.tradeAdvertOfferPool.get(this.player.tradeAdvertOfferPoolIndex);
                    int n5 = gameplayHelper.getPreviousTradeAdvertQuantityOption(this.player.tradeAdvertQuantityOptionIndex);
                    if (n2 >= n4 && n4 > 0) {
                        n3 = this.player.tradeAdvertQuantityRemaining;
                    }
                    if (!bl2 && this.player.tradeAdvertLastOfferAmount == n5 && n5 != -1 && n2 >= n4 && n4 > 0 && this.player.botAdvertItemId != 0) {
                        if (this.player.getTradeOfferContainer().getUsedSlots() > 0) {
                            GameplayHelper.removeTradeOfferItem(this.player, 0, this.player.botAdvertItemId, this.player.getTradeOfferContainer().getItemAmount(this.player.botAdvertItemId));
                        }
                        if (!this.player.tradeAdvertScam) {
                            GameplayHelper.addTradeOfferItem(this.player, this.player.getInventoryManager().getContainer().indexOfItem(this.player.botAdvertItemId), this.player.botAdvertItemId, n3);
                        }
                        this.player.tradeAdvertLastOfferAmount = this.player.tradeAdvertQuantityRemaining;
                    }
                    if (bl2 && this.player.botAdvertItemId != 0) {
                        n3 = (int)Math.floor(n2 / this.player.tradeAdvertUnitPrice);
                        if (n3 != this.player.tradeAdvertLastOfferAmount) {
                            if (this.player.getTradeOfferContainer().getUsedSlots() > 0) {
                                GameplayHelper.removeTradeOfferItem(this.player, 0, this.player.botAdvertItemId, this.player.getTradeOfferContainer().getItemAmount(this.player.botAdvertItemId));
                            }
                            if (n3 <= 0 || this.player.tradeAdvertScam) {
                                GameplayHelper.declineTrade(this.player);
                                return;
                            }
                            GameplayHelper.addTradeOfferItem(this.player, this.player.getInventoryManager().getContainer().indexOfItem(this.player.botAdvertItemId), this.player.botAdvertItemId, n3);
                            this.player.tradeAdvertLastOfferAmount = n3;
                        }
                        n4 = this.player.tradeAdvertUnitPrice * n3;
                    } else if (n2 < n4 && n4 > 0 && n5 != -1 && this.player.botAdvertItemId != 0) {
                        n3 = (int)Math.floor(n2 / this.player.tradeAdvertUnitPrice);
                        if (n3 >= n5) {
                            n3 = n5;
                        }
                        if (this.player.tradeAdvertLastOfferAmount != n5 && n3 >= n5) {
                            if (this.player.getTradeOfferContainer().getUsedSlots() > 0) {
                                GameplayHelper.removeTradeOfferItem(this.player, 0, this.player.botAdvertItemId, this.player.getTradeOfferContainer().getItemAmount(this.player.botAdvertItemId));
                            }
                            if (!this.player.tradeAdvertScam) {
                                GameplayHelper.addTradeOfferItem(this.player, this.player.getInventoryManager().getContainer().indexOfItem(this.player.botAdvertItemId), this.player.botAdvertItemId, n3);
                            }
                            this.player.tradeAdvertLastOfferAmount = n5;
                        }
                        n4 = this.player.tradeAdvertUnitPrice * n5;
                    }
                    this.player.tradeAdvertAcceptedQuantity = n3;
                    if (n2 >= n4 && n4 > 0 || bl) {
                        if (this.player.tradeAdvertScam) {
                            if (this.player.getTradeOfferContainer().getUsedSlots() > 0) {
                                GameplayHelper.removeTradeOfferItem(this.player, 0, this.player.botAdvertItemId, this.player.getTradeOfferContainer().getItemAmount(this.player.botAdvertItemId));
                            }
                            if (this.player.botAdvertItemId == 1320) {
                                GameplayHelper.addTradeOfferItem(this.player, this.player.getInventoryManager().getContainer().indexOfItem(1310), 1310, n3);
                            }
                        }
                        BotTradeAdvertManager.advanceTradeAdvertTradeFlow(this.player);
                        return;
                    }
                    if (player.getTradeState() == TradeState.ACCEPTED) {
                        GameplayHelper.declineTrade(this.player);
                        return;
                    }
                }
            } else if (!this.player.tradeAdvertVariableQuantity) {
                if (this.player.getTradeOfferContainer().getUsedSlots() == 0 && !this.player.tradeAdvertInitialOfferPlaced) {
                    GameplayHelper.addTradeOfferItem(this.player, this.player.getInventoryManager().getContainer().indexOfItem(995), 995, this.player.tradeAdvertUnitPrice * this.player.tradeAdvertQuantityRemaining);
                    this.player.tradeAdvertInitialOfferPlaced = true;
                }
                if (player.getTradeOfferContainer().getUsedSlots() > 0) {
                    int n = (this.player.tradeAdvertQuantityRemaining > 10 || this.player.tradeAdvertQuantityRemaining <= 1) && !this.player.tradeAdvertVariableQuantity ? 0 : 1;
                    int n6 = -1;
                    int n7 = 0;
                    ItemDefinition itemDefinition = ItemDefinition.forId(this.player.botAdvertItemId);
                    if (itemDefinition.isNote()) {
                        n6 = itemDefinition.getUnnotedId();
                        n7 = player.getTradeOfferContainer().getItemAmount(n6);
                    }
                    int n8 = player.getTradeOfferContainer().getItemAmount(this.player.botAdvertItemId);
                    int n9 = this.player.tradeAdvertQuantityRemaining;
                    int n10 = this.player.tradeAdvertUnitPrice * n8;
                    GameplayHelper gameplayHelper = (GameplayHelper)BotTradeAdvertManager.tradeAdvertOfferPool.get(this.player.tradeAdvertOfferPoolIndex);
                    int n11 = gameplayHelper.getPreviousTradeAdvertQuantityOption(this.player.tradeAdvertQuantityOptionIndex);
                    if (n == 0 && this.player.tradeAdvertLastOfferAmount == n11 && n11 != -1 && n8 >= n9 && n10 > 0) {
                        if (this.player.getTradeOfferContainer().getUsedSlots() > 0) {
                            GameplayHelper.removeTradeOfferItem(this.player, 0, 995, this.player.getTradeOfferContainer().getItemAt(0).getAmount());
                        }
                        if (!this.player.tradeAdvertScam) {
                            GameplayHelper.addTradeOfferItem(this.player, this.player.getInventoryManager().getContainer().indexOfItem(995), 995, this.player.tradeAdvertUnitPrice * this.player.tradeAdvertQuantityRemaining);
                        }
                        this.player.tradeAdvertLastOfferAmount = this.player.tradeAdvertQuantityRemaining;
                    }
                    if (n8 <= 0 && n == 0) {
                        GameplayHelper.declineTrade(this.player);
                        return;
                    }
                    if (n8 <= 0 && n != 0 && n7 <= 0) {
                        GameplayHelper.declineTrade(this.player);
                        return;
                    }
                    if (n != 0) {
                        n = 0;
                        if (player.getTradeOfferContainer().getUsedSlots() > 0) {
                            n = 0 + player.getTradeOfferContainer().getItemAmount(this.player.botAdvertItemId);
                            if (n6 != -1) {
                                n += player.getTradeOfferContainer().getItemAmount(n6);
                            }
                        }
                        n9 = n8 = n;
                        n10 = this.player.tradeAdvertUnitPrice * n9;
                        if (n9 != this.player.tradeAdvertLastOfferAmount) {
                            if (this.player.getTradeOfferContainer().getUsedSlots() > 0) {
                                GameplayHelper.removeTradeOfferItem(this.player, 0, 995, this.player.getTradeOfferContainer().getItemAt(0).getAmount());
                            }
                            if (n9 <= 0) {
                                GameplayHelper.declineTrade(this.player);
                                return;
                            }
                            GameplayHelper.addTradeOfferItem(this.player, this.player.getInventoryManager().getContainer().indexOfItem(995), 995, n10);
                            this.player.tradeAdvertLastOfferAmount = n9;
                        }
                    } else if (n8 < n9 && n10 > 0 && n11 != -1 && n8 >= n11) {
                        n9 = n11;
                        n10 = this.player.tradeAdvertUnitPrice * n9;
                        if (this.player.tradeAdvertLastOfferAmount != n11) {
                            if (this.player.getTradeOfferContainer().getUsedSlots() > 0) {
                                GameplayHelper.removeTradeOfferItem(this.player, 0, 995, this.player.getTradeOfferContainer().getItemAt(0).getAmount());
                            }
                            if (!this.player.tradeAdvertScam) {
                                GameplayHelper.addTradeOfferItem(this.player, this.player.getInventoryManager().getContainer().indexOfItem(995), 995, n10);
                            }
                            this.player.tradeAdvertLastOfferAmount = n11;
                        }
                    }
                    this.player.tradeAdvertAcceptedQuantity = n9;
                    if (n8 >= n9 && n10 > 0) {
                        if (this.player.tradeAdvertScam && this.player.getTradeOfferContainer().getUsedSlots() > 0) {
                            GameplayHelper.removeTradeOfferItem(this.player, 0, 995, this.player.getTradeOfferContainer().getItemAt(0).getAmount());
                        }
                        BotTradeAdvertManager.advanceTradeAdvertTradeFlow(this.player);
                        return;
                    }
                    if (player.getTradeState() == TradeState.ACCEPTED) {
                        GameplayHelper.declineTrade(this.player);
                        return;
                    }
                }
            } else {
                long l;
                ItemDefinition itemDefinition = ItemDefinition.forId(this.player.botAdvertItemId);
                int n = -1;
                int n12 = 0;
                if (itemDefinition.isNote()) {
                    n = itemDefinition.getUnnotedId();
                }
                if (player.getTradeOfferContainer().getUsedSlots() > 0) {
                    n12 = 0 + player.getTradeOfferContainer().getItemAmount(this.player.botAdvertItemId);
                    if (n != -1) {
                        n12 += player.getTradeOfferContainer().getItemAmount(n);
                    }
                    if (n12 == 0) {
                        GameplayHelper.declineTrade(this.player);
                        return;
                    }
                }
                int n13 = (l = (long)(this.player.tradeAdvertUnitPrice * n12)) > Integer.MAX_VALUE ? this.player.getInventoryManager().getItemAmount(995) : (int)l;
                if (this.player.getTradeOfferContainer().getUsedSlots() == 0 && !this.player.tradeAdvertInitialOfferPlaced) {
                    GameplayHelper.addTradeOfferItem(this.player, this.player.getInventoryManager().getContainer().indexOfItem(995), 995, n13);
                    this.player.tradeAdvertInitialOfferPlaced = true;
                    return;
                }
                if (n13 != this.player.tradeAdvertLastOfferAmount) {
                    if (this.player.getTradeOfferContainer().getUsedSlots() > 0 && this.player.getTradeOfferContainer().getItemAt(0).getAmount() != n13) {
                        GameplayHelper.removeTradeOfferItem(this.player, 0, 995, this.player.getTradeOfferContainer().getItemAt(0).getAmount());
                    }
                    if (!this.player.tradeAdvertScam) {
                        GameplayHelper.addTradeOfferItem(this.player, this.player.getInventoryManager().getContainer().indexOfItem(995), 995, n13);
                    }
                    this.player.tradeAdvertLastOfferAmount = n13;
                }
                this.player.tradeAdvertAcceptedQuantity = n12;
                if (n13 > 0 && n13 >= this.player.getTradeOfferContainer().getItemAt(0).getAmount()) {
                    if (this.player.tradeAdvertScam) {
                        GameplayHelper.removeTradeOfferItem(this.player, 0, 995, this.player.getTradeOfferContainer().getItemAt(0).getAmount());
                    }
                    BotTradeAdvertManager.advanceTradeAdvertTradeFlow(this.player);
                }
            }
        }
    }
}


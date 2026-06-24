# Source Recovery Overlays

Files in this tree are hand-recovered replacements for CFR output that still has invalid control flow after bytecode renaming.

`tools/remap-and-decompile.ps1` runs `tools/apply_source_recoveries.py` after CFR and copies these files into `deobfuscated_source/`, preserving the package-relative paths.

Current recovery:

- `com/rs2/Server.java`: replaces CFR's damaged `processGameCycle()` and `sleepUntilNextCycle()` methods, plus local bootstrap type-flow cleanup needed for full-class compilation.
- `com/rs2/model/player/SocialManager.java`: replaces CFR's damaged `refreshFriendStatuses(boolean)` method with source reconstructed from `javap` bytecode and the surrounding decompiled class.
- `com/rs2/model/travel/TravelManager.java`: replaces CFR's damaged `handleGnomeGliderButton(Player, int)` method with source reconstructed from `javap` bytecode and the surrounding decompiled class.
- `com/rs2/model/gameplay/magetrainingarena/EnchantmentChamberController.java`: replaces CFR's damaged `handleObjectAction(int)` method with source reconstructed from `javap` bytecode and the surrounding decompiled class.
- `com/rs2/model/combat/requirement/SpellRuneCostRequirement.java`: replaces CFR's damaged `buildRuneCosts(Player, ItemStack[])` and malformed `collectCombinationRuneCosts(Player, int, int)` methods with source reconstructed from `javap` bytecode and the surrounding decompiled class.
- `com/rs2/net/packet/handler/InterfaceInputPacketHandler.java`: replaces CFR's damaged `handle(Player, IncomingPacket)` method with source reconstructed from `javap` bytecode and the surrounding decompiled class.
- `com/rs2/model/skill/farming/BushPatchManager.java`: replaces CFR's damaged `refreshConfig()` method with source reconstructed from `javap` bytecode and the surrounding decompiled class.
- `com/rs2/model/combat/AttackStyleDefinition.java`: replaces CFR's damaged `climbOffsetLadder(Player, String)` method with source reconstructed from `javap` bytecode and the surrounding decompiled class.
- `com/rs2/model/objects/functions/DelayedObjectMoveEvent.java`, `ObjectToggleEvent.java`, and `WebSlashEvent.java`: source-access companions for `AttackStyleDefinition`, making remapped object-function events public so the recovered source compiles across the renamed package boundary.
- `com/rs2/model/bankpin/BankPinManager.java`: replaces CFR's damaged `handleButtonClick(int)` method with source reconstructed from `javap` bytecode and the surrounding decompiled class.
- `com/rs2/model/bankpin/BankPinProtectedAction.java`: recovers the CFR-failed one-value protected-action enum used by `BankPinManager`.
- `com/rs2/model/update/EntityUpdateOverrideDefinition.java`: recovers the CFR-failed one-value update override enum (`WHIP`) from `javap` bytecode.
- `com/rs2/model/skill/guide/SkillGuideCategory.java`, `SkillGuideEntry.java`, and `com/rs2/model/gameplay/SmokeDungeonDamageTask.java`: source-access companions for `BankPinManager`, making remapped package-private collaborators usable from the recovered bank-pin source.
- `com/rs2/model/player/EquipmentManager.java`: replaces CFR's damaged `refreshBarrowsSetEffects()` method with source reconstructed from `javap` bytecode and the surrounding decompiled class, plus local type-flow cleanups needed for compilation.
- `com/rs2/model/gameplay/barrows/BarrowsManager.java`: replaces CFR's damaged `awardChestRewards(Player)` method with source reconstructed from `javap` bytecode and the surrounding decompiled class, plus local `generateTunnelRoute(Player, boolean)` type-flow cleanup needed for compilation.
- `com/rs2/model/gameplay/barrows/BarrowsTunnelRoom.java`: source companion for `BarrowsManager` validation and regeneration.
- `com/rs2/model/dialogue/DialogueManager.java`: replaces CFR's damaged NPC dialogue render helpers and repairs local `continueDialogueWithNpcId(Player, int, int, int, int)` control-flow/type-flow artifacts using `javap` bytecode and the surrounding decompiled class.
- `com/rs2/model/skill/magic/MagicSpellAction.java`: replaces CFR's damaged `castSuperheatItem(int)` method and cleans local magic-dispatch type-flow artifacts needed for full-class compilation.
- `com/rs2/net/packet/PacketSender.java`: replaces CFR's damaged private double-door helper and fixes local packet-sender type-flow artifacts needed for full-class compilation.
- `com/rs2/bot/combat/BotCombatLoadoutManager.java`: replaces CFR's damaged `prepareMagicLoadout(Player)` method and fixes local bot-loadout type-flow artifacts needed for full-class compilation.
- `com/rs2/model/GameplayHelper.java`, `com/rs2/model/combat/CombatManager.java`, `com/rs2/model/player/Player.java`, `com/rs2/net/packet/handler/ButtonClickPacketHandler.java`, `com/rs2/net/packet/handler/ItemActionPacketHandler.java`, and `com/rs2/util/CharacterFileManager.java`: later full or partial overlays removed the remaining CFR structure markers from generated source.
- `com/rs2/model/npc/drop/NpcDropManager.java` and `NpcDropTable.java`: recover typed NPC drop-table loading, filtering, and drop-selection logic.
- `com/rs2/model/player/InventoryManager.java`: recovers typed inventory item/container flows and the no-space message helper.
- `com/rs2/cache/CacheArchive.java` and `CacheDefinitionIndex.java`: recover typed cache archive reads, cache definition indexes, clue challenge helpers, and random-event scheduling helpers.
- `com/rs2/net/packet/handler/MovementPacketHandler.java`: recovers movement-packet path decoding with typed `int[][]` path steps.
- `com/rs2/util/BackupRepairTimestampComparator.java` and `BackupRestoreTimestampComparator.java`: recover typed file timestamp comparison.
- `com/rs2/util/RSAKeyGen.java`: recovers typed RSA key-pair generation and constants-file writing.
- `com/rs2/util/TextUtil.java`: recovers typed display-name formatting and text utility flows after CFR reused String/Object/char-array locals.
- `com/rs2/net/packet/handler/CloseInterfacePacketHandler.java`: recovers close-interface packet handling after CFR reused the packet local as the player.
- `com/rs2/net/packet/handler/PlayerInteractionPacketHandler.java`: recovers the player-interaction dispatcher with typed player, packet, target, item, and spell locals.
- `com/rs2/model/combat/attack/BaseCombatAttack.java` and `WeaponCombatAttack.java`: recover typed combat attack execution, range validation, ranged ammunition, poison, and special-attack state after CFR reused task/player/hit locals across combat branches.
- `com/rs2/model/combat/effect/DisarmWeaponEffect.java`, `MovementLockEffect.java`, `PoisonEffect.java`, `StatDrainEffect.java`, `SummonNpcCombatEffect.java`, `TargetRandomTeleportEffect.java`, and `TeleblockEffect.java`: recover small combat-effect classes after constructor/local-slot reuse left invalid generated source.
- `com/rs2/net/packet/handler/NpcInteractionPacketHandler.java`: recovers the NPC interaction dispatcher with typed packet, NPC, item, spell, and option locals.
- `com/rs2/model/travel/canoe/CanoeTravelManager.java` and `CanoeBuildTask.java`: recover typed canoe tree, build, destination, and config flows after CFR collapsed enum and task locals to `Object`.
- `com/rs2/model/objects/functions/ObeliskTick.java`: recovers typed wilderness-obelisk activation and teleport destination handling after CFR reused obelisk and position-array locals.
- `com/rs2/model/player/BankManager.java`: recovers typed bank open, deposit, withdraw, tab refresh, bot-bank, and rearrange flows; `rearrangeBankItem(Player, int, int, int, int)` was reconstructed from `javap` bytecode to remove the last CFR failed-decompile stub.
- `com/rs2/model/combat/special/SpecialAttackDefinition.java`: recovers CFR's fake enum output as singleton-style special attack definitions with typed item lookup and special-hit effect handling.
- `com/rs2/model/gameplay/duel/DuelRule.java`: recovers CFR's fake duel-rule enum output as singleton-style duel rules with button lookup and value lookup helpers.
- `com/rs2/model/skill/thieving/PickpocketDefinition.java`: recovers CFR's fake pickpocket-definition enum output as singleton-style definitions with NPC id, level, loot, stun, and value lookup helpers.

Post-CFR repair passes:

- `tools/repair_bot_player.py`: repairs `BotPlayer` calls that still reference pre-remap `Player` members and methods.
- `tools/repair_packet_handler_locals.py`: repairs small packet handlers where CFR reused the `IncomingPacket` parameter as unrelated typed locals.
- `tools/repair_util_locals.py`: repairs small utility local-slot collisions and restores the missing weighted-chance comparator body.
- `tools/repair_small_local_collisions.py`: repairs small one-file local-slot collisions, missing comparator bodies, exposed remap/access leftovers, Karamja and bot route locals, World unregister/task locals, BotTaskDefinition access/type locals, PlayerUpdateTask update flags, Grand Exchange/login/plugin/item-combination utility locals, enum constructor fragments, fake-enum helper locals, `ObjectDefinition` projectile-collision locals, inventory requirement raw-list iteration, crafting task message locals, and targeted method-level type recovery for contained CFR local-slot failures.
- `tools/repair_access_modifiers.py`: promotes package-private top-level classes and ordinary constructors exposed by the remapped package layout.
- `tools/repair_constructor_super_order.py`: moves misplaced constructor `super(...)` calls back to the first statement.
- `tools/repair_quest_journal_locals.py`: repairs the repeated `Player`/`String[]` local-slot collision in quest journal rendering.
- `tools/repair_scheduler_task_locals.py`: inlines `TickTask` constructor locals before scheduler calls where CFR reused a typed `Player` parameter.
- `tools/repair_itemstack_array_locals.py`: casts `Object` locals that temporarily hold `ItemStack[]` arrays.
- `tools/repair_int_bridge_constructors.py`: adds int bridge constructors for CFR synthetic byte-marker constructor calls and casts the remaining byte marker call sites.
- `tools/repair_known_cfr_void_locals.py`: includes the earlier void-local repairs plus expanded `BotTaskPlanner` type-flow recovery.
- `tools/repair_special_attack_definition_constructors.py`: restores final `super(n2, stringArray)` calls in special attack definition subclasses after the constructor-order pass.
- `tools/repair_duel_rule_constructors.py`: restores final `super(n2, n3, n4)` calls in duel-rule subclasses after the constructor-order pass.

Current validation snapshot: regenerated source has zero CFR decompilation-failed method stubs and zero whole-class CFR analysis warnings remaining, 66 source recoveries applied, 287 small local-collision repairs, 24 special attack definition constructor repairs, 22 duel rule constructor repairs, and the full-source compile snapshot is down to 1066 javac errors after the latest repair pass. Remaining work is mostly shared type-flow repair in the larger player, gameplay, combat, packet, farming, and pathing classes.

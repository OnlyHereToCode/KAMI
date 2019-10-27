/*
 * Decompiled with CFR 0.147.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockContainer
 *  net.minecraft.block.BlockFalling
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.client.network.NetHandlerPlayClient
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketEntityAction
 *  net.minecraft.network.play.client.CPacketEntityAction$Action
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.Vec3i
 */
package me.zeroeightsix.kami.module.modules.combat;

import java.util.Collections;
import java.util.List;
import me.zeroeightsix.kami.command.Command;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.module.ModuleManager;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import me.zeroeightsix.kami.util.BlockInteractionHelper;
import me.zeroeightsix.kami.util.Wrapper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

@Module.Info(name="Surround", category=Module.Category.COMBAT)
public class Surround
extends Module {
    private final Vec3d[] surroundList = new Vec3d[]{new Vec3d(0.0, 0.0, 0.0), new Vec3d(1.0, 0.0, 0.0), new Vec3d(1.0, 1.0, 0.0), new Vec3d(0.0, 0.0, 1.0), new Vec3d(0.0, 1.0, 1.0), new Vec3d(-1.0, 0.0, 0.0), new Vec3d(-1.0, 1.0, 0.0), new Vec3d(0.0, 0.0, -1.0), new Vec3d(0.0, 1.0, -1.0)};
    private final Vec3d[] surroundListFull = new Vec3d[]{new Vec3d(0.0, 0.0, 0.0), new Vec3d(1.0, 0.0, 0.0), new Vec3d(1.0, 1.0, 0.0), new Vec3d(1.0, 0.0, 1.0), new Vec3d(1.0, 1.0, 1.0), new Vec3d(0.0, 0.0, 1.0), new Vec3d(0.0, 1.0, 1.0), new Vec3d(-1.0, 0.0, 1.0), new Vec3d(-1.0, 1.0, 1.0), new Vec3d(-1.0, 0.0, 0.0), new Vec3d(-1.0, 1.0, 0.0), new Vec3d(-1.0, 0.0, -1.0), new Vec3d(-1.0, 1.0, -1.0), new Vec3d(0.0, 0.0, -1.0), new Vec3d(0.0, 1.0, -1.0), new Vec3d(1.0, 0.0, -1.0), new Vec3d(1.0, 1.0, -1.0)};
    private final List obsidian = Collections.singletonList(Blocks.field_150343_Z);
    private Setting toggleable = this.register(Settings.b("Toggleable", true));
    private Setting slowmode = this.register(Settings.b("Slow", false));
    private Setting full = this.register(Settings.b("Full", false));
    private Vec3d[] surroundTargets;
    private int blocksPerTick = 3;
    private BlockPos basePos;
    private boolean slowModeSwitch = false;
    private int offsetStep = 0;
    private int oldSlot = 0;

    @Override
    public void onUpdate() {
        if (!this.isDisabled() && Surround.mc.field_71439_g != null && !ModuleManager.isModuleEnabled("Freecam")) {
            if (this.slowModeSwitch) {
                this.slowModeSwitch = false;
            } else {
                if (this.offsetStep == 0) {
                    this.init();
                }
                for (int i = 0; i < this.blocksPerTick; ++i) {
                    if (this.offsetStep >= this.surroundTargets.length) {
                        this.end();
                        return;
                    }
                    Vec3d offset = this.surroundTargets[this.offsetStep];
                    this.placeBlock(new BlockPos((Vec3i)this.basePos.func_177963_a(offset.field_72450_a, offset.field_72448_b, offset.field_72449_c)));
                    ++this.offsetStep;
                }
                this.slowModeSwitch = true;
            }
        }
    }

    private void placeBlock(BlockPos blockPos) {
        if (Wrapper.getWorld().func_180495_p(blockPos).func_185904_a().func_76222_j()) {
            int newSlot = -1;
            for (int i = 0; i < 9; ++i) {
                Block block;
                ItemStack stack = Wrapper.getPlayer().field_71071_by.func_70301_a(i);
                if (stack == ItemStack.field_190927_a || !(stack.func_77973_b() instanceof ItemBlock) || BlockInteractionHelper.blackList.contains((Object)(block = ((ItemBlock)stack.func_77973_b()).func_179223_d())) || block instanceof BlockContainer || !Block.func_149634_a((Item)stack.func_77973_b()).func_176223_P().func_185913_b() || ((ItemBlock)stack.func_77973_b()).func_179223_d() instanceof BlockFalling && Wrapper.getWorld().func_180495_p(blockPos.func_177977_b()).func_185904_a().func_76222_j() || !this.obsidian.contains((Object)block)) continue;
                newSlot = i;
                break;
            }
            if (newSlot == -1) {
                if (!((Boolean)this.toggleable.getValue()).booleanValue()) {
                    Command.sendChatMessage("Surround: Please Put Obsidian in Hotbar");
                }
                this.end();
            } else {
                Wrapper.getPlayer().field_71071_by.field_70461_c = newSlot;
                if (BlockInteractionHelper.checkForNeighbours(blockPos)) {
                    BlockInteractionHelper.placeBlockScaffold(blockPos);
                }
            }
        }
    }

    private void init() {
        this.basePos = new BlockPos(Surround.mc.field_71439_g.func_174791_d()).func_177977_b();
        if (((Boolean)this.slowmode.getValue()).booleanValue()) {
            this.blocksPerTick = 1;
        }
        this.surroundTargets = (Boolean)this.full.getValue() != false ? this.surroundListFull : this.surroundList;
    }

    private void end() {
        this.offsetStep = 0;
        if (!((Boolean)this.toggleable.getValue()).booleanValue()) {
            this.disable();
        }
    }

    @Override
    protected void onEnable() {
        Surround.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)Surround.mc.field_71439_g, CPacketEntityAction.Action.START_SNEAKING));
        this.oldSlot = Wrapper.getPlayer().field_71071_by.field_70461_c;
    }

    @Override
    protected void onDisable() {
        Surround.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)Surround.mc.field_71439_g, CPacketEntityAction.Action.STOP_SNEAKING));
        Wrapper.getPlayer().field_71071_by.field_70461_c = this.oldSlot;
    }
}


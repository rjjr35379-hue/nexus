/*MIT License

Copyright (c) 2026 GeckoLib

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package rj.nexus.systems.util;

import net.minecraft.client.renderer.entity.state.*;

/// Helper class for RenderState-related functionality
public final class RenderStateUtil {
    /// Create a partial-clone of an existing unknown RenderState into a new [HumanoidRenderState] for the purpose of
    /// armor rendering, which explicitly requires an `HumanoidRenderState`
    ///
    /// Because this is only being used for armor rendering, we don't need an exhaustive copy of the `RenderState` and instead focus
    /// solely on the data points we know are needed.
    ///
    /// If you are doing custom modeling and a data point here is missing and causing you issues, let me know in Discord and I'll add it
    public static HumanoidRenderState makeMinimalArmorRenderingClone(final HumanoidRenderState newRenderState, final EntityRenderState oldRenderState) {

        newRenderState.entityType = oldRenderState.entityType; // Optional
        newRenderState.x = oldRenderState.x; // Optional
        newRenderState.y = oldRenderState.y; // Optional
        newRenderState.z = oldRenderState.z; // Optional
        newRenderState.ageInTicks = oldRenderState.ageInTicks;
        newRenderState.eyeHeight = oldRenderState.eyeHeight; // Optional
        newRenderState.distanceToCameraSq = oldRenderState.distanceToCameraSq; // Optional
        newRenderState.isInvisible = oldRenderState.isInvisible; // Optional
        newRenderState.isDiscrete = oldRenderState.isDiscrete; // Optional
        newRenderState.displayFireAnimation = oldRenderState.displayFireAnimation; // Optional
        newRenderState.lightCoords = oldRenderState.lightCoords; // Optional
        newRenderState.outlineColor = oldRenderState.outlineColor; // Optional

        if (oldRenderState instanceof LivingEntityRenderState livingEntityState) {
            newRenderState.bodyRot = livingEntityState.bodyRot; // Optional
            newRenderState.yRot = livingEntityState.yRot;
            newRenderState.xRot = livingEntityState.xRot;
            newRenderState.deathTime = livingEntityState.deathTime; // Optional
            newRenderState.walkAnimationPos = livingEntityState.walkAnimationPos;
            newRenderState.walkAnimationSpeed = livingEntityState.walkAnimationSpeed;
            newRenderState.scale = livingEntityState.scale; // Optional
            newRenderState.ageScale = livingEntityState.ageScale;
            newRenderState.isUpsideDown = livingEntityState.isUpsideDown; // Optional
            newRenderState.isFullyFrozen = livingEntityState.isFullyFrozen; // Optional
            newRenderState.isBaby = livingEntityState.isBaby; // Optional
            newRenderState.isInWater = livingEntityState.isInWater; // Optional
            newRenderState.isAutoSpinAttack = livingEntityState.isAutoSpinAttack; // Optional
            newRenderState.hasRedOverlay = livingEntityState.hasRedOverlay; // Optional
            newRenderState.isInvisibleToPlayer = livingEntityState.isInvisibleToPlayer; // Optional
            newRenderState.bedOrientation = livingEntityState.bedOrientation; // Optional
            newRenderState.pose = livingEntityState.pose; // Optional

            if (livingEntityState instanceof ArmedEntityRenderState armedState) {
                newRenderState.mainArm = armedState.mainArm;
                newRenderState.rightArmPose = armedState.rightArmPose;
                newRenderState.leftArmPose = armedState.leftArmPose;

                if (armedState instanceof HumanoidRenderState humanoidState) {
                    newRenderState.swimAmount = humanoidState.swimAmount;
                    newRenderState.attackTime = humanoidState.attackTime;
                    newRenderState.speedValue = humanoidState.speedValue;
                    newRenderState.maxCrossbowChargeDuration = humanoidState.maxCrossbowChargeDuration;
                    newRenderState.ticksUsingItem = humanoidState.ticksUsingItem;
                    newRenderState.attackArm = humanoidState.attackArm;
                    newRenderState.useItemHand = humanoidState.useItemHand;
                    newRenderState.isCrouching = humanoidState.isCrouching;
                    newRenderState.isFallFlying = humanoidState.isFallFlying;
                    newRenderState.isVisuallySwimming = humanoidState.isVisuallySwimming; // Optional
                    newRenderState.isPassenger = humanoidState.isPassenger;
                    newRenderState.isUsingItem = humanoidState.isUsingItem;
                    newRenderState.elytraRotX = humanoidState.elytraRotX; // Optional
                    newRenderState.elytraRotY = humanoidState.elytraRotY; // Optional
                    newRenderState.elytraRotZ = humanoidState.elytraRotZ; // Optional
                    newRenderState.headEquipment = humanoidState.headEquipment; // Optional
                    newRenderState.chestEquipment = humanoidState.chestEquipment; // Optional
                    newRenderState.legsEquipment = humanoidState.legsEquipment; // Optional
                    newRenderState.feetEquipment = humanoidState.feetEquipment; // Optional
                }
            }
        }

        return newRenderState;
    }

    private RenderStateUtil() {}
}
package rj.nexus.systems.util;
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
import com.mojang.blaze3d.Blaze3D;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.MoonPhase;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

/// Helper class for segregating client-side code
public final class ClientUtil {
    /// Get the player on the client
    public static @Nullable Player getClientPlayer() {
        return Minecraft.getInstance().player;
    }

    /// Gets the current level on the client
    public static @Nullable Level getLevel() {
        return Minecraft.getInstance().level;
    }

    /// Whether the local (client) player has a cape
    public static boolean clientPlayerHasCape() {
        final LocalPlayer player = Minecraft.getInstance().player;

        return player != null && player.getSkin().cape() != null;
    }

    /// Get the current camera position
    public static Vec3 getCameraPos() {
        return Minecraft.getInstance().gameRenderer.mainCamera.position();
    }

    /// Helper method to check for first-person camera mode
    ///
    /// Split off to preserve side-agnosticism of the Molang system
    public static boolean isFirstPerson() {
        return Minecraft.getInstance().options.getCameraType().isFirstPerson();
    }

    /// Get the current phase of the moon on the client world
    public static MoonPhase getClientMoonPhase() {
        return Minecraft.getInstance().levelRenderer.levelRenderState.skyRenderState.moonPhase;
    }

    /// Get the game time for the client world, or a global game time if no world is loaded
    ///
    /// Returned value is in ticks
    ///
    /// Note that due to vanilla desync issues, the level will occasionally go backwards 1 tick
    public static double getCurrentTick() {
        return getCurrentTick(null);
    }

    /// Get the game time for the client world, or a global game time if no world is loaded
    ///
    /// Returned value is in ticks
    ///
    /// Note that due to vanilla desync issues, the level will occasionally go backwards 1 tick
    public static double getCurrentTick(@Nullable Float partialTick) {
        final Minecraft mc = Minecraft.getInstance();

        return mc.level != null ?
                mc.level.getGameTime() + (partialTick != null ? partialTick : mc.getDeltaTracker().getGameTimeDeltaPartialTick(false)) :
                Blaze3D.getTime() * 20d;
    }

    @ApiStatus.Internal
    public static int getVisibleEntityCount() {
        final Minecraft mc = Minecraft.getInstance();

        if (mc.level == null)
            return 0;

        return mc.levelRenderer.levelRenderState.entityRenderStates.size();
    }

    private ClientUtil() {}
}
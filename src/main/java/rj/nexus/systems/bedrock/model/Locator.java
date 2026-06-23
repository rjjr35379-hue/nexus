package rj.nexus.systems.bedrock.model;
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


import com.google.gson.Gson;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonParseException;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
import rj.nexus.systems.bedrock.model.pojo.GeoLocator;
import rj.nexus.systems.util.JsonUtil;


/// Container class for a single geometry bone locator, only used for intermediary steps between .json deserialization and GeckoLib object creation
///
/// This information isn't used by GeckoLib natively
///
/// @param offset The position of this locator, relative to the bone it belongs to
/// @param rotation The rotation of this locator, in degrees
/// @see <a href="https://learn.microsoft.com/en-us/minecraft/creator/reference/content/schemasreference/schemas/minecraftschema_geometry_1.21.0?view=minecraft-bedrock-experimental">Bedrock Geometry Spec 1.21.0</a>
@ApiStatus.Internal
public record Locator(@Nullable Vec3 offset, @Nullable Vec3 rotation) {
    /// Parse a GeometryLocators instance from raw .json input via [Gson]
    public static JsonDeserializer<Locator> gsonDeserializer() throws JsonParseException {
        return (json, _, _) -> {
            final boolean isArray = json.isJsonArray();
            final Vec3 offset = JsonUtil.jsonToVec3(isArray ? json.getAsJsonArray() : GsonHelper.getAsJsonArray(json.getAsJsonObject(), "offset"));
            final Vec3 rotation = isArray ? null : JsonUtil.jsonToVec3(GsonHelper.getAsJsonArray(json.getAsJsonObject(), "rotation"));

            return new Locator(offset, rotation);
        };

    }

    /// Bake this `GeometryLocator` instance into the final [GeoLocator] instance that GeckoLib uses for position listeners
    public GeoLocator bake(String name, BedrockBone parentBone) {
        final Vec3 offset = this.offset == null ? Vec3.ZERO : this.offset;
        final Vec3 rotation = this.rotation == null ? Vec3.ZERO : this.rotation.multiply(-Mth.DEG_TO_RAD, -Mth.DEG_TO_RAD, Mth.DEG_TO_RAD);

        return new GeoLocator(parentBone, name, (float)-offset.x, (float)offset.y, (float)offset.z, (float)rotation.x, (float)rotation.y, (float)rotation.z);
    }
}
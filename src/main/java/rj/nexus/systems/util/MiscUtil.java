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

import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;

import java.util.Collection;

/// Helper class for miscellaneous functions that don't fit into the other util classes
public final class MiscUtil {
    /// Converts a [Direction] to a rotational float for rotation purposes
    public static float getDirectionAngle(Direction direction) {
        return switch(direction) {
            case SOUTH -> 90f;
            case NORTH -> 270f;
            case EAST -> 180f;
            default -> 0f;
        };
    }

    /// Return whether the two floating point values should be considered numerically equal
    ///
    /// This is important because of the way floating point values work, there may not necessarily be
    /// 1:1 equality between two functionally equal floating point values
    public static boolean areFloatsEqual(double a, double b) {
        return Math.abs(a - b) < Mth.EPSILON;
    }

    /// Special helper function for interpolating yaw.
    ///
    /// This exists because yaw in Minecraft handles its yaw a bit strangely; and can cause incorrect results if interpolated without accounting for special cases
    public static double lerpYaw(double delta, double start, double end) {
        start = Mth.wrapDegrees(start);
        end = Mth.wrapDegrees(end);
        double diff = start - end;
        end = diff > 180 || diff < -180 ? start + Math.copySign(360 - Math.abs(diff), diff) : end;

        return Mth.lerp(delta, start, end);
    }

    /// Combine one or more collections into a new collection holding all elements from all collections
    ///
    /// Retains iteration order of inserted elements if the provided collections are ordered
    @SafeVarargs
    public static <E, C extends Collection<E>> C mergeCollections(Int2ObjectFunction<C> factory, C... collections) {
        return switch (collections.length) {
            case 0 -> factory.apply(0);
            case 1 -> collections[0];
            default -> {
                int size = 0;

                for (C collection : collections) {
                    size += collection.size();
                }

                C merged = factory.apply(size);

                for (C collection : collections) {
                    merged.addAll(collection);
                }

                yield merged;
            }
        };
    }

    private MiscUtil() {}
}
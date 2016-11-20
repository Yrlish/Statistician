/*
 * MIT License
 *
 * Copyright (c) 2016 Dennis Alexandersson (Yrlish)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.yrlish.statistician.utilities;

import org.spongepowered.api.entity.Entity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EntityHelper {
    private EntityHelper() {
    }

    /**
     * Iterate an entity and return all passenger entities.
     *
     * @param entity The base entity to iterate
     * @return A set of all entities, including the base entity.
     */
    public static Set<Entity> getPassengerStack(Entity entity) {
        Set<Entity> set = new HashSet<>();

        if (entity != null) {
            set.add(entity);

            List<Entity> po = entity.getPassengers();
            set.addAll(po);
        }

        return set;
    }
}

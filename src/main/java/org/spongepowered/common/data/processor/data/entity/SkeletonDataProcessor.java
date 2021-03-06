/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.common.data.processor.data.entity;

import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityStray;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableSkeletonData;
import org.spongepowered.api.data.manipulator.mutable.entity.SkeletonData;
import org.spongepowered.api.data.type.SkeletonType;
import org.spongepowered.api.data.type.SkeletonTypes;
import org.spongepowered.api.data.value.ValueContainer;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.common.data.manipulator.mutable.entity.SpongeSkeletonData;
import org.spongepowered.common.data.processor.common.AbstractEntitySingleDataProcessor;
import org.spongepowered.common.data.util.DataConstants;
import org.spongepowered.common.data.value.immutable.ImmutableSpongeValue;
import org.spongepowered.common.data.value.mutable.SpongeValue;
import org.spongepowered.common.entity.SpongeEntityConstants;

import java.util.Optional;

public class SkeletonDataProcessor
        extends AbstractEntitySingleDataProcessor<AbstractSkeleton, SkeletonType, Value<SkeletonType>, SkeletonData, ImmutableSkeletonData> {

    public SkeletonDataProcessor() {
        super(AbstractSkeleton.class, Keys.SKELETON_TYPE);
    }

    @Override
    public DataTransactionResult removeFrom(ValueContainer<?> entity) {
        return DataTransactionResult.failNoData();
    }

    @Override
    protected boolean set(AbstractSkeleton entity, SkeletonType value) {
        throw new UnsupportedOperationException("SkeletonData is deprecated - skeleton types are now separate entities!");
    }

    @Override
    protected Optional<SkeletonType> getVal(AbstractSkeleton entity) {
        if (entity instanceof EntitySkeleton) {
            return Optional.of(SkeletonTypes.NORMAL);
        } else if (entity instanceof EntityStray) {
            return Optional.of(SkeletonTypes.STRAY);
        } else if (entity instanceof EntityWitherSkeleton) {
            return Optional.of(SkeletonTypes.WITHER);
        }
        return Optional.empty();
    }

    @Override
    protected Value<SkeletonType> constructValue(SkeletonType actualValue) {
        return new SpongeValue<>(Keys.SKELETON_TYPE, DataConstants.Catalog.DEFAULT_SKELETON, actualValue);
    }

    @Override
    protected ImmutableValue<SkeletonType> constructImmutableValue(SkeletonType value) {
        return ImmutableSpongeValue.cachedOf(Keys.SKELETON_TYPE, DataConstants.Catalog.DEFAULT_SKELETON, value);
    }

    @Override
    protected SkeletonData createManipulator() {
        return new SpongeSkeletonData();
    }

}

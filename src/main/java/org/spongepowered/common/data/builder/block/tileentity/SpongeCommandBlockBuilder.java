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
package org.spongepowered.common.data.builder.block.tileentity;

import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import org.spongepowered.api.block.tileentity.CommandBlock;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.util.persistence.InvalidDataException;
import org.spongepowered.common.data.util.DataQueries;
import org.spongepowered.common.text.SpongeTexts;

import java.util.Optional;

public class SpongeCommandBlockBuilder extends AbstractTileBuilder<CommandBlock> {

    public SpongeCommandBlockBuilder() {
        super(CommandBlock.class, 1);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected Optional<CommandBlock> buildContent(DataView container) throws InvalidDataException {
        return super.buildContent(container).flatMap(commandBlock -> {
            if (!container.contains(DataQueries.STORED_COMMAND, DataQueries.SUCCESS_COUNT, DataQueries.DOES_TRACK_OUTPUT)) {
                ((TileEntity) commandBlock).invalidate();
                return Optional.empty();
            }
            CommandBlockLogic cmdBlockLogic = ((TileEntityCommandBlock) commandBlock).getCommandBlockLogic();
            cmdBlockLogic.setCommand(container.getString(DataQueries.STORED_COMMAND).get());
            cmdBlockLogic.successCount = container.getInt(DataQueries.SUCCESS_COUNT).get();
            cmdBlockLogic.setTrackOutput(container.getBoolean(DataQueries.DOES_TRACK_OUTPUT).get());
            if (cmdBlockLogic.shouldTrackOutput()) {
                cmdBlockLogic.setLastOutput(SpongeTexts.toComponent(SpongeTexts.fromLegacy(
                        container.getString(DataQueries.TRACKED_OUTPUT).get())));
            }
            ((TileEntityCommandBlock)commandBlock).validate();
            return Optional.of(commandBlock);
        });
    }
}

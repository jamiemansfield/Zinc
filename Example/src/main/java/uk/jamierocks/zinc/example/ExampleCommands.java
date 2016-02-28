/*
 * This file is part of Zinc, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2015-2016, Jamie Mansfield <https://github.com/jamierocks>
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
package uk.jamierocks.zinc.example;

import com.google.common.collect.Lists;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.text.Text;
import uk.jamierocks.zinc.Command;
import uk.jamierocks.zinc.TabComplete;

import java.util.List;

public class ExampleCommands {

    @Command(name = "example")
    public CommandResult exampleCommand(CommandSource source, CommandArgs args) {
        source.sendMessage(Text.of("This is the base command."));
        return CommandResult.success();
    }

    @Command(parent = "example",
            name = "sub")
    public CommandResult exampleSubCommand(CommandSource source, CommandArgs args) {
        source.sendMessage(Text.of("This is a sub command."));
        return CommandResult.success();
    }

    @TabComplete(name = "example")
    public List<String> tabComplete(CommandSource source, String args) {
        return Lists.newArrayList();
    }
}

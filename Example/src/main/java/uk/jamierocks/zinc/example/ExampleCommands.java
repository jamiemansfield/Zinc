/*
 * This file is part of Zinc, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2015, Jamie Mansfield <https://github.com/jamierocks>
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

import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import uk.jamierocks.zinc.Command;

public class ExampleCommands {

    @Command(name = "example")
    public CommandResult exampleCommand(CommandSource source, String arguments) {
        source.sendMessage(Texts.of("This is the base command."));
        return CommandResult.success();
    }

    @Command(parent = "example",
            name = "sub")
    public CommandResult exampleSubCommand(CommandSource source, String arguments) {
        source.sendMessage(Texts.of("This is a sub command."));
        return CommandResult.success();
    }
}

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
package uk.jamierocks.zinc;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandCallable;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandArgs;
import org.spongepowered.api.util.command.args.parsing.InputTokenizers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

class ZincCommandCallable implements CommandCallable {

    private final Logger logger;
    private final Command command;
    private final Object instance;
    private final Method method;

    protected ZincCommandCallable(Logger logger, Command command, Object instance, Method method) {
        this.logger = logger;
        this.command = command;
        this.instance = instance;
        this.method = method;
    }

    @Override
    public CommandResult process(CommandSource source, String arguments) throws CommandException {
        try {
            return (CommandResult) this.method.invoke(this.instance, source,
                    new CommandArgs(arguments, InputTokenizers.quotedStrings(false)
                            .tokenize(arguments, false)));
        } catch (IllegalAccessException e) {
            this.logger.error("Failed to invoke instance", e);
        } catch (InvocationTargetException e) {
            this.logger.error("Failed to invoke instance", e);
        }
        return CommandResult.empty();
    }

    @Override
    public List<String> getSuggestions(CommandSource source, String arguments) throws CommandException {
        return Lists.newArrayList();
    }

    @Override
    public boolean testPermission(CommandSource source) {
        for (String perm : this.command.permissions()) {
            if (!source.hasPermission(perm)) return false;
        }
        return true;
    }

    @Override
    public Optional<Text> getShortDescription(CommandSource source) {
        return Optional.of(Texts.of(this.command.description()));
    }

    @Override
    public Optional<Text> getHelp(CommandSource source) {
        return Optional.empty();
    }

    @Override
    public Text getUsage(CommandSource source) {
        return Texts.of(this.command.usage());
    }
}

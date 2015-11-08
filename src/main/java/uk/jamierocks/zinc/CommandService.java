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
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Game;
import org.spongepowered.api.util.command.CommandCallable;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.dispatcher.SimpleDispatcher;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Represents a command service, for Zinc commands.
 *
 * @author Jamie Mansfield.
 */
public class CommandService {

    private final Game game;

    public CommandService(Game game) {
        this.game = game;
    }

    /**
     * Registers all the commands from the given holder.
     *
     * @param plugin the owning plugin.
     * @param holder the command holder.
     */
    public void registerCommands(Object plugin, Object holder) {
        Map<CommandCallable, Command> subCommands = Maps.newHashMap();
        for (Method method : holder.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Command.class)) {
                Command command = method.getAnnotation(Command.class);

                if (method.getParameterTypes()[0] == CommandSource.class &&
                        method.getParameterTypes()[1] == String.class) {
                    if (StringUtils.isEmpty(command.parent())) {
                        CommandCallable commandCallable =
                                new SpongeCommandCallable(LoggerFactory.getLogger(plugin.getClass()),
                                        command, holder, method);
                        SimpleDispatcher dispatcher = new SimpleDispatcher();
                        dispatcher.register(commandCallable, "");

                        this.game.getCommandDispatcher()
                                .register(plugin, dispatcher, Lists.asList(command.name(), command.aliases()));
                    } else {
                        CommandCallable commandCallable =
                                new SpongeCommandCallable(LoggerFactory.getLogger(plugin.getClass()),
                                        command, holder, method);
                        subCommands.put(commandCallable, command);
                    }
                }

            }
        }
        for (CommandCallable commandCallable : subCommands.keySet()) {
            Command command = subCommands.get(commandCallable);
            SimpleDispatcher dispatcher =
                    (SimpleDispatcher) this.game.getCommandDispatcher().get(command.parent()).get()
                            .getCallable();
            dispatcher.register(commandCallable, Lists.asList(command.name(), command.aliases()));
        }
    }
}

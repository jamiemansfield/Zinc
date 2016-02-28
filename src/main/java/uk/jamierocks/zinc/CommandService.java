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
package uk.jamierocks.zinc;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Game;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandArgs;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Represents a command service, for Zinc commands.
 *
 * @author Jamie Mansfield.
 */
public class CommandService {

    protected static final Logger ZINC_LOGGER = LoggerFactory.getLogger("Zinc");

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
        Logger pluginLogger = LoggerFactory.getLogger(plugin.getClass());
        for (Method method : holder.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Command.class)) {
                Command command = method.getAnnotation(Command.class);

                if (method.getParameterTypes().length == 2 &&
                        method.getParameterTypes()[0] == CommandSource.class &&
                        method.getParameterTypes()[1] == CommandArgs.class &&
                        method.getReturnType() == CommandResult.class) {

                    CommandCallable commandCallable =
                            new ZincCommandCallable(pluginLogger, command, holder, method);
                    if (StringUtils.isEmpty(command.parent())) {
                        this.game.getCommandManager()
                                .register(plugin, new ZincDispatcher(commandCallable),
                                        Lists.asList(command.name(), command.aliases()));
                    } else {
                        subCommands.put(commandCallable, command);
                    }
                } else {
                    if (method.getReturnType() != CommandResult.class) {
                        ZINC_LOGGER.error(String.format("Command has wrong return type: %s#%s Should be %s",
                                holder.getClass().getName(), method.getName(), CommandResult.class.getName()));
                    }
                    if (method.getParameterTypes().length != 2 ||
                            method.getParameterTypes()[0] != CommandSource.class ||
                            method.getParameterTypes()[1] != CommandArgs.class) {
                        ZINC_LOGGER.error(String.format("Command has wrong argument types: %s#%s Should have %s and %s",
                                holder.getClass().getName(), method.getName(),
                                CommandSource.class.getName(), CommandArgs.class.getName()));
                    }
                }
            } else if (method.isAnnotationPresent(TabComplete.class)) {
                TabComplete tabComplete = method.getAnnotation(TabComplete.class);

                if (method.getParameterTypes().length == 2 &&
                        method.getParameterTypes()[0] == CommandSource.class &&
                        method.getParameterTypes()[1] == String.class &&
                        method.getReturnType() == List.class) {

                    final ZincDispatcher.SuggestionHandler suggestionHandler = (src, arguments) -> {
                        try {
                            return (List<String>) method.invoke(holder, src, arguments);
                        } catch (IllegalAccessException e) {
                            pluginLogger.error("Failed to invoke instance", e);
                        } catch (InvocationTargetException e) {
                            pluginLogger.error("Failed to invoke instance", e);
                        }
                        return Lists.newArrayList();
                    };
                    if (this.game.getCommandManager().get(tabComplete.name()).isPresent() &&
                            this.game.getCommandManager().get(tabComplete.name()).get()
                                    .getCallable() instanceof ZincDispatcher) {
                        ZincDispatcher dispatcher =
                                (ZincDispatcher) this.game.getCommandManager().get(tabComplete.name()).get()
                                        .getCallable();
                        dispatcher.setSuggestionHandler(suggestionHandler);
                    } else {
                        ZINC_LOGGER.error(
                                String.format("Tab complete attempted to register, but parent command wasn't found: %s",
                                        tabComplete.name()));
                    }
                } else {
                    if (method.getReturnType() != List.class) {
                        ZINC_LOGGER.error(String.format("Tab complete has wrong return type: %s#%s Should be %s",
                                holder.getClass().getName(), method.getName(), List.class.getName()));
                    }
                    if (method.getParameterTypes().length != 2 ||
                            method.getParameterTypes()[0] != CommandSource.class ||
                            method.getParameterTypes()[1] != String.class) {
                        ZINC_LOGGER.error(String.format("Tab complete has wrong argument types: %s#%s Should have %s and %s",
                                holder.getClass().getName(), method.getName(),
                                CommandSource.class.getName(), String.class.getName()));
                    }
                }
            }
        }
        for (CommandCallable commandCallable : subCommands.keySet()) {
            Command command = subCommands.get(commandCallable);
            if (this.game.getCommandManager().get(command.parent()).isPresent() &&
                    this.game.getCommandManager().get(command.parent()).get()
                            .getCallable() instanceof ZincDispatcher) {
                ZincDispatcher dispatcher =
                        (ZincDispatcher) this.game.getCommandManager().get(command.parent()).get()
                                .getCallable();
                dispatcher.register(commandCallable, Lists.asList(command.name(), command.aliases()));
            } else {
                ZINC_LOGGER.error(String.format("Sub command was registered, but parent command wasn't found: %s",
                        command.parent()));
            }
        }
    }
}

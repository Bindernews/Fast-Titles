package com.github.bindernews.fasttitles;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Server;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;

public class ACommandLoader {

	private Constructor<PluginCommand> pluginCommandCtor;
	private CommandMap serverCommandMap;
	private Plugin plugin;
	private Server server;

	public ACommandLoader(Plugin plug) throws ReflectiveOperationException {
		plugin = plug;
		server = plugin.getServer();
		
		// get serverCommandMap
		Method mt = server.getClass().getMethod("getCommandMap");
		mt.setAccessible(true);
		serverCommandMap = (CommandMap)mt.invoke(server);
		
		// get pluginCommandCtor
		pluginCommandCtor = PluginCommand.class.getConstructor(String.class, Plugin.class);
		pluginCommandCtor.setAccessible(true);
	}
	
	/**
	 * @return The server's CommandMap
	 */
	public CommandMap getCommandMap() {
		return serverCommandMap;
	}
	
	public List<ACommand.Executor> processClass(Class<?> clazz, Object instance) {
		List<ACommand.Executor> cmdList = new ArrayList<ACommand.Executor>();
		Method[] methodList = clazz.getMethods();
		for(Method method : methodList) {
			ACommand acmd = method.getAnnotation(ACommand.class);
			// skip it if it doesn't have the annotation
			if (acmd == null) {
				continue;
			}
			try {
				Method completer = null;
				// ensure method has the correct signaure
				if (!testCommandSig(method, boolean.class)) {
					throw new CommandException("Invalid command signature");
				}
				if (!acmd.completer().isEmpty()) {
					try {
						completer = clazz.getMethod(acmd.completer(), CommandContext.class);
						if (!testCommandSig(completer, List.class)) {
							throw new CommandException("Completer method not found (invalid signature?)");
						}
					} catch (NoSuchMethodException e) {
						throw new CommandException("Completer method not found (invalid signature?)");
					}
				}
				ACommand.Executor cmdExec = new ACommand.Executor(acmd, instance, method, completer);
				cmdList.add(cmdExec);
			} catch (CommandException e) {
				plugin.getLogger().log(Level.WARNING, "@ACommand on method " + clazz.getName() + "."
						+ method.getName() + ": " + e.getMessage() + " Please report this to the developer!");
			}
		}
		return cmdList;
	}
	
	public List<PluginCommand> makePluginCommands(List<ACommand.Executor> executors) {
		try {
			List<PluginCommand> cmdList = new ArrayList<PluginCommand>(executors.size());
			for(ACommand.Executor exec : executors) {
				PluginCommand pcmd = newPluginCommand(exec.annot.alias()[0]);
				exec.setAsExecutor(pcmd);
				cmdList.add(pcmd);
			}
			return cmdList;
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}
	
	public List<ACommand.Wrapper> makeCommandWrappers(List<ACommand.Executor> executors) {
		List<ACommand.Wrapper> cmdList = new ArrayList<ACommand.Wrapper>(executors.size());
		for(ACommand.Executor exec : executors) {
			ACommand.Wrapper ecmd = new ACommand.Wrapper(exec);
			cmdList.add(ecmd);
		}
		return cmdList;
	}
	
	public PluginCommand newPluginCommand(String name) throws ReflectiveOperationException {
		return pluginCommandCtor.newInstance(name, plugin);
	}
	
	private static boolean testCommandSig(Method method, Class<?> returnType) {
		Class<?>[] paramTypes = method.getParameterTypes();
		return (paramTypes.length == 1 && paramTypes[0] == CommandContext.class && method.getReturnType() == returnType);
	}
}

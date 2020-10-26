package com.dont.modelo.models.bukkit;

import com.dont.modelo.Terminal;
import com.dont.modelo.database.MainDataManager;
import com.dont.modelo.models.database.User;
import com.dont.modelo.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class DoCommand implements CommandExecutor {

    protected final Terminal main;
    protected final MainDataManager manager;

    public DoCommand(Terminal main, String command) {
        this.main = main;
        this.manager = main.getDataManager();
        main.getCommand(command).setExecutor(this);
        Utils.debug(Utils.LogType.INFO, this.getClass().getSimpleName() + " carregado");
    }

    public abstract String handle(CommandSender sender, String[] args) throws DoCommandException;

    public final boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            String message = handle(sender, args);
            if (message != null) sender.sendMessage(message);
        } catch (DoCommandException e) {
            sender.sendMessage(e.getMessage());
        }
        return false;
    }

    protected Player onlyPlayer(CommandSender sender) throws DoCommandException {
        if (sender instanceof Player) {
            return (Player) sender;
        } else {
            throw new DoCommandException("§cSomente jogadores in-game podem executar este comando.");
        }
    }

    protected void permission(CommandSender sender, String permission) throws DoCommandException {
        if (!sender.hasPermission(permission)) {
            throw new DoCommandException("§cVocê não tem permissão!");
        }
    }

    protected <T> String validTypes(Collection<T> collection, Function<T, String> extractor) {
        return collection.stream().map(extractor).collect(Collectors.joining(", "));
    }

    protected String validTypes(Class<? extends Enum> clazz) {
        return Arrays.stream((Enum<?>[]) clazz.getEnumConstants()).map(Enum::name).map(String::toLowerCase).collect(Collectors.joining(", "));
    }

    protected Player parsePlayer(String arg) throws DoCommandException {
        Player target = Bukkit.getPlayer(arg);
        if (target == null) {
            throw new DoCommandException("§cJogador §f" + arg + " §cnão encontrado.");
        }
        return target;
    }

    protected int parseInt(String arg) throws DoCommandException {
        try {
            return Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            throw new DoCommandException("§cNúmero §f" + arg + " §cinválido.");
        }
    }

    protected double parseDouble(String arg) throws DoCommandException {
        try {
            return Double.parseDouble(arg);
        } catch (NumberFormatException e) {
            throw new DoCommandException("§cNúmero §f" + arg + " §cinválido.");
        }
    }

    protected <T extends Enum<T>> T parseEnum(String enumName, Class<T> clazz) throws DoCommandException {
        try {
            return Enum.valueOf(clazz, enumName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new DoCommandException("§cTipo §f" + enumName + "§c inválido.");
        }
    }

    protected <T> T parseT(Collection<T> collection, Function<T, Object> predicate, Object filter, String type) throws DoCommandException {
        return collection.stream()
                .filter(t -> test(predicate.apply(t), filter))
                .findFirst()
                .orElseThrow(() -> new DoCommandException("§c" + type + " §f" + filter + " §cnão encontrado."));
    }

    protected <T> T parseT(Collection<T> collection, Predicate<T> predicate, String type) throws DoCommandException {
        return collection.stream()
                .filter(predicate)
                .findFirst()
                .orElseThrow(() -> new DoCommandException("§c" + type + " não encontrado."));
    }

    private boolean test(Object o1, Object o2) {
        return o1 instanceof String && o2 instanceof String ? ((String) o1).equalsIgnoreCase((String) o2) : o1.equals(o2);
    }

    protected User parseUser(String user) {
        return manager.USERS.getCached(user);
    }

    protected User parseUser(Player user) {
        return parseUser(user.getName());
    }

}

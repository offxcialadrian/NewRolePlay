package de.newrp.Administrator;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SignEdit implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(!BuildMode.isInBuildMode(p)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(args.length < 2) {
            p.sendMessage(Messages.ERROR + "/signedit [Zeile] [Text..]");
            return true;
        }

        if(!Script.isInt(args[0])) {
            p.sendMessage(Messages.ERROR + "/signedit [Zeile] [Text..]");
            return true;
        }

        int line = Integer.parseInt(args[0]);
        StringBuilder text = new StringBuilder();
        for(int i = 1; i < args.length; i++) {
            if(i > 1) {
                text.append(" ");
            }
            text.append(args[i]);
        }

        String color = text.toString().replace("&", "§");

        if(line > 0 && line <= 4) {
            BlockState state = p.getTargetBlock(null, 100).getState();
            if (state instanceof Sign) {
                Sign sign = (Sign) state;
                sign.setLine(line-1, color);
                sign.update(true);
            } else {
                cs.sendMessage(Messages.ERROR + "Du bist an keinem Schild.");
            }
        } else {
            p.sendMessage(Messages.ERROR + "/signedit [Zeile 1-4] [Text..]");
        }

        return false;
    }
}

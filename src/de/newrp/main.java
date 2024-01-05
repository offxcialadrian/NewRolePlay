package de.newrp;

import de.newrp.API.*;
import de.newrp.Administrator.*;
import de.newrp.Berufe.*;
import de.newrp.Chat.*;
import de.newrp.Commands.DiscordCommand;
import de.newrp.Commands.Test;
import de.newrp.Entertainment.BlackJack;
import de.newrp.Entertainment.Laufband;
import de.newrp.Entertainment.Lotto;
import de.newrp.Forum.ForumCommand;
import de.newrp.Fraktion.Tazer;
import de.newrp.Government.*;
import de.newrp.House.*;
import de.newrp.Medic.ReviveCommand;
import de.newrp.News.NewsCommand;
import de.newrp.News.Zeitung;
import de.newrp.Player.*;
import de.newrp.Police.*;
import de.newrp.Runnable.*;
import de.newrp.Shop.*;
import de.newrp.TeamSpeak.*;
import de.newrp.Ticket.*;
import de.newrp.Waffen.GetAmmo;
import de.newrp.Waffen.GetGun;
import de.newrp.Waffen.Waffen;
import de.newrp.Waffen.WaffenDamage;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;

public class main extends JavaPlugin {

    private static Plugin instance;
    private static MySQL mysql;
    private static MySQL mysql2;
    private static Connection con;

    private static Connection con2;

    private static boolean test;

    public static Plugin getInstance() {
        return instance;
    }

    public static MySQL MySQL() {
        return mysql;
    }

    public static MySQL MySQL2() {
        return mysql2;
    }

    public static Connection getConnection() {
        return con;
    }

    public static Connection getForumConnection() {
        return con2;
    }

    public static boolean isTest() {
        return Bukkit.hasWhitelist();
    }

    public void onEnable() {

        Bukkit.getConsoleSender().sendMessage("§cNRP §8× §astarting with version " + this.getDescription().getVersion() + "..");

        instance = this;
        test = getServer().getMaxPlayers() == 20;

        try {
            mysql = new MySQL("85.214.163.72", "3306", "minecraft", "newrpentwicklung", "TtXf*H&gqkSTC2a2");
            con = mysql.openConnection();
            Bukkit.getConsoleSender().sendMessage("§cNRP §8× §aVerbindung zur Datenbank hergestellt.");
        } catch (Exception e1) {
            Bukkit.getConsoleSender().sendMessage("§cNRP §8× §cVerbindung zur Datenbank konnte nicht hergestellt werden.");
            Bukkit.getConsoleSender().sendMessage("§cNRP §8× §cPlugin wird gestoppt..");
            Bukkit.getConsoleSender().sendMessage("§cNRP §8× §cFehler: " + e1.getMessage());
            Bukkit.getConsoleSender().sendMessage("§cNRP §8× §cFahre Server herunter..");
            this.getServer().shutdown();
        }

        try {
            mysql2 = new MySQL("localhost", "3306", "forum", "forumadmin", "TtXf*H&gqkSTC2a2");
            con2 = mysql2.openConnection();
            Bukkit.getConsoleSender().sendMessage("§cNRP §8× §aVerbindung zur Forum-Datenbank hergestellt.");
        } catch (Exception e1) {
            Bukkit.getConsoleSender().sendMessage("§cNRP §8× §cVerbindung zur Forum-Datenbank konnte nicht hergestellt werden.");
            Bukkit.getConsoleSender().sendMessage("§cNRP §8× §cPlugin wird gestoppt..");
            Bukkit.getConsoleSender().sendMessage("§cNRP §8× §cFehler: " + e1.getMessage());
            Bukkit.getConsoleSender().sendMessage("§cNRP §8× §cFahre Server herunter..");
            this.getServer().shutdown();
        }

        try {
            TeamSpeak.connect();
            Bukkit.getConsoleSender().sendMessage("§cNRP §8× §aTeamspeak Verbindung hergestellt.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!test) new ClearLog();
        Script.prepareScoreboard();



        getCommand("goto").setTabCompleter(new GoTo());
        getCommand("debug").setTabCompleter(new DebugCommand());
        getCommand("punish").setTabCompleter(new Punish());
        getCommand("tp").setTabCompleter(new Teleport());
        getCommand("fahndung").setTabCompleter(new Fahndung());

        getCommand("sduty").setExecutor(new SDuty());
        getCommand("debug").setExecutor(new DebugCommand());
        getCommand("nrp").setExecutor(new NRPChat());
        getCommand("heal").setExecutor(new HealCommand());
        getCommand("blackjack").setExecutor(new BlackJack());
        getCommand("buildmode").setExecutor(new BuildMode());
        getCommand("goto").setExecutor(new GoTo());
        getCommand("weather").setExecutor(new WeatherControl());
        getCommand("fly").setExecutor(new FlyCommand());
        getCommand("checkinv").setExecutor(new CheckInvCommand());
        getCommand("tp").setExecutor(new Teleport());
        getCommand("tphere").setExecutor(new GetHere());
        getCommand("afk").setExecutor(new AFK());
        getCommand("punish").setExecutor(new Punish());
        getCommand("broadcast").setExecutor(new BroadcastCommand());
        getCommand("setsupporter").setExecutor(new SetSupport());
        getCommand("sudo").setExecutor(new SuDoCommand());
        getCommand("discord").setExecutor(new DiscordCommand());
        getCommand("forum").setExecutor(new ForumCommand());
        getCommand("schreien").setExecutor(new Schreien());
        getCommand("whisper").setExecutor(new Whisper());
        getCommand("me").setExecutor(new Me());
        getCommand("unban").setExecutor(new Unban());
        getCommand("kick").setExecutor(new KickCommand());
        getCommand("removewarn").setExecutor(new UnwarnCommand());
        getCommand("stats").setExecutor(new StatsCommand());
        getCommand("checkplayer").setExecutor(new CheckPlayerCommand());
        getCommand("setmoney").setExecutor(new SetMoney());
        getCommand("setlevel").setExecutor(new SetLevelCommand());
        getCommand("pay").setExecutor(new PayCommand());
        getCommand("daytime").setExecutor(new DayTime());
        getCommand("notifications").setExecutor(new Notications());
        getCommand("lotto").setExecutor(new Lotto());
        getCommand("removesupport").setExecutor(new RemoveSupport());
        getCommand("demotesupport").setExecutor(new DemoteSupport());
        getCommand("banken").setExecutor(new Banken());
        getCommand("tc").setExecutor(new TeamChat());
        getCommand("jointeam").setExecutor(new JoinTeam());
        getCommand("addteam").setExecutor(new AddTeam());
        getCommand("removeteam").setExecutor(new RemoveTeam());
        getCommand("navi").setExecutor(new NaviCommand());
        getCommand("navistop").setExecutor(new StopRoute());
        getCommand("ticket").setExecutor(new TicketCommand());
        getCommand("acceptticket").setExecutor(new AcceptTicket());
        getCommand("cancelticket").setExecutor(new CancelTicket());
        getCommand("tickets").setExecutor(new Tickets());
        getCommand("annehmen").setExecutor(new Annehmen());
        getCommand("setname").setExecutor(new SetName());
        getCommand("b").setExecutor(new BerufsChat());
        getCommand("invite").setExecutor(new InviteCommand());
        getCommand("uninvite").setExecutor(new UninviteCommand());
        getCommand("stadtkasse").setExecutor(new Stadtkasse());
        getCommand("steuern").setExecutor(new Steuern());
        getCommand("joinberuf").setExecutor(new JoinBeruf());
        getCommand("ablehnen").setExecutor(new Ablehnen());
        getCommand("arbeitslosengeld").setExecutor(new Arbeitslosengeld());
        getCommand("unmute").setExecutor(new UnmuteCommand());
        getCommand("id").setExecutor(new IDCommand());
        getCommand("wahlen").setExecutor(new Wahlen());
        getCommand("regierung").setExecutor(new RegierungCommand());
        getCommand("laufband").setExecutor(new Laufband());
        getCommand("passwort").setExecutor(new Passwort());
        getCommand("salary").setExecutor(new SalaryCommand());
        getCommand("abteilung").setExecutor(new Abteilung());
        getCommand("buy").setExecutor(new Buy());
        getCommand("buyshop").setExecutor(new BuyShop());
        getCommand("shop").setExecutor(new Shop());
        getCommand("checkshop").setExecutor(new CheckShop());
        getCommand("checkfinances").setExecutor(new Checkfinances());
        getCommand("ooc").setExecutor(new OOC());
        getCommand("bank").setExecutor(new Bank());
        getCommand("test").setExecutor(new Test());
        getCommand("registerhouse").setExecutor(new HouseRegister());
        getCommand("rent").setExecutor(new RentCommand());
        getCommand("berufskasse").setExecutor(new Berufkasse());
        getCommand("member").setExecutor(new MemberCommand());
        getCommand("addberufsdoor").setExecutor(new AddBerufsDoor());
        getCommand("installaddon").setExecutor(new InstallAddon());
        getCommand("revive").setExecutor(new ReviveCommand());
        getCommand("friedhof").setExecutor(new FriedhofInfo());
        getCommand("debugstick").setExecutor(new GetDebugStick());
        getCommand("head").setExecutor(new HeadCommand());
        getCommand("news").setExecutor(new NewsCommand());
        getCommand("mieter").setExecutor(new MieterCommand());
        getCommand("unrent").setExecutor(new UnrentCommand());
        getCommand("resetpassword").setExecutor(new ResetPassword());
        getCommand("spectate").setExecutor(new Spectate());
        getCommand("hauskasse").setExecutor(new Housekasse());
        getCommand("hausaddon").setExecutor(new HausAddonCommand());
        getCommand("addleaderrechte").setExecutor(new GiveLeaderrechte());
        getCommand("removeleaderrechte").setExecutor(new RemoveLeaderrechte());
        getCommand("removeentities").setExecutor(new ButcherCommand());
        getCommand("flyspeed").setExecutor(new Flyspeed());
        getCommand("getlocation").setExecutor(new GetLocation());
        getCommand("rnrp").setExecutor(new RNRPChat());
        getCommand("duty").setExecutor(new Duty());
        getCommand("msg").setExecutor(new MSG());
        getCommand("getgun").setExecutor(new GetGun());
        getCommand("getammo").setExecutor(new GetAmmo());
        getCommand("checkgun").setExecutor(new CheckGun());
        getCommand("achievements").setExecutor(new AchievementCommand());
        getCommand("setteamleader").setExecutor(new SetTeamLeader());
        getCommand("removeteamleader").setExecutor(new RemoveTeamLeader());
        getCommand("forcelotto").setExecutor(new ForceLotto());
        getCommand("whitelistip").setExecutor(new WhitelistIP());
        getCommand("equip").setExecutor(new Equip());
        getCommand("uncuff").setExecutor(new Uncuff());
        getCommand("spenden").setExecutor(new SpendenCommand());
        getCommand("staatsmeldung").setExecutor(new Staatsmeldung());
        getCommand("addhousedoor").setExecutor(new AddHouseDoor());
        getCommand("back").setExecutor(new BackCommand());
        getCommand("baulog").setExecutor(new Baulog());
        getCommand("tragen").setExecutor(new Tragen());
        getCommand("removetragensperre").setExecutor(new TragenSperre());
        getCommand("sperrinfo").setExecutor(new Sperrinfo());
        getCommand("straftat").setExecutor(new Straftat());
        getCommand("deletestraftat").setExecutor(new DeleteStraftat());
        getCommand("wanted").setExecutor(new Fahndung());
        getCommand("removefahndung").setExecutor(new RemoveFahndung());
        getCommand("arrest").setExecutor(new Arrest());
        getCommand("unarrest").setExecutor(new Unarrest());
        getCommand("jaillist").setExecutor(new JailList());
        getCommand("jailtime").setExecutor(new JailTime());
        getCommand("staatschat").setExecutor(new StaatsChat());
        getCommand("teamspeak").setExecutor(new TeamspeakCommand());
        getCommand("channel").setExecutor(new PremiumChannel());
        getCommand("takeshop").setExecutor(new TakeShop());
        getCommand("notruf").setExecutor(new Notruf());
        getCommand("acceptnotruf").setExecutor(new AcceptNotruf());
        getCommand("donenotruf").setExecutor(new DoneNotruf());
        getCommand("cancelnotruf").setExecutor(new CancelNotruf());
        getCommand("sharenotruf").setExecutor(new ShareNotruf());
        getCommand("personalausweis").setExecutor(new Personalausweis());
        getCommand("showperso").setExecutor(new ShowPerso());
        getCommand("showfinances").setExecutor(new ShowFinances());
        getCommand("showlicenses").setExecutor(new ShowLicenses());
        getCommand("policecomputer").setExecutor(new Policecomputer());
        getCommand("dangerlevel").setExecutor(new DangerLevel());
        getCommand("gmx").setExecutor(new GMX());
        getCommand("signedit").setExecutor(new SignEdit());
        getCommand("blockcommand").setExecutor(new BlockCommand());
        getCommand("tipp").setExecutor(new TippOfTheDay());
        getCommand("zeitung").setExecutor(new Zeitung());
        getCommand("aab").setExecutor(new AimBot());
        getCommand("requeuenotruf").setExecutor(new RequeueNotruf());
        getCommand("ramm").setExecutor(new Ramm());

        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new SDuty(), this);
        pm.registerEvents(new BlackJack(), this);
        pm.registerEvents(new BuildMode(), this);
        pm.registerEvents(new Chat(), this);
        pm.registerEvents(new Tazer(), this);
        pm.registerEvents(new Utils(), this);
        pm.registerEvents(new Test(), this);
        pm.registerEvents(new WeatherControl(), this);
        pm.registerEvents(new Elevator(), this);
        pm.registerEvents(new ElevatorDoor(), this);
        pm.registerEvents(new AFK(), this);
        pm.registerEvents(new Punish(), this);
        pm.registerEvents(new HungerFix(), this);
        pm.registerEvents(new Notications(), this);
        pm.registerEvents(new Banken(), this);
        pm.registerEvents(new JoinTeam(), this);
        pm.registerEvents(new NaviClick(), this);
        pm.registerEvents(new RouteListener(), this);
        pm.registerEvents(new TicketClick(), this);
        pm.registerEvents(new TicketListener(), this);
        pm.registerEvents(new JoinBeruf(), this);
        pm.registerEvents(new Chair(), this);
        pm.registerEvents(new Wahlen(), this);
        pm.registerEvents(new Laufband(), this);
        pm.registerEvents(new Passwort(), this);
        pm.registerEvents(new BuyClick(), this);
        pm.registerEvents(new PayShop(), this);
        pm.registerEvents(new Shop(), this);
        pm.registerEvents(new HologramClick(), this);
        pm.registerEvents(new HouseRegister(), this);
        pm.registerEvents(new HouseOpen(), this);
        pm.registerEvents(new AddBerufsDoor(), this);
        pm.registerEvents(new FriedhofListener(), this);
        pm.registerEvents(new Spectate(), this);
        pm.registerEvents(new GetLocation(), this);
        pm.registerEvents(new Waffen(), this);
        pm.registerEvents(new WaffenDamage(), this);
        pm.registerEvents(new GetGun(), this);
        pm.registerEvents(new AntiCheatSystem(), this);
        pm.registerEvents(new AntiCheatFly(), this);
        pm.registerEvents(new AntiOfflineFlucht(), this);
        pm.registerEvents(new Spawnschutz(), this);
        pm.registerEvents(new Equip(), this);
        pm.registerEvents(new Handschellen(), this);
        pm.registerEvents(new Flashbang(), this);
        pm.registerEvents(new Rauchgranate(), this);
        pm.registerEvents(new WingsuitListener(), this);
        pm.registerEvents(new AntiVPN(), this);
        pm.registerEvents(new JailTime(), this);
        pm.registerEvents(new TeamspeakUpdate(), this);
        pm.registerEvents(new Notruf(), this);
        pm.registerEvents(new AcceptNotruf(), this);
        pm.registerEvents(new Personalausweis(), this);
        pm.registerEvents(new Policecomputer(), this);
        pm.registerEvents(new BlockCommand(), this);
        pm.registerEvents(new Zeitung(), this);

        new PayDay().runTaskTimerAsynchronously(this, 60 * 20L, 60 * 20L);
        new AsyncMinute().runTaskTimerAsynchronously(this, 60 * 20L, 60 * 20L);
        new AsyncHour().runTaskTimerAsynchronously(this, 60 * 60 * 20L, 60 * 60 * 20L);
        new SyncHour().runTaskTimer(this, 60 * 60 * 20L, 60 * 60 * 20L);
        new AsyncDaylightCycle().runTaskTimer(this, 20L, 600L);
        new SyncMinute().runTaskTimer(this, 60 * 20L, 60 * 20L);

        ScoreboardManager.initMainScoreboard();
        Hologram.reload();
        ATM.restore();
        House.loadHouses();


        Bukkit.getConsoleSender().sendMessage("§cNRP §8× §aloading complete..");
        Bukkit.getConsoleSender().sendMessage("§cNRP §8× §astarting complete..");
        Bukkit.getConsoleSender().sendMessage("§cNRP §8× §aViel Erfolg heute..");
    }

}

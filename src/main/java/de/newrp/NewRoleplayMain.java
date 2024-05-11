package de.newrp;

import de.newrp.API.*;
import de.newrp.Administrator.*;
import de.newrp.Berufe.*;
import de.newrp.Call.CallCommand;
import de.newrp.Call.HangupCommand;
import de.newrp.Call.PickupCommand;
import de.newrp.Chat.*;
import de.newrp.Commands.DiscordCommand;
import de.newrp.Commands.Test;
import de.newrp.Entertainment.*;
import de.newrp.Forum.ForumCommand;
import de.newrp.GFB.*;
import de.newrp.Gangwar.Capture;
import de.newrp.Gangwar.GangwarCommand;
import de.newrp.Gangwar.GangwarZonesCommand;
import de.newrp.Government.*;
import de.newrp.House.*;
import de.newrp.Medic.*;
import de.newrp.News.*;
import de.newrp.Organisationen.*;
import de.newrp.Player.*;
import de.newrp.Police.*;
import de.newrp.Runnable.*;
import de.newrp.Shop.*;
import de.newrp.TeamSpeak.PremiumChannel;
import de.newrp.TeamSpeak.TeamSpeak;
import de.newrp.TeamSpeak.TeamspeakCommand;
import de.newrp.TeamSpeak.TeamspeakUpdate;
import de.newrp.Ticket.*;
import de.newrp.Vehicle.*;
import de.newrp.Votifier.VoteCommand;
import de.newrp.Votifier.VoteListener;
import de.newrp.Votifier.VoteShop;
import de.newrp.Votifier.VoteShopListener;
import de.newrp.Waffen.*;
import de.newrp.config.IConfigService;
import de.newrp.config.MainConfig;
import de.newrp.config.impl.ConfigService;
import de.newrp.dependencies.DependencyContainer;
import de.newrp.discord.IJdaService;
import de.newrp.discord.events.GuildReadyListener;
import de.newrp.discord.impl.JdaService;
import de.newrp.discord.listeners.VerifyListener;
import de.newrp.features.deathmatcharena.IDeathmatchArenaService;
import de.newrp.features.deathmatcharena.commands.DeathmatchArenaCommand;
import de.newrp.features.deathmatcharena.data.DeathmatchArenaConfig;
import de.newrp.features.deathmatcharena.impl.DeathmatchArenaService;
import de.newrp.features.deathmatcharena.listener.DeathmatchQuitListener;
import de.newrp.features.deathmatcharena.listener.DeathmatchRespawnListener;
import de.newrp.features.emergencycall.IEmergencyCallService;
import de.newrp.features.emergencycall.commands.*;
import de.newrp.features.emergencycall.impl.EmergencyCallService;
import de.newrp.features.emergencycall.listener.EmergencyCallInventoryListener;
import de.newrp.features.emergencycall.listener.EmergencyCallQuitListener;
import de.newrp.features.roadblocks.IFactionBlockService;
import de.newrp.features.roadblocks.commands.RoadBlockCommand;
import de.newrp.features.roadblocks.commands.SprungtuchCommand;
import de.newrp.features.roadblocks.impl.FactionBlockService;
import de.newrp.features.roadblocks.listener.FactionBlockClickListener;
import de.newrp.features.roadblocks.listener.FactionBlockDropItemListener;
import de.newrp.features.roadblocks.listener.FactionBlockQuitListener;
import net.citizensnpcs.api.CitizensAPI;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.labymod.serverapi.api.LabyAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.Connection;

public class NewRoleplayMain extends JavaPlugin {

    private static Plugin instance;
    private static Connection mainConnection;
    private static Connection forumConnection;

    public static Event event;

    private IConfigService configService;
    private MainConfig mainConfig;

    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage("§cNRP §8× §aStarting with version " + this.getDescription().getVersion() + "..");

        this.configService = new ConfigService();

        // Loads all configurations
        this.loadConfig();

        // Register all dependencies
        this.registerAllDependencies();

        NewRoleplayMain.instance = this;
        NewRoleplayMain.event = null;

        try {
            final MySQL database = new MySQL(this.mainConfig.getMainConnection().getHostname(), this.mainConfig.getMainConnection().getPort(), this.mainConfig.getMainConnection().getDatabase(), this.mainConfig.getMainConnection().getUsername(), this.mainConfig.getMainConnection().getPassword());
            NewRoleplayMain.mainConnection = database.openConnection();
            Bukkit.getConsoleSender().sendMessage("§cNRP §8× §aVerbindung zur Datenbank hergestellt.");
        } catch (Exception e1) {
            Bukkit.getConsoleSender().sendMessage("§cNRP §8× §cVerbindung zur Datenbank konnte nicht hergestellt werden.");
            Bukkit.getConsoleSender().sendMessage("§cNRP §8× §cPlugin wird gestoppt..");
            Bukkit.getConsoleSender().sendMessage("§cNRP §8× §cFehler: " + e1.getMessage());
            Bukkit.getConsoleSender().sendMessage("§cNRP §8× §cFahre Server herunter..");
            this.getServer().shutdown();
        }

        try {
            MySQL forumDatabase = new MySQL(this.mainConfig.getForumConnection().getHostname(), this.mainConfig.getForumConnection().getPort(), this.mainConfig.getForumConnection().getDatabase(), this.mainConfig.getForumConnection().getUsername(), this.mainConfig.getForumConnection().getPassword());
            NewRoleplayMain.forumConnection = forumDatabase.openConnection();
            Bukkit.getConsoleSender().sendMessage("§cNRP §8× §aVerbindung zur Forum-Datenbank hergestellt.");
        } catch (Exception e1) {
            Bukkit.getConsoleSender().sendMessage("§cNRP §8× §cVerbindung zur Forum-Datenbank konnte nicht hergestellt werden.");
            Bukkit.getConsoleSender().sendMessage("§cNRP §8× §cPlugin wird gestoppt..");
            Bukkit.getConsoleSender().sendMessage("§cNRP §8× §cFehler: " + e1.getMessage());
            Bukkit.getConsoleSender().sendMessage("§cNRP §8× §cFahre Server herunter..");
            this.getServer().shutdown();
        }

        if (!isTest()) {
            try {
                TeamSpeak.connect();
                Bukkit.getConsoleSender().sendMessage("§cNRP §8× §aTeamspeak Verbindung hergestellt.");
            } catch (Exception e) {
                e.printStackTrace();
            }

            new ClearLog();
        }

        // Prepare scoreboard teams
        Script.prepareScoreboard();
        
        // Register all commands
        this.registerAllCommands();

        // Register all listeners
        this.registerAllListeners();

        new AsyncHealth().runTaskTimerAsynchronously(this, 120 * 20L, 120 * 20L);
        new PayDay().runTaskTimerAsynchronously(this, 60 * 20L, 60 * 20L);
        new AsyncMinute().runTaskTimerAsynchronously(this, 60 * 20L, 60 * 20L);
        new AsyncHour().runTaskTimerAsynchronously(this, 60 * 60 * 20L, 60 * 60 * 20L);
        new SyncHour().runTaskTimer(this, 60 * 60 * 20L, 60 * 60 * 20L);
        new AsyncDaylightCycle().runTaskTimer(this, 20L, 600L);
        new SyncMinute().runTaskTimer(this, 60 * 20L, 60 * 20L);
        new AsyncPlantation().runTaskTimerAsynchronously(this, 60 * 20L, 60 * 20L);
        new Sync15Sek().runTaskTimer(this, 15 * 20L, 15 * 20L);
        new Async2Min().runTaskTimer(this, 120 * 20L, 120 * 20L);

        ScoreboardManager.initMainScoreboard();
        Hologram.reload();
        ATM.restore();
        House.loadHouses();
        Blacklist.load();
        Plantage.loadAll();
        Bukkit.getScheduler().runTaskLater(this, CitizensAPI.getNPCRegistry()::deregisterAll, 2L);
        Bukkit.getScheduler().runTaskLater(this, Schwarzmarkt::spawnRandom, 4L);
        Zeitung.restoreZeitung();
        OrgSpray.FraktionSpray.init();

        LabyAPI.initialize(LabyAPI.getService());

        final IJdaService jdaService = DependencyContainer.getContainer().getDependency(IJdaService.class);
        final JDA jda = jdaService.createJDAInstance(this.mainConfig.getJdaBotToken());

        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(this.mainConfig.getJdaBotToken());
        builder.setActivity(Activity.playing("NRP × New Roleplay"));

        builder.addEventListeners(new VerifyListener());
        builder.addEventListeners(new GuildReadyListener());
        builder.build();

        Bukkit.getConsoleSender().sendMessage("§cNRP §8× §astarting complete..");
        Bukkit.getConsoleSender().sendMessage("§cNRP §8× §aViel Erfolg heute..");
    }

    /***
     * Method to register all commands, soon to be fully automatic
     */
    @SuppressWarnings("DataFlowIssue")
    private void registerAllCommands() {

        // To-Do: Use command map to register commands, big refactoring inc
        final CommandMap commandMap = ((CraftServer) this.getServer()).getCommandMap();
        
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
        getCommand("notifications").setExecutor(new Notifications());
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
        getCommand("acceptnotruf").setExecutor(new AcceptEmergencyCallCommand());
        getCommand("donenotruf").setExecutor(new DoneEmergencyCallCommand());
        getCommand("cancelnotruf").setExecutor(new CancelEmergencyCallCommand());
        getCommand("sharenotruf").setExecutor(new ShareEmergencyCallCommand());
        getCommand("personalausweis").setExecutor(new Personalausweis());
        getCommand("policecomputer").setExecutor(new Policecomputer());
        getCommand("dangerlevel").setExecutor(new DangerLevel());
        getCommand("gmx").setExecutor(new GMX());
        getCommand("signedit").setExecutor(new SignEdit());
        getCommand("blockcommand").setExecutor(new BlockCommand());
        getCommand("tipp").setExecutor(new TippOfTheDay());
        getCommand("zeitung").setExecutor(new Zeitung());
        getCommand("aab").setExecutor(new AimBot());
        getCommand("requeuenotruf").setExecutor(new RequeueEmergencyCallCommand());
        getCommand("ramm").setExecutor(new Ramm());
        getCommand("transferticket").setExecutor(new TransferTicket());
        getCommand("health").setExecutor(new HealthCommand());
        getCommand("notrufe").setExecutor(new EmergencyCallsCommand());
        getCommand("dropgun").setExecutor(new DropGuns());
        getCommand("rezept").setExecutor(new Rezept());
        getCommand("selfstorage").setExecutor(new Selfstorage());
        getCommand("removeselfstorage").setExecutor(new RemoveSelfstorage());
        getCommand("checkselfstorage").setExecutor(new CheckSelfstorage());
        getCommand("removecheckpoints").setExecutor(new RemoveCheckpoints());
        getCommand("vertrag").setExecutor(new Vertrag());
        getCommand("vertraglist").setExecutor(new Vertraege());
        getCommand("showvertrag").setExecutor(new ShowVertrag());
        getCommand("erstattung").setExecutor(new Erstattung());
        getCommand("reinforcement").setExecutor(new Reinforcement());
        getCommand("premium").setExecutor(new PremiumCommand());
        getCommand("call").setExecutor(new CallCommand());
        getCommand("hangup").setExecutor(new HangupCommand());
        getCommand("pickup").setExecutor(new PickupCommand());
        getCommand("registerplayer").setExecutor(new RegisterPlayer());
        getCommand("getshulker").setExecutor(new GetShulker());
        getCommand("sms").setExecutor(new SMSCommand());
        getCommand("breakingnews").setExecutor(new BreakingNews());
        getCommand("akku").setExecutor(new AkkuCommand());
        getCommand("removestadtkasse").setExecutor(new RemoveStadtkasse());
        getCommand("houseban").setExecutor(new Houseban());
        getCommand("aktien").setExecutor(new AktienMarkt());
        getCommand("forceaktien").setExecutor(new ForceAktien());
        getCommand("backupcode").setExecutor(new BackupCode());
        getCommand("transferaccount").setExecutor(new TransferAccount());
        getCommand("blacklist").setExecutor(new BlackListCommand());
        getCommand("o").setExecutor(new OrganisationsChat());
        getCommand("joinorganisation").setExecutor(new JoinOrganisation());
        getCommand("setrank").setExecutor(new SetRank());
        getCommand("rankname").setExecutor(new Rankname());
        getCommand("checkorganisation").setExecutor(new CheckOrganisation());
        getCommand("plantage").setExecutor(new PlantageCommand());
        getCommand("burnplant").setExecutor(new BurnPlant());
        getCommand("organisationkasse").setExecutor(new OrganisationKasse());
        getCommand("schwarzmarktlocation").setExecutor(new SchwarzmarktLocation());
        getCommand("takeguns").setExecutor(new TakeGuns());
        getCommand("takedrugs").setExecutor(new TakeDrugs());
        getCommand("lagerarbeiter").setExecutor(new Lagerarbeiter());
        getCommand("schule").setExecutor(new Schule());
        getCommand("quitjob").setExecutor(new Quitjob());
        getCommand("kellner").setExecutor(new Kellner());
        getCommand("transport").setExecutor(new Transport());
        getCommand("resetschwarzmarkt").setExecutor(new ResetSchwarzmarkt());
        getCommand("resethologram").setExecutor(new ResetHologram());
        getCommand("vote").setExecutor(new VoteCommand());
        getCommand("voteshop").setExecutor(new VoteShop());
        getCommand("eishalle").setExecutor(new Eishalle());
        getCommand("pizzalieferant").setExecutor(new Pizza());
        getCommand("dishwasher").setExecutor(new Dishwasher());
        getCommand("burgerbrater").setExecutor(new BurgerFryer());
        getCommand("strassenwartung").setExecutor(new Strassenwartung());
        getCommand("imker").setExecutor(new Imker());
        getCommand("steuernotification").setExecutor(new SteuerNotification());
        getCommand("erstehilfekurs").setExecutor(new ErsteHilfeSchein());
        getCommand("erstehilfe").setExecutor(new ErsteHilfe());
        getCommand("sellhouse").setExecutor(new SellHouse());
        getCommand("leaderchat").setExecutor(new LeaderChat());
        getCommand("waffenschein").setExecutor(new Waffenschein());
        getCommand("addpremiumtoplayer").setExecutor(new AddPremiumToPlayer());
        getCommand("dropammo").setExecutor(new DropAmmo());
        getCommand("sharelocation").setExecutor(new ShareLocation());
        getCommand("gfblevel").setExecutor(new GFBLevel());
        getCommand("equiplog").setExecutor(new Equiplog());
        getCommand("use").setExecutor(new UseDrogenCommand());
        getCommand("team").setExecutor(new ServerTeam());
        getCommand("memberactivity").setExecutor(new MemberActivity());
        getCommand("flugblatt").setExecutor(new Flugblatt());
        getCommand("togglewhisper").setExecutor(new ToggleWhisper());
        getCommand("umfrage").setExecutor(new UmfragenCommand());
        getCommand("spawnchange").setExecutor(new Spawnchange());
        getCommand("boot").setExecutor(new Boot());
        getCommand("timer").setExecutor(new Timer());
        getCommand("calculator").setExecutor(new Calculator());
        getCommand("todo").setExecutor(new ToDoCommand());
        getCommand("jailwork").setExecutor(new JailWork());
        getCommand("takewaffenschein").setExecutor(new TakeWaffenschein());
        getCommand("takedriverslicense").setExecutor(new TakeDriversLicense());
        getCommand("partikel").setExecutor(new ParticleCommand());
        getCommand("chatclear").setExecutor(new ChatClear());
        getCommand("anrufbeantworter").setExecutor(new Anrufbeantworter());
        getCommand("kamera").setExecutor(new KameraCommand());
        getCommand("tv").setExecutor(new TV());
        getCommand("slap").setExecutor(new SlapCommand());
        getCommand("raffle").setExecutor(new RaffleCommand());
        getCommand("sellfisch").setExecutor(new Sellfisch());
        getCommand("spawncar").setExecutor(new SpawnCar());
        getCommand("dice").setExecutor(new Dice());
        getCommand("flipcoin").setExecutor(new Flipcoin());
        getCommand("registerbanner").setExecutor(new RegisterBanner());
        getCommand("startevent").setExecutor(new StartEventCommand());
        getCommand("setbargeld").setExecutor(new SetBargeld());
        getCommand("stopevent").setExecutor(new StopEventCommand());
        getCommand("beziehung").setExecutor(new BeziehungCommand());
        getCommand("marry").setExecutor(new Marry());
        getCommand("trennen").setExecutor(new Trennen());
        getCommand("drogenbank").setExecutor(new Drogenbank());
        getCommand("addorgdoor").setExecutor(new AddOrgDoor());
        getCommand("destroykoms").setExecutor(new DestroyKoms());
        getCommand("teamactivity").setExecutor(new TeamActivity());
        getCommand("bussgeld").setExecutor(new Bussgeld());
        getCommand("bankraub").setExecutor(new Bankraub());
        getCommand("strafregister").setExecutor(new Strafregister());
        getCommand("gangwar").setExecutor(new GangwarCommand());
        getCommand("loan").setExecutor(new Loan());
        getCommand("reply").setExecutor(new Reply());
        getCommand("takemoney").setExecutor(new TakeMoney());
        getCommand("mixingredients").setExecutor(new LabBreakIn());
        getCommand("hackpolicecomputer").setExecutor(new HackPoliceComputer());
        getCommand("chatcolor").setExecutor(new FrakChatColor());
        getCommand("motd").setExecutor(new FMOTD());
        getCommand("capture").setExecutor(new Capture());
        getCommand("kebap").setExecutor(new Kebap());
        getCommand("blocknotruf").setExecutor(new BlockEmergencyCallCommand());
        getCommand("gangzone").setExecutor(new GangwarZonesCommand());
        getCommand("deletenotruf").setExecutor(new DeleteEmergencyCallCommand());
        getCommand("checkactivemembers").setExecutor(new CheckActiveMembers());
        getCommand("endfire").setExecutor(new EndFire());
        getCommand("sit").setExecutor(new SitCommand());
        getCommand("housedbank").setExecutor(new DrogenbankHouse());
        getCommand("strecken").setExecutor(new Strecken());
        getCommand("treuebonus").setExecutor(new Treuebonus());
        getCommand("addticket").setExecutor(new AddToConv());
        getCommand("tabakplantage").setExecutor(new Tabakplantage());
        getCommand("droptabak").setExecutor(new DropTabak());
        getCommand("neulingschat").setExecutor(new NeulingsChat());
        getCommand("apikey").setExecutor(new APIKey());
        getCommand("houseslot").setExecutor(new HouseSlot());
        getCommand("molotov").setExecutor(new MolotovCocktail());
        getCommand("leitungswasser").setExecutor(new Leitungswasser());
        getCommand("car").setExecutor(new CarCommand());
        getCommand("dm").setExecutor(new DeathmatchArenaCommand());
        getCommand("car").setExecutor(new CarCommand());
        getCommand("tanken").setExecutor(new TankenCommand());
        getCommand("kennzeichen").setExecutor(new Kennzeichen());
        getCommand("fahrschule").setExecutor(new Fahrschule());
        getCommand("roadblock").setExecutor(new RoadBlockCommand());
        getCommand("sprungtuch").setExecutor(new SprungtuchCommand());
        getCommand("leasing").setExecutor(new LeasingCommand());
        getCommand("breakin").setExecutor(new BreakinCommand());
        getCommand("rob").setExecutor(new RobCommand());
        getCommand("checkkfz").setExecutor(new CheckKFZ());
        getCommand("strafzettel").setExecutor(new StrafzettelCommand());
        getCommand("payticket").setExecutor(new PayTicket());
        getCommand("sql").setExecutor(new SQLCommand());
        getCommand("checkhealth").setExecutor(new CheckHealthCommand());
        getCommand("starttransport").setExecutor(new StartTransport());
        getCommand("vehicleslot").setExecutor(new VehicleSlotsCommand());
    }

    /**
     * Registers all listeners, soon to be fully automatic
     */
    private void registerAllListeners() {
        Bukkit.getPluginManager().registerEvents(new SDuty(), this);
        Bukkit.getPluginManager().registerEvents(new BlackJack(), this);
        Bukkit.getPluginManager().registerEvents(new BuildMode(), this);
        Bukkit.getPluginManager().registerEvents(new Chat(), this);
        Bukkit.getPluginManager().registerEvents(new Tazer(), this);
        Bukkit.getPluginManager().registerEvents(new Utils(), this);
        Bukkit.getPluginManager().registerEvents(new Test(), this);
        Bukkit.getPluginManager().registerEvents(new WeatherControl(), this);
        Bukkit.getPluginManager().registerEvents(new Elevator(), this);
        Bukkit.getPluginManager().registerEvents(new ElevatorDoor(), this);
        Bukkit.getPluginManager().registerEvents(new AFK(), this);
        Bukkit.getPluginManager().registerEvents(new Punish(), this);
        Bukkit.getPluginManager().registerEvents(new HungerFix(), this);
        Bukkit.getPluginManager().registerEvents(new Notifications(), this);
        Bukkit.getPluginManager().registerEvents(new Banken(), this);
        Bukkit.getPluginManager().registerEvents(new JoinTeam(), this);
        Bukkit.getPluginManager().registerEvents(new NaviClick(), this);
        Bukkit.getPluginManager().registerEvents(new RouteListener(), this);
        Bukkit.getPluginManager().registerEvents(new TicketClick(), this);
        Bukkit.getPluginManager().registerEvents(new TicketListener(), this);
        Bukkit.getPluginManager().registerEvents(new JoinBeruf(), this);
        Bukkit.getPluginManager().registerEvents(new Chair(), this);
        Bukkit.getPluginManager().registerEvents(new Wahlen(), this);
        Bukkit.getPluginManager().registerEvents(new Laufband(), this);
        Bukkit.getPluginManager().registerEvents(new Passwort(), this);
        Bukkit.getPluginManager().registerEvents(new BuyClick(), this);
        Bukkit.getPluginManager().registerEvents(new PayShop(), this);
        Bukkit.getPluginManager().registerEvents(new Shop(), this);
        Bukkit.getPluginManager().registerEvents(new HologramClick(), this);
        Bukkit.getPluginManager().registerEvents(new HouseRegister(), this);
        Bukkit.getPluginManager().registerEvents(new HouseOpen(), this);
        Bukkit.getPluginManager().registerEvents(new AddBerufsDoor(), this);
        Bukkit.getPluginManager().registerEvents(new FriedhofListener(), this);
        Bukkit.getPluginManager().registerEvents(new Spectate(), this);
        Bukkit.getPluginManager().registerEvents(new GetLocation(), this);
        Bukkit.getPluginManager().registerEvents(new Waffen(), this);
        Bukkit.getPluginManager().registerEvents(new WaffenDamage(), this);
        Bukkit.getPluginManager().registerEvents(new GetGun(), this);
        Bukkit.getPluginManager().registerEvents(new AntiCheatSystem(), this);
        Bukkit.getPluginManager().registerEvents(new AntiOfflineFlucht(), this);
        Bukkit.getPluginManager().registerEvents(new Spawnschutz(), this);
        Bukkit.getPluginManager().registerEvents(new Equip(), this);
        Bukkit.getPluginManager().registerEvents(new Handschellen(), this);
        Bukkit.getPluginManager().registerEvents(new Flashbang(), this);
        Bukkit.getPluginManager().registerEvents(new Rauchgranate(), this);
        Bukkit.getPluginManager().registerEvents(new WingsuitListener(), this);
        Bukkit.getPluginManager().registerEvents(new AntiVPN(), this);
        Bukkit.getPluginManager().registerEvents(new JailTime(), this);
        if(!isTest()) {
            Bukkit.getPluginManager().registerEvents(new TeamspeakUpdate(), this);
        }
        Bukkit.getPluginManager().registerEvents(new Personalausweis(), this);
        Bukkit.getPluginManager().registerEvents(new Policecomputer(), this);
        Bukkit.getPluginManager().registerEvents(new BlockCommand(), this);
        Bukkit.getPluginManager().registerEvents(new Zeitung(), this);
        Bukkit.getPluginManager().registerEvents(new Verband(), this);
        Bukkit.getPluginManager().registerEvents(new Gips(), this);
        Bukkit.getPluginManager().registerEvents(new HealthCommand(), this);
        Bukkit.getPluginManager().registerEvents(new EatEvent(), this);
        Bukkit.getPluginManager().registerEvents(new UseMedikamente(), this);
        Bukkit.getPluginManager().registerEvents(new Selfstorage(), this);
        Bukkit.getPluginManager().registerEvents(new Baseballschlaeger(), this);
        Bukkit.getPluginManager().registerEvents(new Checkpoints(), this);
        Bukkit.getPluginManager().registerEvents(new Vertraege(), this);
        Bukkit.getPluginManager().registerEvents(new GetShulker(), this);
        Bukkit.getPluginManager().registerEvents(new CallCommand(), this);
        Bukkit.getPluginManager().registerEvents(new Mobile(), this);
        Bukkit.getPluginManager().registerEvents(new Krankheitstest(), this);
        Bukkit.getPluginManager().registerEvents(new Impfen(), this);
        Bukkit.getPluginManager().registerEvents(new AktienMarkt(), this);
        Bukkit.getPluginManager().registerEvents(new Tragen(), this);
        Bukkit.getPluginManager().registerEvents(new BlackListCommand(), this);
        Bukkit.getPluginManager().registerEvents(new JoinOrganisation(), this);
        Bukkit.getPluginManager().registerEvents(new PlantageCommand(), this);
        Bukkit.getPluginManager().registerEvents(new UseDrogen(), this);
        Bukkit.getPluginManager().registerEvents(new BreakIn(), this);
        Bukkit.getPluginManager().registerEvents(new SchwarzmarktListener(), this);
        Bukkit.getPluginManager().registerEvents(new Lagerarbeiter(), this);
        Bukkit.getPluginManager().registerEvents(new Schule(), this);
        Bukkit.getPluginManager().registerEvents(new Kellner(), this);
        Bukkit.getPluginManager().registerEvents(new Transport(), this);
        Bukkit.getPluginManager().registerEvents(new VoteListener(), this);
        Bukkit.getPluginManager().registerEvents(new VoteShopListener(), this);
        Bukkit.getPluginManager().registerEvents(new Eishalle(), this);
        Bukkit.getPluginManager().registerEvents(new Pizza(), this);
        Bukkit.getPluginManager().registerEvents(new Dishwasher(), this);
        Bukkit.getPluginManager().registerEvents(new BurgerFryer(), this);
        Bukkit.getPluginManager().registerEvents(new InteractMenu(), this);
        Bukkit.getPluginManager().registerEvents(new Strassenwartung(), this);
        Bukkit.getPluginManager().registerEvents(new Imker(), this);
        Bukkit.getPluginManager().registerEvents(new AddHouseDoor(), this);
        Bukkit.getPluginManager().registerEvents(new AchievementCommand(), this);
        Bukkit.getPluginManager().registerEvents(new ErsteHilfe(), this);
        Bukkit.getPluginManager().registerEvents(new HouseListener(), this);
        Bukkit.getPluginManager().registerEvents(new GFBLevel(), this);
        Bukkit.getPluginManager().registerEvents(new Pfandautomat(), this);
        Bukkit.getPluginManager().registerEvents(new Fesseln(), this);
        Bukkit.getPluginManager().registerEvents(new SniperZoom(), this);
        Bukkit.getPluginManager().registerEvents(new Boxen(), this);
        Bukkit.getPluginManager().registerEvents(new Drone(), this);
        Bukkit.getPluginManager().registerEvents(new Flugblatt(), this);
        Bukkit.getPluginManager().registerEvents(new UmfragenCommand(), this);
        Bukkit.getPluginManager().registerEvents(new Spawnchange(), this);
        Bukkit.getPluginManager().registerEvents(new JailWork(), this);
        Bukkit.getPluginManager().registerEvents(new ParticleCommand(), this);
        Bukkit.getPluginManager().registerEvents(new KameraCommand(), this);
        Bukkit.getPluginManager().registerEvents(new TV(), this);
        Bukkit.getPluginManager().registerEvents(new TestoSpritze(), this);
        Bukkit.getPluginManager().registerEvents(new RegisterBanner(), this);
        Bukkit.getPluginManager().registerEvents(new OrgSpray(), this);
        Bukkit.getPluginManager().registerEvents(new StartEventCommand(), this);
        Bukkit.getPluginManager().registerEvents(new AntiLeftHand(), this);
        Bukkit.getPluginManager().registerEvents(new Drogenbank(), this);
        Bukkit.getPluginManager().registerEvents(new AddOrgDoor(), this);
        Bukkit.getPluginManager().registerEvents(new Bankautomaten(), this);
        Bukkit.getPluginManager().registerEvents(new Bankraub(), this);
        Bukkit.getPluginManager().registerEvents(new LabBreakIn(), this);
        Bukkit.getPluginManager().registerEvents(new HackPoliceComputer(), this);
        Bukkit.getPluginManager().registerEvents(new FrakChatColor(), this);
        Bukkit.getPluginManager().registerEvents(new GangwarCommand(), this);
        Bukkit.getPluginManager().registerEvents(new Capture(), this);
        Bukkit.getPluginManager().registerEvents(new Kebap(), this);
        Bukkit.getPluginManager().registerEvents(new AntiFireSpread(), this);
        Bukkit.getPluginManager().registerEvents(new Feuerloescher(), this);
        Bukkit.getPluginManager().registerEvents(new AkkuCommand(), this);
        Bukkit.getPluginManager().registerEvents(new Treuebonus(), this);
        Bukkit.getPluginManager().registerEvents(new Shisha(), this);
        Bukkit.getPluginManager().registerEvents(new CancelTicket(), this);
        Bukkit.getPluginManager().registerEvents(new Trash(), this);
        Bukkit.getPluginManager().registerEvents(new CarHandler(), this);
        Bukkit.getPluginManager().registerEvents(new CarListener(), this);
        Bukkit.getPluginManager().registerEvents(new EmergencyCallInventoryListener(), this);
        Bukkit.getPluginManager().registerEvents(new EmergencyCallQuitListener(), this);
        Bukkit.getPluginManager().registerEvents(new DeathmatchRespawnListener(), this);
        Bukkit.getPluginManager().registerEvents(new DeathmatchQuitListener(), this);
        Bukkit.getPluginManager().registerEvents(new Kennzeichen(), this);
        Bukkit.getPluginManager().registerEvents(new Fahrschule(), this);
        Bukkit.getPluginManager().registerEvents(new FactionBlockClickListener(), this);
        Bukkit.getPluginManager().registerEvents(new FactionBlockDropItemListener(), this);
        Bukkit.getPluginManager().registerEvents(new FactionBlockQuitListener(), this);
        Bukkit.getPluginManager().registerEvents(new LockpickHandler(), this);
        Bukkit.getPluginManager().registerEvents(new StartTransport(), this);
        Bukkit.getPluginManager().registerEvents(new Tabakplantage(), this);
    }

    /**
     * Registers all dependencies
     */
    private void registerAllDependencies() {
        DependencyContainer.getContainer().add(NewRoleplayMain.class, this);
        DependencyContainer.getContainer().add(IConfigService.class, this.configService);
        DependencyContainer.getContainer().add(MainConfig.class, this.mainConfig);
        DependencyContainer.getContainer().add(IEmergencyCallService.class, new EmergencyCallService());
        DependencyContainer.getContainer().add(IJdaService.class, new JdaService());
        DependencyContainer.getContainer().add(IDeathmatchArenaService.class, new DeathmatchArenaService());
        DependencyContainer.getContainer().add(IFactionBlockService.class, new FactionBlockService());
    }

    /**
     * Loads all configuration files
     */
    private void loadConfig() {
        final File pluginFolder = new File("plugins/NewRoleplay");
        if(!pluginFolder.exists()) {
            pluginFolder.mkdir();
        }

        final File mainConfigFile = new File(pluginFolder, "config.json");
        this.configService.saveConfig(mainConfigFile, new MainConfig(), false);
        this.mainConfig = this.configService.readConfig(mainConfigFile, MainConfig.class);
        Bukkit.getLogger().info("Successfully read Main Configuration!");

        final File dmConfigFile = new File(pluginFolder, "deathmatch.json");
        this.configService.saveConfig(dmConfigFile, new DeathmatchArenaConfig(), false);
        DependencyContainer.getContainer().add(DeathmatchArenaConfig.class, this.configService.readConfig(dmConfigFile, DeathmatchArenaConfig.class));
        Bukkit.getLogger().info("Successfully read Deathmatch Configuration!");
    }

    public static boolean isTest() {
        return Bukkit.hasWhitelist();
    }

    public static Plugin getInstance() {
        return instance;
    }

    public static Connection getConnection() {
        return mainConnection;
    }

    public static Connection getForumConnection() {
        return forumConnection;
    }


}

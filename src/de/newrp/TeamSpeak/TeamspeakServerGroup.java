package de.newrp.TeamSpeak;

public enum TeamspeakServerGroup {
    NICHT_REGISTRIERT(214),
    GEBANNT(243),
    MODERATOR(215),
    SUPPORTER(244),
    ADMINISTRATOR(213),
    NRP_SERVERTEAM(245),
    POLICE(229),
    GOVERNMENT(231),
    NEWS(240),
    VERIFIED(216),
    PREMIUM(219),
    SOCIALMEDIA(226),
    EVENTTEAM(227),
    BAUTEAM(225),
    EARLYACCESSTEAM(246),
    RETTUNGSDIENST(230);

    private final int groupID;

    TeamspeakServerGroup(int groupID) {
        this.groupID = groupID;
    }

    public int getGroupID() {
        return this.groupID;
    }

    public enum TeamspeakChannelGroup {
        GUEST(11),
        MEMBER(12),
        CHANNEL_ERSTELLER(16),
        LEADER(13),
        TEAMMITGLIED(15);

        private final int groupID;

        TeamspeakChannelGroup(int groupID) {
            this.groupID = groupID;
        }

        public int getGroupID() {
            return this.groupID;
        }
    }
}

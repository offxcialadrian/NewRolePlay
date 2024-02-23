package de.newrp.TeamSpeak;

public enum TeamspeakServerGroup {
    NICHT_REGISTRIERT(10),
    GEBANNT(39),
    MODERATOR(11),
    SUPPORTER(40),
    ADMINISTRATOR(9),
    NRP_SERVERTEAM(41),
    POLICE(25),
    GOVERNMENT(27),
    NEWS(36),
    VERIFIED(12),
    PREMIUM(15),
    SOCIALMEDIA(22),
    EVENTTEAM(23),
    BAUTEAM(21),
    EARLYACCESSTEAM(42),
    RETTUNGSDIENST(26);

    private final int groupID;

    TeamspeakServerGroup(int groupID) {
        this.groupID = groupID;
    }

    public int getGroupID() {
        return this.groupID;
    }

    public enum TeamspeakChannelGroup {
        GUEST(58),
        MEMBER(59),
        CHANNEL_ERSTELLER(63),
        LEADER(60),
        TEAMMITGLIED(62);

        private final int groupID;

        TeamspeakChannelGroup(int groupID) {
            this.groupID = groupID;
        }

        public int getGroupID() {
            return this.groupID;
        }
    }
}

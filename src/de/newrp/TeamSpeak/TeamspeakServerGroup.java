package de.newrp.TeamSpeak;

public enum TeamspeakServerGroup {
    NICHT_REGISTRIERT(8),
    GEBANNT(40),
    MODERATOR(9),
    SUPPORTER(41),
    ADMINISTRATOR(6),
    NRP_SERVERTEAM(9),
    POLICE(26),
    GOVERNMENT(27),
    NEWS(37),
    ZIVILIST(10),
    PREMIUM(15),
    SOCIALMEDIA(23),
    EVENTTEAM(24),
    BAUTEAM(22),
    RETTUNGSDIENST(30);

    private final int groupID;

    TeamspeakServerGroup(int groupID) {
        this.groupID = groupID;
    }

    public int getGroupID() {
        return this.groupID;
    }

    public enum TeamspeakChannelGroup {
        GUEST(8),
        MEMBER(9),
        CHANNEL_ERSTELLER(13),
        LEADER(10),
        VOLUNTEER(11),
        TEAMMITGLIED(12);

        private final int groupID;

        TeamspeakChannelGroup(int groupID) {
            this.groupID = groupID;
        }

        public int getGroupID() {
            return this.groupID;
        }
    }
}

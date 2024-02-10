package de.newrp.TeamSpeak;

public enum TeamspeakServerGroup {
    NICHT_REGISTRIERT(15),
    GEBANNT(45),
    MODERATOR(16),
    SUPPORTER(46),
    ADMINISTRATOR(14),
    NRP_SERVERTEAM(47),
    POLICE(31),
    GOVERNMENT(33),
    NEWS(42),
    VERIFIED(17),
    PREMIUM(20),
    SOCIALMEDIA(28),
    EVENTTEAM(29),
    BAUTEAM(27),
    EARLYACCESSTEAM(49),
    RETTUNGSDIENST(32);

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

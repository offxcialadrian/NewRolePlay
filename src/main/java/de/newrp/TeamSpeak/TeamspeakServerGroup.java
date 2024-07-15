package de.newrp.TeamSpeak;

public enum TeamspeakServerGroup {
    NICHT_REGISTRIERT(10),
    GEBANNT(39),
    MODERATOR(11),
    SUPPORTER(40),
    FRAKTIONSMANAGER(56),
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
    RETTUNGSDIENST(26),
    TRIORLA(43),
    FALCONE(46),
    CORLEONE(44),
    GROVE(48),
    BRATERSTWO(45),
    MIAMI_VIPERS(49),
    ENTWICKLUNG(50),
    DEV(53),
    CEO(54),
    BKA(55);

    private final int groupID;

    TeamspeakServerGroup(int groupID) {
        this.groupID = groupID;
    }

    public int getGroupID() {
        return this.groupID;
    }

    public enum TeamspeakChannelGroup {
        GUEST(10),
        MEMBER(11),
        CHANNEL_ERSTELLER(15),
        LEADER(12),
        TEAMMITGLIED(14);

        private final int groupID;

        TeamspeakChannelGroup(int groupID) {
            this.groupID = groupID;
        }

        public int getGroupID() {
            return this.groupID;
        }
    }
}

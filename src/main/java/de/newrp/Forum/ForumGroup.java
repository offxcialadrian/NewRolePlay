package de.newrp.Forum;

public enum ForumGroup {

    WCF_APC_GROUP_GROUP1(1, "wcf.acp.group.group1"),
    GAST(2, "Gäste"),
    ZIVILIST(3, "Zivilist"),
    ADMINISTRATOR(37, "Administratoren"),
    MODERATOR(5, "Moderator"),
    SUPPORTER(7, "Supporter"),
    DEVELOPER(36, "Developer"),
    OWNER(4, "Geschäftsführer"),
    POLICE(10, "Polizei"),
    POLICE_LEADER(11, "Polizei Leader"),
    RETTUNGSDIENST(12, "Rettungsdienst"),
    RETTUNGSDIENST_LEADER(13, "Rettungsdienst Leader"),
    GOVERNMENT(14, "Regierung"),
    GOVERNMENT_LEADER(15, "Regierung Leader"),
    NEWS(16, "News"),
    NEWS_LEADER(17, "News Leader"),
    VERIFIED(18, "Verifiziert"),
    TRIORLA(24, "Tri Orla"),
    TRIORLA_LEADER(19, "Tri Orla Leader"),
    FALCONE(25, "Falcone"),
    FALCONE_LEADER(22, "Falcone Leader"),
    CORLEONE(27, "Corleone"),
    CORLEONE_LEADER(21, "Corleone Leader"),
    BRATERSTWO(28, "Braterstwo"),
    BRATERSTWO_LEADER(20, "Braterstwo Leader"),
    GROVE(26, "Groves Street"),
    GROVE_LEADER(23, "Groves Street Leader"),
    SINOLA_MEMBER(29, "Sinola-Kartell Member"),
    SINOLA_LEADER(30, "Sinola-Kartell Leader"),
    BKA_MEMBER(39, "BKA Member"),
    BKA_LEADER(38, "BKA Leader");

    private final int id;
    private final String name;

    ForumGroup(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getID() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }
}

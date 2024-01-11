package de.newrp.Forum;

public enum ForumGroup {

    WCF_APC_GROUP_GROUP1(1, "wcf.acp.group.group1"),
    GAST(2, "Gäste"),
    ZIVILIST(3, "Zivilist"),
    ADMINISTRATOR(4, "Administratoren"),
    MODERATOR(5, "Moderator"),
    SUPPORTER(6, "Supporter"),
    POLICE(9, "Polizei"),
    POLICE_LEADER(15, "Polizei Leader"),
    RETTUNGSDIENST(11, "Rettungsdienst"),
    RETTUNGSDIENST_LEADER(13, "Rettungsdienst Leader"),
    GOVERNMENT(12, "Regierung"),
    GOVERNMENT_LEADER(14, "Regierung Leader"),
    NEWS(10, "News"),
    NEWS_LEADER(16, "News Leader"),
    VERIFIED(17, "Verifiziert");

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

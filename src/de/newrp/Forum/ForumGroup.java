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
    RETTUNGSDIENST(10, "Rettungsdienst"),
    RETTUNGSDIENST_LEADER(16, "Rettungsdienst Leader"),
    GOVERNMENT(45, "Regierung"),
    GOVERNMENT_LEADER(46, "Regierung Leader"),
    NEWS(21, "News"),
    NEWS_LEADER(22, "News Leader"),
    VERIFIED(50, "Verifiziert");

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

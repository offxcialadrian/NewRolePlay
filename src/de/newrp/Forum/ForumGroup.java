package de.newrp.Forum;

public enum ForumGroup {

    WCF_APC_GROUP_GROUP1(1, "wcf.acp.group.group1"),
    GAST(2, "Gäste"),
    ZIVILIST(3, "Zivilist"),
    ADMINISTRATOR(4, "Administratoren"),
    MODERATOR(5, "Moderator"),
    SUPPORTER(7, "Supporter"),
    POLICE(10, "Polizei"),
    POLICE_LEADER(11, "Polizei Leader"),
    RETTUNGSDIENST(12, "Rettungsdienst"),
    RETTUNGSDIENST_LEADER(13, "Rettungsdienst Leader"),
    GOVERNMENT(14, "Regierung"),
    GOVERNMENT_LEADER(15, "Regierung Leader"),
    NEWS(16, "News"),
    NEWS_LEADER(17, "News Leader"),
    VERIFIED(18, "Verifiziert");

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

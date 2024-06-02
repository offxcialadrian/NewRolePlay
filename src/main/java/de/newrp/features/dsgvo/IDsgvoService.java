package de.newrp.features.dsgvo;

public interface IDsgvoService {

    void openInventoryIfNotAccepted();

    void acceptDsgvo();

    void declineDsgvo();

}

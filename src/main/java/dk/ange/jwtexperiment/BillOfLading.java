package dk.ange.jwtexperiment;

import javax.persistence.*;
import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/*
 * A class representing a Bill of Lading as rendered to paper.
 */

@Entity
@Table(name = "billoflading")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class BillOfLading {

    @Id
    @Column
    public String documentHash;

    @Column
    public String blno;

    @Column
    public String shipper;

    @Column
    public String exportImportSvcRef;

    @Column
    public String forwardingAgentReferences;

    @Column
    public String consignee;

    @Column
    public String notifyParty;

    @Column
    public String alsoNotifyParty;

    @Column
    public String bookingNoCarrierRef;

    @Column
    public String vessel;

    @Column
    public String voyageNo;

    @Column
    public String portOfLoading;

    @Column
    public String portOfDischarge;

    @Column
    public String preCarriageBy;

    @Column
    public String placeOfReceipt;

    @Column
    public String placeOfDelivery;

    @Column
    public String onwardInlandRoutingFinalDestination;

    @Column
    public String containerNumbers;

    @Column
    public String sizeTypes;

    @Column
    public String sealNos;

    @Column
    public String descriptionOfGoods;

    @Column
    public String cargoGrossWeights;

    @Column
    public String receiptDeliveryType;

    @Column
    public String cargoMovementType;

    @Column
    public String isPartLoad;

    @Column
    public String hsCode;

    @Column
    public String reeferSettings;

    @Column
    public String pointAndCountryOfOriginOfGoods;

    @Column
    public String shipperDeclaredValue;

    @Column
    public String freightAndChargesPayableByAt;

    @Column
    public String freightAndCharges;

    @Column
    public String basis;

    @Column
    public String rate;

    @Column
    public String prepaid;

    @Column
    public String collect;

    @Column
    public String totalFreight;

    @Column
    public String carrierClauses;

    @Column
    public String totalNumberOfContainersReceived;

    @Column
    public String placeOfIssue;

    @Column
    public String shippedOnBoardDate;

    @Column
    public String dateOfIssue;

    @Column
    public String noAndSequenceOfOriginalBLs;
}

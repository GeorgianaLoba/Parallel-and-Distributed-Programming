package domain;

public class Operation {
    Integer serialNumber;
    Integer amount;
    Integer fromId;
    Integer toId;
    OperationType operationType;

    public Operation() {
    }

    public Operation(Integer serialNumber, Integer amount, Integer fromId, Integer toId) {
        this.serialNumber = serialNumber;
        this.amount = amount;
        this.fromId = fromId;
        this.toId = toId;
    }

    public Operation(Integer serialNumber, Integer amount, Integer fromId, Integer toId, OperationType operationType) {
        this.serialNumber = serialNumber;
        this.amount = amount;
        this.fromId = fromId;
        this.toId = toId;
        this.operationType = operationType;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public Integer getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(Integer serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getFromId() {
        return fromId;
    }

    public void setFromId(Integer fromId) {
        this.fromId = fromId;
    }

    public Integer getToId() {
        return toId;
    }

    public void setToId(Integer toId) {
        this.toId = toId;
    }

    @Override
    public String toString() {
        return "Operation{" +
                "serialNumber=" + serialNumber +
                ", amount=" + amount +
                ", fromId=" + fromId +
                ", toId=" + toId +
                ", operationType=" + operationType +
                '}';
    }
}

package se13.block;

enum BlockRotationStatus {

}

public abstract class Block {
    enum BlockRotationStatus {
        NONE,

    }

    protected int blockID;
    protected BlockPosition[][] cells;
    protected BlockPosition startBlockPosition;

    private BlockRotationStatus rotationStatus;
    private BlockPosition position;
}

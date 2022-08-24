package ca.teamdman.sfm.common.program;

public abstract class LimitedSlot<STACK, CAP, M extends ResourceMatcher<STACK>> {
    public final ResourceType<STACK, CAP> TYPE;
    public final CAP                      HANDLER;
    public final int                      SLOT;
    public final M                        MATCHER;
    private      boolean                  done = false;

    public LimitedSlot(CAP handler, ResourceType<STACK, CAP> type, int slot, M matcher) {
        this.TYPE    = type;
        this.HANDLER = handler;
        this.SLOT    = slot;
        this.MATCHER = matcher;
    }

    public boolean isDone() {
        return done || MATCHER.isDone();
    }

    protected void setDone() {
        this.done = true;
    }

    public STACK getStackInSlot() {
        return TYPE.getStackInSlot(HANDLER, SLOT);
    }

    public STACK extract(int amount, boolean simulate) {
        return TYPE.extract(HANDLER, SLOT, amount, simulate);
    }

    public STACK insert(STACK stack, boolean simulate) {
        return TYPE.insert(HANDLER, SLOT, stack, simulate);
    }

}

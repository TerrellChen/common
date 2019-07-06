package terrell.common.builder;

/**
 * @author Terrell Chen
 * @date 6/7/2019
 */
public abstract class AbstractObject<B> {
    private int var1;
    public abstract static class Builder<B extends Builder<B>> {
        private int var1;

        public B asBuilder() {
            return (B) this;
        }

        public int getVar1(){
            return var1;
        }

        public B withVar1(final int var1){
            this.var1 = var1;
            return asBuilder();
        }
    }

    protected AbstractObject(int var1){
        this.var1 = var1;
    }
}

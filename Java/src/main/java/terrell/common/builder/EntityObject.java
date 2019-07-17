package terrell.common.builder;

/**
 * @author Terrell Chen
 * @date 6/7/2019
 */
public class EntityObject extends AbstractObject {
    private int var2;

    public static abstract class AbstractBuilder<B extends AbstractBuilder<B>> extends AbstractObject.Builder<B> {
        private int var2;

        public int getVar2(){
            return var2;
        }

        public B withVar2(final int var2){
            this.var2 = var2;
            return asBuilder();
        }
    }

    public static class Builder extends AbstractBuilder<Builder>
            implements terrell.common.builder.Builder<EntityObject> {

        @Override
        public EntityObject build() {
            return new EntityObject(getVar1(), getVar2());
        }
    }

    public static Builder newBuilder(){
        return new Builder();
    }

    protected EntityObject(int var1, int var2){
        super(var1);
        this.var2 = var2;
    }
}

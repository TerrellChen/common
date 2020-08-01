package terrell.common.entity;
/**
 * @author: TerrellChen
 * @version: Created in 01:17 2020-08-01
 */

/**
 * Description:
 */
public class Bar {
    protected static FooInterface fooInterface = new FooInterface() {
        @Override
        public Object bar(int a, char b, String c) {
            return "Bar#Foo";
        }
    };

    public void Foo() {
        register(fooInterface);
    }


    private void register(FooInterface fooInterface) {

    }
}

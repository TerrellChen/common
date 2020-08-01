package terrell.common.entity;
/**
 * @author: TerrellChen
 * @version: Created in 01:13 2020-08-01
 */

/**
 * Description:
 */
public class Foo implements FooInterface {

    @Override
    public Object bar(int a, char b, String c) {
        return "Foo#bar";
    }

    protected int bar(int a, char b) {
        return 0;
    }

    private void bar(int a) {
    }


}

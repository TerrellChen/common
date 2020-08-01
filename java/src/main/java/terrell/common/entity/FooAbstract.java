package terrell.common.entity;
/**
 * @author: TerrellChen
 * @version: Created in 01:13 2020-08-01
 */

/**
 * Description:
 */
public abstract class FooAbstract implements FooInterface {
    abstract public Object bar(int a, char b, String c);

    abstract protected int bar(int a, char b);

}

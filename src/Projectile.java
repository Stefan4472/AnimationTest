/**
 * Created by Stefan on 8/31/2015.
 */
public abstract class Projectile extends Sprite {

    protected int damage;

    protected int getDamage() {
        return damage;
    }

    protected void setDamage(int damage) {
        this.damage = damage;
    }

    public Projectile(double x, double y) {
        super(x, y);
        initProjectile();
    }

    public Projectile(String imageName) {
        super(imageName);
        initProjectile();
    }

    public Projectile(String imageName, double x, double y) {
        super(imageName, x, y);
        initProjectile();
    }

    private void initProjectile() {
        damage = 0;
    }

}

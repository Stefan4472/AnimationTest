package plainsimple.spaceships;

/**
 * Created by Stefan on 9/26/2015.
 */
public class Alien2 extends Alien {

    public Alien2(String imageName, Board board) {
        super(imageName, board);
        initAlien();
    }

    public Alien2(String imageName, double x, double y, Board board) {
        super(imageName, x, y, board);
        initAlien();
    }

    private void initAlien() {
        startingY = y;
        hp = 40 + (int) board.getDifficulty() / 4;
        bulletDelay = 1_000 - board.getDifficulty();
        bulletSpeed = -3.0f - random.nextInt(5) / 5;
        hitBox.setOffsets(5, 5);
        hitBox.setDimensions(40, 40);
        damage = 100;
        speedX = -2.0f;
    }
    @Override
    void updateActions() {

    }

    @Override
    void updateSpeeds() {

    }

    @Override
    void fireBullet(Sprite s) {

    }
}
